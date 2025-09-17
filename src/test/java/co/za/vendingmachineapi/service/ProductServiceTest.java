package co.za.vendingmachineapi.service;

import static org.junit.jupiter.api.Assertions.*;

import co.za.vendingmachineapi.entity.Product;
import co.za.vendingmachineapi.exception.ProductOutOfStockException;
import co.za.vendingmachineapi.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    private static Product product(Long id, String name, int quantity, BigDecimal price) {
        Product p = new Product();
        p.setId(Math.toIntExact(id));
        p.setName(name);
        p.setQuantity(quantity);
        p.setPrice(price);
        return p;
    }

    @Test
    void getProduct_returnsEntity_whenFound() {
        Product expected = product(1L, "Soda", 5, new BigDecimal("12.50"));
        when(productRepository.findById(1L)).thenReturn(Optional.of(expected));

        Product actual = productService.getProduct(1);

        assertSame(expected, actual);
        verify(productRepository).findById(1L);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void getProduct_throwsNoSuchElement_whenNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(NoSuchElementException.class, () -> productService.getProduct(99));

        verify(productRepository).findById(99L);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void getAllProducts_delegatesToRepository() {
        List<Product> expected = Arrays.asList(
                product(1L, "Soda", 5, new BigDecimal("12.50")),
                product(2L, "Chips", 3, new BigDecimal("8.00"))
        );
        when(productRepository.findAll()).thenReturn(expected);

        List<Product> actual = productService.getAllProducts();

        assertEquals(expected, actual);
        verify(productRepository).findAll();
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void addProduct_savesEntity() {
        Product p = product(3L, "Candy", 10, new BigDecimal("5.00"));

        productService.addProduct(p);

        verify(productRepository).save(p);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void dispenseProduct_decrementsQuantityAndSaves_whenInStock() {
        Product p = product(4L, "Water", 2, new BigDecimal("6.00"));

        productService.dispenseProduct(p);

        assertEquals(1, p.getQuantity());
        verify(productRepository).save(p);
        verifyNoMoreInteractions(productRepository);
    }

    @Test
    void dispenseProduct_throws_whenOutOfStock() {
        Product p = product(5L, "Juice", 0, new BigDecimal("10.00"));

        assertThrows(ProductOutOfStockException.class, () -> productService.dispenseProduct(p));

        verifyNoInteractions(productRepository);
    }
}