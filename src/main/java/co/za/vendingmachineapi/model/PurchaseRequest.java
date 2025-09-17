package co.za.vendingmachineapi.model;

import java.util.List;

public record PurchaseRequest(
         Integer productId,
         List<Integer> cashInserted
) {
}
