package fm.douban.service;

import fm.douban.model.Song;
import fm.douban.param.SongQueryParam;
import org.springframework.data.domain.Page;

/**
 * @author YangTao
 * @create 2022/2/24
 */
public interface SongService {
    // 增加一个歌曲
    Song add(Song song);
    // 根据id查询歌曲
    Song get(String songId);
    // 查询全部歌曲
    Page<Song> list(SongQueryParam songParam);
    // 修改一首歌
    boolean modify(Song song);
    // 删除一首歌
    boolean delete(String songId);

}
