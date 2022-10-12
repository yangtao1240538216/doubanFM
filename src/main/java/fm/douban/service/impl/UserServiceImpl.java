package fm.douban.service.impl;

import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import fm.douban.model.User;
import fm.douban.param.UserQueryParam;
import fm.douban.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.repository.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.function.LongSupplier;

/**
 * @author YangTao
 * @create 2022/4/3
 */
@Service
public class UserServiceImpl implements UserService {
    private static final Logger LOG = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private MongoTemplate  mongoTemplate;

    @Override
    public User add(User user) {
        // 作为服务要对入参进行判断，不能假设被调用时，传入的一定是对象
        if(user == null) {
            LOG.error("input User data is null.");
            return null;
        }
        return mongoTemplate.insert(user);
    }

    @Override
    public User get(String id) {
        if(!StringUtils.hasText(id)) {
            LOG.error("input id data is null.");
            return null;
        }
        User user = mongoTemplate.findById(id, User.class);
        return user;
    }

    @Override
    public Page<User> list(UserQueryParam param) {
        if(param == null) {
            LOG.error("input user data is not correct.");
            return null;
        }
        // 总条件
        Criteria criteria = new Criteria();
        // 可能有多个子条件
        List<Criteria> subCris = new ArrayList();
        if (StringUtils.hasText(param.getLoginName())) {
            subCris.add(Criteria.where("loginName").is(param.getLoginName()));
        }

        if (StringUtils.hasText(param.getPassword())) {
            subCris.add(Criteria.where("password").is(param.getPassword()));
        }

        if (StringUtils.hasText(param.getMobile())) {
            subCris.add(Criteria.where("mobile").is(param.getMobile()));
        }
        // 必须至少有一个查询条件
        if (subCris.isEmpty()) {
            LOG.error("input User query param is not correct.");
            return null;
        }
        // 三个子条件以 and 关键词连接成总条件对象，相当于 name='' and lyrics='' and subjectId=''
        criteria.andOperator(subCris.toArray(new Criteria[] {}));

        // 条件对象构建查询对象
        Query query = new Query(criteria);
        // 总数
        long count = mongoTemplate.count(query,User.class);
        // 构建分页对象，注意此对象页码是从0 开始计数的。
        Pageable pageable = PageRequest.of(param.getPageNum()-1, param.getPageSize() );
        query.with(pageable);
        List<User> users = mongoTemplate.find(query,User.class);
        Page<User> pageResult = PageableExecutionUtils.getPage(users, pageable, new LongSupplier() {
            @Override
            public long getAsLong() {
                return count;
            }
        });

        return pageResult;
    }

    @Override
    public boolean modify(User user) {
        // 作为服务，要对入参进行判断，不能假设被调用时，入参一定正确
        if (user == null || !StringUtils.hasText(user.getId())) {
            LOG.error("input User data is not correct.");
            return false;
        }
        // 设置唯一主键不可修改
        Query query = new Query(Criteria.where("id").is(user.getId()));

        Update updateData = new Update();
        if(user.getLoginName() != null) {
            updateData.set("loginName",user.getLoginName());
        }
        if(user.getPassword() != null) {
            updateData.set("password",user.getPassword());
        }
        if(user.getMobile() != null) {
            updateData.set("mobile",user.getMobile());
        }
        //把一条符合条件的记录，修改其字段
        UpdateResult result = mongoTemplate.updateFirst(query,updateData,User.class);

        return result != null && result.getModifiedCount() > 0;
    }

    @Override
    public boolean delete(String id) {
        if(!StringUtils.hasText(id)) {
            LOG.error(" input id is blank.");
            return false;
        }
        User user = new User();
        user.setId(id);
        DeleteResult result = mongoTemplate.remove(user);
        return result != null && result.getDeletedCount()>0;
    }
}
