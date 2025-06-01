package com.example.GreenBascket.controller;

import com.example.GreenBascket.dto.ProductRequestDTO;
import com.example.GreenBascket.dto.ProductResponseDTO;
import com.example.GreenBascket.service.ProductService;
import com.example.GreenBascket.service.ImageStorageService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final ImageStorageService imageStorageService;

    public ProductController(ProductService productService, ImageStorageService imageStorageService) {
        this.productService = productService;
        this.imageStorageService = imageStorageService;
    }

    // --- NEW OPTIONAL ENDPOINT FOR GET /api/products ---
    @GetMapping // Maps to /api/products
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        // You'll need to add a method like this in your ProductService
        // Example: List<ProductResponseDTO> products = productService.findAllProducts();
        // For now, let's return an empty list or throw an error if you don't intend this endpoint
        // Or, implement a method to fetch all products for public listing if that's your goal.
        return ResponseEntity.status(HttpStatus.NOT_IMPLEMENTED).build(); // Or an empty list: ResponseEntity.ok(Collections.emptyList());
    }
    // ----------------------------------------------------

    @PostMapping("/{farmerId}")
    public ResponseEntity<ProductResponseDTO> addProduct(
            @PathVariable String farmerId,
            @ModelAttribute ProductRequestDTO productRequest,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

        productRequest.setImageFile(imageFile);
        ProductResponseDTO newProduct = productService.addProduct(farmerId, productRequest);
        return new ResponseEntity<>(newProduct, HttpStatus.CREATED);
    }

    @GetMapping("/farmer/{farmerId}")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByFarmer(@PathVariable String farmerId) {
        List<ProductResponseDTO> products = productService.getProductsByFarmer(farmerId);
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/{farmerId}/{productId}")
    public ResponseEntity<Void> deleteProduct(
            @PathVariable String farmerId,
            @PathVariable Long productId) {
        productService.deleteProduct(farmerId, productId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{farmerId}/{productId}")
    public ResponseEntity<ProductResponseDTO> updateProduct(
            @PathVariable String farmerId,
            @PathVariable Long productId,
            @ModelAttribute ProductRequestDTO productRequest,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile) {

        productRequest.setImageFile(imageFile);
        ProductResponseDTO updatedProduct = productService.updateProduct(farmerId, productId, productRequest);
        return ResponseEntity.ok(updatedProduct);
    }

    @GetMapping("/images/{fileName}")
    public ResponseEntity<Resource> serveImage(@PathVariable String fileName) {
        try {
            Path filePath = imageStorageService.loadFile(fileName);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) {
                    contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;
                }
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                System.err.println("ProductController: Resource not found or not readable for path: " + filePath.toAbsolutePath().normalize()); // Added debug log here too
                return ResponseEntity.notFound().build();
            }
        } catch (IOException e) {
            System.err.println("Error serving image: " + fileName + " - " + e.getMessage()); // Log actual error
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}