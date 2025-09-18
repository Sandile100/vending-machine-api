package co.za.vendingmachineapi.model;

public record PurchaseResponse(
        String message,
        Integer changeReturned,
        String productName
) {
}
