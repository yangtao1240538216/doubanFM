package fm.douban.spider;

import com.alibaba.fastjson.JSON;
import fm.douban.model.Singer;
import fm.douban.model.Song;
import fm.douban.service.SingerService;
import fm.douban.service.SongService;
import fm.douban.service.SubjectService;
import fm.douban.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Component
public class SingerSongSpider {
    private static Logger logger = LoggerFactory.getLogger(SingerSongSpider.class);

    private static final String SONG_URL = "https://fm.douban.com/j/v2/artist/{0}/";

    private static final String HOST = "fm.douban.com";

    @Autowired
    private HttpUtil httpUtil;

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private SingerService singerService;

    @Autowired
    private SongService songService;

    @Autowired
    private SubjectSpider subjectSpider;

    // @PostConstruct
    public void init() {
        CompletableFuture.supplyAsync(this::doExcute).thenAccept(result -> {
            logger.error("spider end ...");
        });
    }

    public boolean doExcute() {
        getSongDataBySingers();
        return true;
    }

    private void getSongDataBySingers() {
        List<Singer> singers = singerService.getAll();
        if (singers == null || singers.isEmpty()) {
            return;
        }

        // 遍历每个歌手
        for (Singer singer : singers) {
            String singerId = singer.getId();
            String url = MessageFormat.format(SONG_URL, singerId);

            // 替换为自己使用浏览器开发者工具观察到的值
            String cookie = "_ga=GA1.2.1671310206.1613987321; __gads=ID=bd575f6f1a575c66-2250e934f7cf00e0:T=1642242205:RT=1642242205:S=ALNI_MakEvWfMXwb7j-PYEI6KxmrqpEmfw; bid=OpWnfuRJNvA; ll=\"118159\"; douban-fav-remind=1; viewed=\"23065902\"; __utma=30149280.1671310206.1613987321.1646268711.1648605493.6; __utmz=30149280.1648605493.6.2.utmcsr=fm.douban.com|utmccn=(referral)|utmcmd=referral|utmcct=/; _pk_ref.100001.f71f=[\"\",\"\",1648621562,\"https://learn.youkeda.com/\"]; _pk_id.100001.f71f=94d8195eb9a10774.1637464820.13.1648621562.1648611445.";
            Map<String, String> headerData = httpUtil.buildHeaderData(null, HOST, cookie);
            String content = httpUtil.getContent(url, headerData);

            if (!StringUtils.hasText(content)) {
                continue;
            }

            Map dataObj = null;

            try {
                dataObj = JSON.parseObject(content, Map.class);
            } catch (Exception e) {
                // 抛异常表示返回的内容不正确，不是正常的 json 格式，可能是网络或服务器出错了。
                logger.error("parse content to map error. ", e);
            }

            // 可能格式错误
            if (dataObj == null) {
                continue;
            }

            // 解析歌手的歌曲
            Map songlistData = (Map)dataObj.get("songlist");

            if (songlistData == null || songlistData.isEmpty()) {
                continue;
            }

            List<Map> songsData = (List<Map>)songlistData.get("songs");

            if (songsData == null || songsData.isEmpty()) {
                continue;
            }

            for (Map songObj : songsData) {
                Song song = subjectSpider.buildSong(songObj);
                subjectSpider.saveSong(song);
            }
        }
    }

}
