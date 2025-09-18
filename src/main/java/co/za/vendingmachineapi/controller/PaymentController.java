package co.za.vendingmachineapi.controller;

import co.za.vendingmachineapi.model.PurchaseRequest;
import co.za.vendingmachineapi.model.PurchaseResponse;
import co.za.vendingmachineapi.service.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@CrossOrigin(origins = "http://localhost:3000")
@Controller
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/payment")
    public ResponseEntity<PurchaseResponse> purchaseProduct(@RequestBody final PurchaseRequest purchaseRequest) {
        PurchaseResponse response = paymentService.purchaseProduct(purchaseRequest.productId(), purchaseRequest.cashInserted());
        return ResponseEntity.ok(response);
    }
}
