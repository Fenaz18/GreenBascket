package com.example.GreenBascket.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

// IMPORTANT: Use the correct import based on your Spring Boot version:
// For Spring Boot 2.x:
import jakarta.annotation.PostConstruct;// For Spring Boot 3.x (if you're using Java 17+ and newer Spring Boot):
// import jakarta.annotation.PostConstruct;


@Service
public class ImageStorageService {

    @Value("${upload.dir}")
    private String uploadDirConfig; // Renamed to avoid confusion with the Path object

    private Path rootLocation; // This will hold the absolute, normalized path to the uploads directory

    // This method runs automatically after the 'uploadDirConfig' value is injected
    @PostConstruct
    public void init() {
        try {
            // Resolve the configured relative path to an absolute, normalized path ONCE
            this.rootLocation = Paths.get(uploadDirConfig).toAbsolutePath().normalize();
            Files.createDirectories(rootLocation); // Ensure the directory exists
            // --- DEBUG LOG: Verify the root directory ---
            System.out.println("ImageStorageService initialized. Root upload directory: " + this.rootLocation);
            // ------------------------------------------
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
        Path filePath = this.rootLocation.resolve(uniqueFileName); // Use the pre-initialized rootLocation

        // --- DEBUG LOG: Verify the path where the file is being saved ---
        System.out.println("ImageStorageService: Storing file at: " + filePath.toAbsolutePath().normalize());
        // ---------------------------------------------------------------

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING); // Add REPLACE_EXISTING for safety
        return uniqueFileName;
    }

    public void deleteFile(String fileName) throws IOException {
        Path filePath = this.rootLocation.resolve(fileName); // Use the pre-initialized rootLocation
        // --- DEBUG LOG: Verify the path where the file is being deleted from ---
        System.out.println("ImageStorageService: Attempting to delete file from: " + filePath.toAbsolutePath().normalize());
        // ----------------------------------------------------------------------
        Files.deleteIfExists(filePath);
    }

    public Path loadFile(String fileName) {
        Path resolvedPath = this.rootLocation.resolve(fileName); // Use the pre-initialized rootLocation
        // --- DEBUG LOG: Verify the path where the file is being loaded from ---
        System.out.println("ImageStorageService: Attempting to load file from: " + resolvedPath.toAbsolutePath().normalize());
        // ---------------------------------------------------------------------
        return resolvedPath;
    }
}