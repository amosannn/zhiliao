package com.amosannn.configuration;

import com.amosannn.interceptor.CorsInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.config.annotation.InterceptorRegistration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

@Component
public class InterceptorConfiguration extends WebMvcConfigurerAdapter {

  @Autowired
  CorsInterceptor corsInterceptor;

  @Override
  public void addInterceptors(InterceptorRegistry registry) {

    // 跨域拦截器
    registry.addInterceptor(corsInterceptor);
    // 配置拦截的路径
//    ir.addPathPatterns("/**");
    // 配置不拦截的路径
//    ir.excludePathPatterns("/**.html");

    super.addInterceptors(registry);
  }

}
