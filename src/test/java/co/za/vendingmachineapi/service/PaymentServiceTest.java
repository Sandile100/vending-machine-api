package co.za.vendingmachineapi.service;

import co.za.vendingmachineapi.entity.Cash;
import co.za.vendingmachineapi.entity.Product;
import co.za.vendingmachineapi.entity.Sale;
import co.za.vendingmachineapi.exception.ProductOutOfStockException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @Mock
    private CashService cashService;

    @Mock
    private ProductService productService;

    @Mock
    private SalesService salesService;

    @InjectMocks
    private PaymentService paymentService;

    private static Product product(Long id, String name, int qty, BigDecimal price) {
        Product p = new Product();
        p.setId(Math.toIntExact(id));
        p.setName(name);
        p.setQuantity(qty);
        p.setPrice(price);
        return p;
    }

    private static Cash cash(int denom, int qty) {
        Cash c = new Cash();
        c.setDenomination(denom);
        c.setQuantity(qty);
        return c;
    }

    @Test
    void purchaseProduct_happyPath_processesPayment_dispensesChange_and_updatesSales() {
        Product prod = product(1L, "Soda", 5, new BigDecimal("10"));
        List<Integer> paymentCash = Arrays.asList(5, 5, 2, 2); // total = 14, change = 4

        when(productService.getProduct(1)).thenReturn(prod);
        when(cashService.getCashByDenomination(5)).thenReturn(cash(5, 10));
        when(cashService.getCashByDenomination(2)).thenReturn(cash(2, 10));

        ArgumentCaptor<Sale> saleCaptor = ArgumentCaptor.forClass(Sale.class);

        paymentService.purchaseProduct(1, paymentCash);

        verify(cashService, times(1)).getCashByDenomination(5);
        verify(cashService, times(1)).getCashByDenomination(2);
        verify(cashService, times(2)).addPaymentCash(any(Cash.class), eq(paymentCash));
        verify(cashService, times(2)).addPaymentCash(any(Cash.class), eq(paymentCash)); // exactly twice (5 and 2)
        verify(cashService).dispenseChange(4);

        verify(productService).getProduct(1);
        verify(productService).dispenseProduct(prod);

        verify(salesService).updateSales(saleCaptor.capture());
        Sale sale = saleCaptor.getValue();
        org.assertj.core.api.Assertions.assertThat(sale.getProductid()).isEqualTo(prod.getId());
        org.assertj.core.api.Assertions.assertThat(sale.getProductname()).isEqualTo(prod.getName());
        org.assertj.core.api.Assertions.assertThat(sale.getPrice()).isEqualTo(prod.getPrice());

        verifyNoMoreInteractions(cashService, productService, salesService);
    }

    @Test
    void purchaseProduct_whenProductIsNull_throws_andDoesNotInteractFurther() {
        when(productService.getProduct(42)).thenReturn(null);

        assertThrows(ProductOutOfStockException.class,
                () -> paymentService.purchaseProduct(42, List.of(10, 10)));

        verify(productService).getProduct(42);
        verifyNoInteractions(cashService, salesService);
        verify(productService, never()).dispenseProduct(any());
    }

    @Test
    void purchaseProduct_callsGetCashAndAddPaymentOncePerDistinctNote() {
        Product prod = product(2L, "Chips", 3, new BigDecimal("7"));
        List<Integer> paymentCash = Arrays.asList(5, 5, 5, 2, 2); // distinct: 5, 2 -> change = (19 - 7) = 12

        when(productService.getProduct(2)).thenReturn(prod);
        when(cashService.getCashByDenomination(5)).thenReturn(cash(5, 10));
        when(cashService.getCashByDenomination(2)).thenReturn(cash(2, 10));

        paymentService.purchaseProduct(2, paymentCash);

        verify(cashService, times(1)).getCashByDenomination(5);
        verify(cashService, times(1)).getCashByDenomination(2);
        verify(cashService, times(2)).addPaymentCash(any(Cash.class), eq(paymentCash));
        verify(cashService).dispenseChange(12);
        verify(productService).dispenseProduct(prod);
        verify(salesService).updateSales(any(Sale.class));
        verifyNoMoreInteractions(cashService, productService, salesService);
    }
}