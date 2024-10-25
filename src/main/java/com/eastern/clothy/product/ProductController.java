package com.eastern.clothy.product;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@CrossOrigin("*")
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private static final String PRODUCT_IMAGES_DIR = "src/main/resources/products";

    @Autowired
    private ProductService productService;



    private void createFolder(){
        File directory = new File(PRODUCT_IMAGES_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }


    @PostMapping("/add")
    public ResponseEntity<Product> addProduct(
            @RequestParam("productName") String productName,
            @RequestParam("price") Double price,
            @RequestParam("category") String category,
            @RequestParam("imageFile") MultipartFile imageFile) {
        createFolder();
        try {
            String imageFileName = saveImageFile(imageFile);
            Product product = new Product(
                    null,
                    productName,
                    price,
                    category,
                    imageFileName);
            Product savedProduct = productService.save(product);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedProduct);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/images/{filename:.+}")
    public ResponseEntity<Resource> getImage(@PathVariable String filename) {
        try {
            File imageFile = new File(PRODUCT_IMAGES_DIR + "/" + filename);
            if (!imageFile.exists()) {
                return ResponseEntity.notFound().build();
            }

            Resource resource = new FileSystemResource(imageFile);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        Optional<Product> product = productService.findById(id);
        if (product.isPresent()) {
            deleteImageFile(product.get().getProductImageLink());
            productService.delete(id);
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @GetMapping("/getAll")
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.findAll();
        return ResponseEntity.ok(products);
    }

    @GetMapping("/get/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        Optional<Product> product = productService.findById(id);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/get/all/{category}")
    public ResponseEntity<List<Product>> getProductsByCategory(@PathVariable String category) {
        List<Product> products = productService.findByCategory(category);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/get/{name}")
    public ResponseEntity<List<Product>> getProductsByName(@PathVariable String name) {
        List<Product> products = productService.findByName(name);
        return ResponseEntity.ok(products);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Product> editProduct(
            @PathVariable Long id,
            @RequestParam("productName") String productName,
            @RequestParam("price") Double price,
            @RequestParam("category") String category,
            @RequestParam("imageFile") MultipartFile imageFile) {
        Optional<Product> existingProduct = productService.findById(id);
        if (existingProduct.isPresent()) {
            try {
                String imageFileName = saveImageFile(imageFile);
                Product updatedProduct = existingProduct.get();
                deleteImageFile(updatedProduct.getProductImageLink());
                updatedProduct.setProductName(productName);
                updatedProduct.setPrice(price);
                updatedProduct.setCategory(category);
                updatedProduct.setProductImageLink(imageFileName);
                productService.save(updatedProduct);
                return ResponseEntity.ok(updatedProduct);
            } catch (IOException e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
            }
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    // Helper Methods

    private String saveImageFile(MultipartFile imageFile) throws IOException {
        String fileName = System.currentTimeMillis() + "_" + imageFile.getOriginalFilename();
        Path filePath = Paths.get(PRODUCT_IMAGES_DIR, fileName);
        Files.copy(imageFile.getInputStream(), filePath);
        return fileName;
    }

    private void deleteImageFile(String imageFileName) {
        try {
            Path filePath = Paths.get(PRODUCT_IMAGES_DIR, imageFileName);
            Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

