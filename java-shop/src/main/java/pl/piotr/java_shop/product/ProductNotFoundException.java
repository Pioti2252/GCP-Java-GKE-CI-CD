package pl.piotr.java_shop.product;

public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(Long id) {
        super("Product not found with id: " + id);
    }
}
