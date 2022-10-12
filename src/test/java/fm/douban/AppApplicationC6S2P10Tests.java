package fm.douban;

import fm.douban.app.control.MainControl;
import fm.douban.service.SubjectService;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.ResourceUtils;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.FileInputStream;
import java.nio.charset.Charset;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(SpringRunner.class)
@SpringBootTest
public class AppApplicationC6S2P10Tests {

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private MainControl mainControl;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private MockMvc mockMvc;

    /**
     * 所有测试方法执行之前执行该方法
     */
    @BeforeEach
    public void before() {
        //获取mockmvc对象实例
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    public void contextLoads() throws Exception {
        System.out.println("开始检查服务：");
        checkContent();
        checkClass();
        checkData();
        checkFile();
        System.out.println("检查完毕");
    }

    private void checkContent() throws Exception {
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.get("/index")).andExpect(
            MockMvcResultMatchers.status().isOk()).andReturn();

        String contentAsString = mvcResult.getResponse().getContentAsString();
        assertNotNull("页面内容不能为空", contentAsString);
        assertTrue("页面内容必须包含 从艺术家出发", contentAsString.contains("从艺术家出发"));
    }

    private void checkClass() throws Exception {

    }

    private void checkData() {

    }

    private void checkFile() {
        assertTrue("必须有 explore.html 文件", fileExist("templates/explore.html"));

        String exploreHtml = readFileContent("templates/explore.html");
        assertNotNull("explore.html 文件不能为空", exploreHtml);

        assertTrue("explore.html 中必须使用 th:fragment", exploreHtml.contains("th:fragment"));
        assertTrue("explore.html 中必须使用 th:each", exploreHtml.contains("th:each"));
        assertTrue("explore.html 中必须输出 Subject 对象的 name 属性", exploreHtml.contains(".name"));

    }

    public boolean fileExist(String subPath) {
        boolean result = false;
        try {
            File file = ResourceUtils.getFile("classpath:" + subPath);
            result = file.exists();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return result;
    }

    public String readFileContent(String subPath) {
        String content = null;
        try {
            FileInputStream fis = new FileInputStream(ResourceUtils.getFile("classpath:" + subPath));
            content = IOUtils.toString(fis, Charset.forName("utf-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return content;
    }

}
