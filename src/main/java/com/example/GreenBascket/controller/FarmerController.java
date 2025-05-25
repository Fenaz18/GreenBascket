package com.example.GreenBascket.controller;


import com.example.GreenBascket.dto.ProductRequestDTO;
import com.example.GreenBascket.dto.ProductResponseDTO;
import com.example.GreenBascket.security.UserPrincipal;
import com.example.GreenBascket.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


import java.util.List;

@RestController
@RequestMapping("/api/farmer")
@PreAuthorize("hasRole('FARMER')")
public class FarmerController {

    private final ProductService productService;

    public FarmerController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping("/product")
    public ResponseEntity<ProductResponseDTO> addProduct(@AuthenticationPrincipal UserPrincipal user,
                                                         @RequestBody ProductRequestDTO request) {
        String farmerId = user.getUserId(); // Now it works
        ProductResponseDTO response = productService.addProduct(farmerId, request);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }


    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDTO>> getOwnProducts(@AuthenticationPrincipal UserPrincipal user) {
        List<ProductResponseDTO> products = productService.getProductsByFarmer(user.getUserId());
        return ResponseEntity.ok(products);
    }

    @DeleteMapping("/product/{id}")
    public ResponseEntity<Void> deleteProduct(@AuthenticationPrincipal UserPrincipal user,
                                              @PathVariable Long id) {
        productService.deleteProduct(user.getUserId(), id);
        return ResponseEntity.noContent().build();
    }
}
