package pl.piotr.java_shop.product;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class ProductServiceTest {

    private ProductRepository productRepository;
    private ProductService productService;

    @BeforeEach
    void setUp() {
        productRepository = Mockito.mock(ProductRepository.class);
        productService = new ProductService(productRepository);
    }

    @Test
    void shouldReturnAllProducts() {
        // given
        Product laptop = new Product(
                "Laptop Lenovo ThinkPad",
                "Business laptop",
                new BigDecimal("3999.99"),
                10
        );

        Product mouse = new Product(
                "Wireless Mouse",
                "Ergonomic mouse",
                new BigDecimal("129.99"),
                50
        );

        when(productRepository.findAll()).thenReturn(List.of(laptop, mouse));

        // when
        List<Product> products = productService.getAllProducts();

        // then
        assertThat(products).hasSize(2);
        assertThat(products.get(0).getName()).isEqualTo("Laptop Lenovo ThinkPad");
        assertThat(products.get(1).getName()).isEqualTo("Wireless Mouse");

        verify(productRepository).findAll();
    }

    @Test
    void shouldReturnProductById() {
        // given
        Product laptop = new Product(
                "Laptop Lenovo ThinkPad",
                "Business laptop",
                new BigDecimal("3999.99"),
                10
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(laptop));

        // when
        Product result = productService.getProductById(1L);

        // then
        assertThat(result.getName()).isEqualTo("Laptop Lenovo ThinkPad");
        assertThat(result.getPrice()).isEqualByComparingTo("3999.99");

        verify(productRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        // given
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> productService.getProductById(999L))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product not found with id: 999");

        verify(productRepository).findById(999L);
    }

    @Test
    void shouldCreateProduct() {
        // given
        CreateProductRequest request = new CreateProductRequest(
                "USB-C Hub",
                "Multiport adapter",
                new BigDecimal("199.99"),
                15
        );

        Product savedProduct = new Product(
                "USB-C Hub",
                "Multiport adapter",
                new BigDecimal("199.99"),
                15
        );

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // when
        Product result = productService.createProduct(request);

        // then
        assertThat(result.getName()).isEqualTo("USB-C Hub");
        assertThat(result.getPrice()).isEqualByComparingTo("199.99");
        assertThat(result.getStockQuantity()).isEqualTo(15);

        verify(productRepository).save(any(Product.class));
    }

    @Test
    void shouldUpdateProduct() {
        // given
        Product existingProduct = new Product(
                "Old name",
                "Old description",
                new BigDecimal("100.00"),
                5
        );

        UpdateProductRequest request = new UpdateProductRequest(
                "New name",
                "New description",
                new BigDecimal("200.00"),
                10
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(existingProduct));
        when(productRepository.save(existingProduct)).thenReturn(existingProduct);

        // when
        Product result = productService.updateProduct(1L, request);

        // then
        assertThat(result.getName()).isEqualTo("New name");
        assertThat(result.getDescription()).isEqualTo("New description");
        assertThat(result.getPrice()).isEqualByComparingTo("200.00");
        assertThat(result.getStockQuantity()).isEqualTo(10);

        verify(productRepository).findById(1L);
        verify(productRepository).save(existingProduct);
    }

    @Test
    void shouldDeleteProduct() {
        // given
        Product product = new Product(
                "USB-C Hub",
                "Multiport adapter",
                new BigDecimal("199.99"),
                15
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // when
        productService.deleteProduct(1L);

        // then
        verify(productRepository).findById(1L);
        verify(productRepository).delete(product);
    }
}