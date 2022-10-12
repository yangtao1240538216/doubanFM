package fm.douban.app.control;

import fm.douban.model.User;
import fm.douban.model.UserLoginInfo;
import fm.douban.param.UserQueryParam;
import fm.douban.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * @author YangTao
 * @create 2022/4/3
 */
@Controller
public class UserControl {
    private static final Logger LOG = LoggerFactory.getLogger(UserControl.class);

    @Autowired
    private UserService userService;

    @PostConstruct
    public void into() {
        LOG.info("UserControl 启动啦");
        LOG.info("userService 注入啦");
    }

    @GetMapping(path = "/login")
    public String loginPage(Model model) {
        return "login";
    }

    @PostMapping(path = "/authenticate")
    @ResponseBody
    public Map login(@RequestParam String name,@RequestParam String password,HttpServletRequest request,
                     HttpServletResponse response) {
        Map returnData = new HashMap();
        // 根据登录名查询用户
        User regedUser = getUserByLoginName(name);
        // 找不到此登录用户
        if(regedUser == null) {
            returnData.put("result", false);
            returnData.put("message", "userName not correct");
            return returnData;
        }
        if(regedUser.getPassword().equals(password)) {
            UserLoginInfo userLoginInfo = new UserLoginInfo();
            userLoginInfo.setUserId("123456789abcd");
            userLoginInfo.setUserName(name);
            // 获取HttpSession 对象
            HttpSession session = request.getSession();
            // 写入登录信息
            session.setAttribute("userLoginInfo", userLoginInfo);
            returnData.put("result", true);
            returnData.put("message", "login successfule");
        }else {
            returnData.put("result", false);
            returnData.put("message", "userName or password not correct");
        }
        return returnData;
    }

    @GetMapping(path = "/sign")
    public String signPage(Model model) {
        return "sign";
    }

    @PostMapping(path = "/register")
    @ResponseBody
    public Map registerAction(@RequestParam String name, @RequestParam String password, @RequestParam String mobile,
                              HttpServletResponse response, HttpServletRequest request) {
        Map returnData = new HashMap();
        // 判断登录名是否已经存在
        User regerdUser = getUserByLoginName(name);
        if (regerdUser != null) {
            returnData.put("result", false);
            returnData.put("message", "login name already exist");
            return returnData;
        }
        User user = new User();
        user.setLoginName(name);
        user.setPassword(password);
        user.setMobile(mobile);
        User newUser = userService.add(user);
        if (newUser != null && StringUtils.hasText(newUser.getId())) {
            returnData.put("result", true);
            returnData.put("message", "register successfule");
        } else {
            returnData.put("result", false);
            returnData.put("message", "register failed");
        }
        return returnData;
    }

    private User getUserByLoginName(String loginName) {
        User regedUser = null;
        UserQueryParam param = new UserQueryParam();
        param.setLoginName(loginName);
        Page<User> users = userService.list(param);

        // 如果登录名正确，只取第一个，要保证用户名不能重复
        if (users != null && users.getContent() != null && users.getContent().size() > 0) {
            regedUser = users.getContent().get(0);
        }
        return regedUser;
    }
}
