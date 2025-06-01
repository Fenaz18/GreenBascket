// src/main/java/com/example/GreenBascket/config/WebConfig.java (create this if it doesn't exist)
package com.example.GreenBascket.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${upload.dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Map requests to /images/** to the file system directory where images are stored
        // The "file:" prefix is crucial for accessing local file system paths
        // Resolve the absolute path to ensure it works correctly
        String absolutePath = new java.io.File(uploadDir).getAbsolutePath();
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + absolutePath + "/");
        // Example: If an image is stored as "unique-id.jpg" in "uploads/",
        // it can be accessed at "http://localhost:8080/images/unique-id.jpg"
    }
}