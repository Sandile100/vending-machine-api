package co.za.vendingmachineapi.controller;

import co.za.vendingmachineapi.service.CashService;
import co.za.vendingmachineapi.service.PaymentService;
import co.za.vendingmachineapi.service.ProductService;
import co.za.vendingmachineapi.service.SalesService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.time.Instant;

@Controller
public class AdminController {

    private final ProductService productService;
    private final CashService cashService;
    private final SalesService salesService;

    public AdminController(ProductService productService, CashService cashService, SalesService salesService) {
        this.productService = productService;
        this.cashService = cashService;
        this.salesService = salesService;
    }

    @PostMapping("/admin/product")
    public void addNewProduct(@RequestBody final String productName){

    }

    @PutMapping ("/admin/product")
    public void updateProduct(@RequestBody final String productName){

    }

    @PutMapping ("/admin/cash")
    public void updateCash(@RequestBody final String cashInserted){

    }

    @GetMapping("/admin/sales")
    public void getSales(Instant startDate, Instant endDate) {

    }
}
