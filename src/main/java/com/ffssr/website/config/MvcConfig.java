package com.ffssr.website.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class MvcConfig implements WebMvcConfigurer {
    @Value("${uploadIMG.path}")
    private String uploadPathIMG;

    @Value("${uploadDOC.path}")
    private String uploadPathDOC;

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
        registry.addViewController("/").setViewName("/home");
        registry.addViewController("/login").setViewName("/login");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/img/**")
                .addResourceLocations( "file:///" + uploadPathIMG + "/" );
        registry.addResourceHandler("/doc/**")
                .addResourceLocations( "file:///" + uploadPathDOC + "/" );
    }



}
