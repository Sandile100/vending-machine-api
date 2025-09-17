package co.za.vendingmachineapi.service;

import co.za.vendingmachineapi.entity.Product;
import co.za.vendingmachineapi.exception.ProductOutOfStockException;
import co.za.vendingmachineapi.repository.ProductRepository;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product getProduct(final Integer id) {
        return productRepository.findById(Long.valueOf(id)).get();
    }

    @Cacheable(value = "ProductCache", cacheManager = "myCacheManager", unless="#result == null")
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public void addProduct(Product product) {
        productRepository.save(product);
    }

    public void dispenseProduct(Product product) {
        if(product.getQuantity() == 0) {
            throw new ProductOutOfStockException("No more stock available for this product");
        }
        product.setQuantity(product.getQuantity() - 1);
        productRepository.save(product);
    }
}
