package fm.douban.service.impl;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import fm.douban.model.Singer;
import fm.douban.service.SingerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;


/**
 * @author YangTao
 * @create 2021/11/27
 */
@Service
public class SingerServiceImpl implements SingerService {

    private static final Logger LOG = LoggerFactory.getLogger(SingerServiceImpl.class);

    private final MongoTemplate mongoTemplate;

    @Autowired
    public SingerServiceImpl(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public Singer addSinger(Singer singer) {
        // 作为服务，要对入参进行判断，不能假设被调用时，传入的一定是真正的对象
        if (singer == null) {
            LOG.error("input singer data is null.");
            return null;
        }
        if (singer.getGmtCreated() == null) {
            singer.setGmtCreated(LocalDateTime.now());
        }
        if (singer.getGmtModified() == null) {
            singer.setGmtModified(LocalDateTime.now());
        }
        return mongoTemplate.insert(singer);
    }

    @Override
    public Singer get(String singerId) {
        // 输入的主键 id 必须有文本，不能为空或全空格
        if (!StringUtils.hasText(singerId)) {
            LOG.error("input singer is blank.");
            return null;
        }

        return mongoTemplate.findById(singerId, Singer.class);
    }

    @Override
    public List<Singer> getAll() {
        return mongoTemplate.findAll(Singer.class);
    }

    @Override
    public boolean modify(Singer singer) {
        // 作为服务，要对入参进行判断，不能假设被调用时，入参一定正确
        if (singer == null || !StringUtils.hasText(singer.getId())) {
            LOG.error("input singer data is not correct.");
            return false;
        }
        // 主键不能修改，作为查询条件
        Query query = new Query(Criteria.where("id").is(singer.getId()));
        Update updateData = new Update();
        // 每次修改都更新 gmtModified 为当前时间
        updateData.set("gmtModified", LocalDateTime.now());
        if (singer.getName() == null) {
            updateData.set("name", singer.getName());
        }
        if (singer.getAvatar() == null) {
            updateData.set("avatar", singer.getAvatar());
        }
        if (singer.getHomepage() == null) {
            updateData.set("homepage", singer.getHomepage());
        }
        if (singer.getSimilarSingerIds() == null) {
            updateData.set("similarSingerIds", singer.getSimilarSingerIds());
        }
        UpdateResult result = mongoTemplate.updateFirst(query, updateData, Singer.class);
        System.out.println("修改的数据记录数量：" + result.getModifiedCount());
        return result != null && result.getModifiedCount() > 0;
    }

    @Override
    public boolean deleted(String singerId) {
        if (!StringUtils.hasText(singerId)) {
            LOG.error("input singer is blank.");
            return false;
        }
        Singer singer = new Singer();
        singer.setId(singerId);
        DeleteResult result = mongoTemplate.remove(singer);
        return result != null && result.getDeletedCount() > 0;
    }
}
