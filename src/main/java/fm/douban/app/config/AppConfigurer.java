package fm.douban.app.config;

import fm.douban.app.interceptor.UserInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author YangTao
 * @create 2022/4/3
 */
public class AppConfigurer implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new UserInterceptor())
                .addPathPatterns("/my")
                .addPathPatterns("/fav")
                .addPathPatterns("/share");
    }
}
