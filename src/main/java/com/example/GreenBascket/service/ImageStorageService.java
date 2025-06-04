package com.example.GreenBascket.service;

import jakarta.annotation.PostConstruct; // For Spring Boot 3.x (Jakarta EE)
// For Spring Boot 2.x, use: import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class ImageStorageService {

    @Value("${upload.dir}") // This value must be set in your application.properties/yml
    private String uploadDirConfig;

    private Path rootLocation;

    @PostConstruct
    public void init() {
        try {
            this.rootLocation = Paths.get(uploadDirConfig).toAbsolutePath().normalize();
            Files.createDirectories(rootLocation);
            System.out.println("ImageStorageService initialized. Root upload directory: " + this.rootLocation);
        } catch (IOException e) {
            throw new RuntimeException("Could not initialize storage location: " + uploadDirConfig, e);
        }
    }

    public String storeFile(MultipartFile file) throws IOException {
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String uniqueFileName = UUID.randomUUID().toString() + fileExtension;
        Path filePath = this.rootLocation.resolve(uniqueFileName);

        System.out.println("ImageStorageService: Storing file at: " + filePath.toAbsolutePath().normalize());

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);
        return uniqueFileName;
    }

    public void deleteFile(String fileName) throws IOException {
        Path filePath = this.rootLocation.resolve(fileName);
        System.out.println("ImageStorageService: Attempting to delete file from: " + filePath.toAbsolutePath().normalize());
        Files.deleteIfExists(filePath);
    }

    public Path loadFile(String fileName) {
        Path resolvedPath = this.rootLocation.resolve(fileName);
        System.out.println("ImageStorageService: Attempting to load file from: " + resolvedPath.toAbsolutePath().normalize());
        return resolvedPath;
    }
}