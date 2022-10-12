package fm.douban.app.control;

import fm.douban.model.Subject;
import fm.douban.service.SubjectService;
import fm.douban.util.SubjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * @author YangTao
 * @create 2022/2/28
 */
@RequestMapping("test/subject")
@RestController
public class SubjectTestControl {
    private static final Logger LOG = LoggerFactory.getLogger(SubjectTestControl.class);

    private SubjectService subjectService;
    @Autowired
    private SubjectTestControl(SubjectService subjectService) {
        this.subjectService =subjectService;
    }
    @GetMapping(path="/add")
    Subject testAdd(){
        Subject subject = new Subject();
        subject.setId("0");
        subject.setGmtCreated(LocalDateTime.now());
        subject.setGmtModified(LocalDateTime.now());
        subject.setCover("https://5b0988e595225.cdn.sohucs.com/images/20170724/ad28da0d658b43aba84ce91df9cacdad.jpeg");
        subject.setName("测试主题赫兹");
        subject.setDescription("测试主题赫兹");
        subject.setMaster("0");
        subject.setSubjectType(SubjectUtil.TYPE_MHZ);
        subject.setSubjectSubType(SubjectUtil.TYPE_SUB_ARTIST);
        subject.setSongIds(Arrays.asList("0"));
        subject.setPublishedDate(LocalDate.now());
        Subject addedSong = subjectService.addSubject(subject);
        return  addedSong;
    }
    @GetMapping(path="/get")
    Subject testGet(){
        return subjectService.get("0");
    }
    @GetMapping(path = "/getByType")
    List<Subject> testGetByType(){
        return subjectService.getSubjects(SubjectUtil.TYPE_MHZ);
    }
    @GetMapping(path = "/getBySubType")
    List<Subject> testGetBySubType(){

        return subjectService.getSubjects(SubjectUtil.TYPE_MHZ, SubjectUtil.TYPE_SUB_ARTIST);
    }
    @GetMapping(path = "/del")
    boolean testDelete(){
        return subjectService.delete("0");
    }
}
