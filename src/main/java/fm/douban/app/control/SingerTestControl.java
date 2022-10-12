package fm.douban.app.control;

import fm.douban.model.Singer;
import fm.douban.service.SingerService;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;


/**
 * @author YangTao
 * @create 2021/11/27
 */
@RestController
@RequestMapping("/test/singer")
public class SingerTestControl {
    private static final Logger LOG = LoggerFactory.getLogger(SingerService.class);

    @Autowired
    private SingerService singerService;

    @GetMapping(path="/add")
    public Singer testAddSinger() {
        Singer singer = new Singer();
        singer.setId("0");
        singer.setGmtCreated(LocalDateTime.now());
        singer.setGmtModified(LocalDateTime.now());
        singer.setAvatar("https://5b0988e595225.cdn.sohucs.com/images/20170724/ad28da0d658b43aba84ce91df9cacdad.jpeg");
        singer.setName("测试歌手");
        singer.setHomepage("https://www.baidu.com/");
        return singerService.addSinger(singer);
    }
    @GetMapping(path = "/getAll")
    public List<Singer> testGetAll() {
        return singerService.getAll();
    }
    @GetMapping(path = "/getOne")
    public Singer testGetSinger() {
        return singerService.get("0");
    }
    @GetMapping(path = "/modify")
    public boolean testModifySinger(Singer singer) {
        Singer singer1 = new Singer();
        singer1.setName("测试歌手1");
        return singerService.modify(singer1);
    }
    @GetMapping(path = "/del")
    public boolean testDelSinger() {
        return singerService.deleted("0");
    }


}
