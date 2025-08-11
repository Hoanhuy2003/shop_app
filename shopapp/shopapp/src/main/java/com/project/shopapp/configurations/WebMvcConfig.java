package com.project.shopapp.configurations;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map URL /api/v1/products/images/** đến folder D:/BanHang/ShopApp/uploads/
        registry.addResourceHandler("/api/v1/products/images/**")
                .addResourceLocations("file:/D:/BanHang/ShopApp/uploads/");
    }
}
