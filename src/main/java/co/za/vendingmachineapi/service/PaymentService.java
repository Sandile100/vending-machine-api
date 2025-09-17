package co.za.vendingmachineapi.service;

import co.za.vendingmachineapi.entity.Cash;
import co.za.vendingmachineapi.entity.Product;
import co.za.vendingmachineapi.entity.Sale;
import co.za.vendingmachineapi.exception.ProductOutOfStockException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PaymentService {

    private final CashService cashService;
    private final ProductService productService;
    private final SalesService salesService;

    public PaymentService(CashService cashService, ProductService productService, SalesService salesService) {
        this.cashService = cashService;
        this.productService = productService;
        this.salesService = salesService;
    }

    @Transactional
    public void purchaseProduct(final Integer productId, List<Integer> paymentCash) {
        Optional<Product> product = productService.getProduct(productId);

        if (product.isEmpty()) {
            log.error("Product not found");
            throw new ProductOutOfStockException("Product not found");
        }

        int totalPayment = paymentCash.stream().mapToInt(i -> i).sum();
        int change = Math.toIntExact(totalPayment - product.get().getPrice().intValue());

        log.info("Update Available cash, add : {}", totalPayment);
        for (Integer paymentNote : paymentCash) {
            Cash cash = cashService.getCashByDenomination(paymentNote);
            cashService.addPaymentCash(cash, paymentCash);
        }
        log.info("Dispense change for the customer. Total change : {}", change);
        cashService.dispenseChange(change);
        log.info("Dispense purchased product  : {}", product.get().getName());
        productService.dispenseProduct(product.get());
        log.info("Update sales made");
        salesService.updateSales(updateSales(product.get()));
    }

    private Sale updateSales(final Product product) {
        Sale sale = new Sale();
        sale.setPrice(product.getPrice());
        sale.setProductid(product.getId());
        sale.setProductname(product.getName());
        return sale;
    }
}
