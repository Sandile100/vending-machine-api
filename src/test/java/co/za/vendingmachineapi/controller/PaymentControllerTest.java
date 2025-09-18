package co.za.vendingmachineapi.controller;


import co.za.vendingmachineapi.exception.ProductOutOfStockException;
import co.za.vendingmachineapi.model.PurchaseResponse;
import co.za.vendingmachineapi.service.PaymentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@ExtendWith(MockitoExtension.class)
class PaymentControllerTest {

    @Mock
    private PaymentService paymentService;

    private PaymentController controller;

    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        controller = new PaymentController(paymentService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
    }

    @Test
    void purchaseProduct_shouldReturn200_andInvokeService() throws Exception {
        String body = """
                {
                  "productId": 1,
                  "cashInserted": [5,5,2]
                }
                """;
        // Return a PaymentResponse instance from the service to match controller's return type
        when(paymentService.purchaseProduct(anyInt(), anyList()))
                .thenReturn(mock(PurchaseResponse.class));

        var result = mockMvc.perform(post("/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getStatus()).isEqualTo(200);

        ArgumentCaptor<Integer> idCaptor = ArgumentCaptor.forClass(Integer.class);
        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Integer>> cashCaptor = ArgumentCaptor.forClass(List.class);

        verify(paymentService).purchaseProduct(idCaptor.capture(), cashCaptor.capture());
        verifyNoMoreInteractions(paymentService);

        assertThat(idCaptor.getValue()).isEqualTo(1);
        assertThat(cashCaptor.getValue()).containsExactly(5, 5, 2);
    }

    @Test
    void purchaseProduct_whenServiceThrows_shouldReturn404() throws Exception {
        doThrow(new ProductOutOfStockException("Product not found"))
                .when(paymentService).purchaseProduct(eq(99), anyList());

        String body = """
                {
                  "productId": 99,
                  "cashInserted": [10]
                }
                """;

        mockMvc.perform(post("/payment")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().is4xxClientError());

        verify(paymentService).purchaseProduct(eq(99), eq(List.of(10)));
        verifyNoMoreInteractions(paymentService);
    }
}
