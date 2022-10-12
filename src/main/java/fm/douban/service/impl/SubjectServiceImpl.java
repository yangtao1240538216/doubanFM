package fm.douban.service.impl;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import fm.douban.model.Subject;
import fm.douban.service.SongService;
import fm.douban.service.SubjectService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.w3c.dom.ls.LSException;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author YangTao
 * @create 2022/2/26
 */
@Service
public class SubjectServiceImpl implements SubjectService {
    private static final Logger LOG = LoggerFactory.getLogger(SubjectServiceImpl.class);

    private final MongoTemplate mongoTemplate;

    @Autowired
    public SubjectServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Autowired
    private SongService songService;


    @Override
    public Subject addSubject(Subject subject) {
        if (subject == null) {
            LOG.error("input subject data is null.");
            return null;
        }
        if (subject.getGmtCreated() == null) {
            subject.setGmtCreated(LocalDateTime.now());
        }
        if (subject.getGmtModified() == null) {
            subject.setGmtModified(LocalDateTime.now());
        }
        return mongoTemplate.insert(subject);
    }

    @Override
    public boolean modify(Subject subject) {
        if (subject == null || !StringUtils.hasText(subject.getId())) {
            LOG.error("Input song data is not correct.");
            return false;
        }
        Query query = new Query(Criteria.where("id").is(subject.getId()));
        Update updateData = new Update();
        updateData.set("getModified", LocalDateTime.now());
        if (subject.getSongIds() != null) {
            updateData.set("songIds", subject.getSongIds());
        }
        UpdateResult result = mongoTemplate.updateFirst(query, updateData, Subject.class);
        return result != null && result.getModifiedCount() > 0;
    }

    @Override
    public Subject get(String subjectId) {
        if (!StringUtils.hasText(subjectId)) {
            LOG.error("input subject is blank.");
            return null;
        }
        return mongoTemplate.findById(subjectId, Subject.class);
    }

    @Override
    public boolean delete(String subjectId) {
        if (!StringUtils.hasText(subjectId)) {
            LOG.error("input subject data is blank.");
            return false;
        }
        Subject subject = new Subject();
        subject.setId(subjectId);
        DeleteResult result = mongoTemplate.remove(subject);
        return result != null && result.getDeletedCount() > 0;
    }


    @Override
    public List<Subject> getSubjects(String type) {
        return getSubjects(type, null);
    }

    @Override
    public List<Subject> getSubjects(String type, String subType) {
        Subject subjectParam = new Subject();
        subjectParam.setSubjectType(type);
        subjectParam.setSubjectSubType(subType);
        return getSubjects(subjectParam);
    }

    @Override
    public List<Subject> getSubjects(Subject subjectParam) {
        if(subjectParam == null) {
            LOG.error("input subjectParam is not correct.");
            return null;
        }
        String type = subjectParam.getSubjectType();
        String subType = subjectParam.getSubjectSubType();
        String master = subjectParam.getMaster();

        if(!StringUtils.hasText(type)) {
            LOG.error("input type is not correct.");
            return null;
        }
        Criteria criteria = new Criteria();
        List<Criteria> subCris = new ArrayList<>();
        subCris.add(Criteria.where("subjectType").is(type));
        if(StringUtils.hasText(subType)) {
            subCris.add(Criteria.where("subjectSubType").is(subType));
        }
        if(StringUtils.hasText(master)){
            subCris.add(Criteria.where(("master")).is(master));
        }
        criteria.andOperator(subCris.toArray(new Criteria[] {}));

        Query query = new Query(criteria);

        List<Subject> subjects = mongoTemplate.find(query,Subject.class);
        return subjects;
    }
}
