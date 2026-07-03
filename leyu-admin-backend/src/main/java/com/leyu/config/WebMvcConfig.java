package com.leyu.config;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.File;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.upload-path}")
    private String configuredUploadPath;

    private String uploadPath;

    @PostConstruct
    public void init() {
        // 将相对路径转换为绝对路径
        File path = new File(configuredUploadPath);
        if (!path.isAbsolute()) {
            String projectPath = System.getProperty("user.dir");
            uploadPath = new File(projectPath, configuredUploadPath).getAbsolutePath();
        } else {
            uploadPath = configuredUploadPath;
        }
        
        // 确保目录存在
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }
        
        System.out.println("静态资源映射路径: " + uploadPath);
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 确保路径以 / 结尾
        String location = uploadPath.endsWith(File.separator) ? uploadPath : uploadPath + File.separator;
        
        registry.addResourceHandler("/files/**")
                .addResourceLocations("file:" + location);
    }
}
