package fm.douban.model;

import java.util.List;

/**
 * @author YangTao
 * @create 2022/4/4
 */
public class CollectionViewModel {
    private Subject subject;
    private Singer singer;
    private List<Song> songs;

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Singer getSinger() {
        return singer;
    }

    public void setSinger(Singer singer) {
        this.singer = singer;
    }

    public List<Song> getSongs() {
        return songs;
    }

    public void setSongs(List<Song> songs) {
        this.songs = songs;
    }
}
