package co.za.vendingmachineapi.controller;

import co.za.vendingmachineapi.entity.Product;
import co.za.vendingmachineapi.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllProducts());
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProduct(@PathVariable String id) {
        return  ResponseEntity.status(HttpStatus.OK).body(productService.getProduct(Integer.valueOf(id)));
    }

    @PostMapping("products")
    public ResponseEntity<String> addProduct(Product product) {
        productService.addProduct(product);
        return ResponseEntity.ok("Product added successfully");
    }
}
