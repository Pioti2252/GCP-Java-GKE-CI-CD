package pl.piotr.java_shop.order;

public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(Long productId, Integer requested, Integer available) {
        super("Insufficient stock for product id: " + productId +
                ". Requested: " + requested +
                ", available: " + available);
    }
}