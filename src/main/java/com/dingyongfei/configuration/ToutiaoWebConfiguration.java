package com.dingyongfei.configuration;

import com.dingyongfei.interceptor.LoginRequiredInterceptor;
import com.dingyongfei.interceptor.PassportInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

/**
 * @Author: Ding Yongfei
 * @Date: 2019/1/13
 */
@Component
public class ToutiaoWebConfiguration extends WebMvcConfigurerAdapter {
    @Autowired
    PassportInterceptor passportInterceptor;

    @Autowired
    LoginRequiredInterceptor loginRequiredInterceptor;


    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(passportInterceptor);
        registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/setting*");
        registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/msg/detail*");
        registry.addInterceptor(loginRequiredInterceptor).addPathPatterns("/msg/list*");
        super.addInterceptors(registry);
    }
}
