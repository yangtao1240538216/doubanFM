package fm.douban.model;

import java.util.List;

/**
 * @author YangTao
 * @create 2022/4/1
 */
public class MhzViewModel {
    private String title;
    private List<Subject> subjects;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public List<Subject> getSubjects() {
        return subjects;
    }

    public void setSubjects(List<Subject> subjects) {
        this.subjects = subjects;
    }
}
