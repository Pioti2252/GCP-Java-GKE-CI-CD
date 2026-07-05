package pl.piotr.java_shop.product;

import org.springframework.data.jpa.repository.JpaRepository;
import pl.piotr.java_shop.product.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {
}