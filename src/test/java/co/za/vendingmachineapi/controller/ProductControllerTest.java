package co.za.vendingmachineapi.controller;

import co.za.vendingmachineapi.entity.Product;
import co.za.vendingmachineapi.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    @Mock
    private ProductService productService;

    private ProductController controller;
    private MockMvc mockMvc;

    @BeforeEach
    void setup() {
        controller = new ProductController(productService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    private static Product product(Long id, String name, int qty, BigDecimal price) {
        Product p = new Product();
        p.setId(Math.toIntExact(id));
        p.setName(name);
        p.setQuantity(qty);
        p.setPrice(price);
        return p;
    }

    @Test
    void getAllProducts_shouldReturnOk_withJsonArray() throws Exception {
        List<Product> products = Arrays.asList(
                product(1L, "Soda", 5, new BigDecimal("12.50")),
                product(2L, "Chips", 3, new BigDecimal("8.00"))
        );
        when(productService.getAllProducts()).thenReturn(products);

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id", is(1)))
                .andExpect(jsonPath("$[0].name", is("Soda")))
                .andExpect(jsonPath("$[0].quantity", is(5)))
                .andExpect(jsonPath("$[0].price", is(12.50)))
                .andExpect(jsonPath("$[1].id", is(2)))
                .andExpect(jsonPath("$[1].name", is("Chips")));

        verify(productService).getAllProducts();
        verifyNoMoreInteractions(productService);
    }

    @Test
    void getProduct_shouldReturnOk_withSingleProduct() throws Exception {
        Product prod = product(10L, "Water", 7, new BigDecimal("6.00"));
        when(productService.getProduct(10)).thenReturn(prod);

        mockMvc.perform(get("/products/{id}", "10"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(10)))
                .andExpect(jsonPath("$.name", is("Water")))
                .andExpect(jsonPath("$.quantity", is(7)))
                .andExpect(jsonPath("$.price", is(6.00)));

        verify(productService).getProduct(10);
        verifyNoMoreInteractions(productService);
    }

    @Test
    void addProduct_shouldBindFormParams_callService_andReturnOk() throws Exception {
        // Using form parameters because controller method does not use @RequestBody
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                        .param("name", "Candy")
                        .param("quantity", "9")
                        .param("price", "5.50"))
                .andExpect(status().isOk())
                .andExpect(content().string("Product added successfully"));

        ArgumentCaptor<Product> productCaptor = ArgumentCaptor.forClass(Product.class);
        verify(productService).addProduct(productCaptor.capture());
        Product saved = productCaptor.getValue();
        org.assertj.core.api.Assertions.assertThat(saved.getName()).isEqualTo("Candy");
        org.assertj.core.api.Assertions.assertThat(saved.getQuantity()).isEqualTo(9);
        org.assertj.core.api.Assertions.assertThat(saved.getPrice()).isEqualByComparingTo(new BigDecimal("5.50"));

        verifyNoMoreInteractions(productService);
    }
}