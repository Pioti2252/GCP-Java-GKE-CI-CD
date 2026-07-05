package pl.piotr.java_shop.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import pl.piotr.java_shop.exception.GlobalExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProductController.class)
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ProductService productService;

    @Test
    void shouldReturnAllProducts() throws Exception {
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

        when(productService.getAllProducts()).thenReturn(List.of(laptop, mouse));

        // when / then
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].name").value("Laptop Lenovo ThinkPad"))
                .andExpect(jsonPath("$[0].price").value(3999.99))
                .andExpect(jsonPath("$[1].name").value("Wireless Mouse"));

        verify(productService).getAllProducts();
    }

    @Test
    void shouldReturnProductById() throws Exception {
        // given
        Product laptop = new Product(
                "Laptop Lenovo ThinkPad",
                "Business laptop",
                new BigDecimal("3999.99"),
                10
        );

        when(productService.getProductById(1L)).thenReturn(laptop);

        // when / then
        mockMvc.perform(get("/api/products/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Laptop Lenovo ThinkPad"))
                .andExpect(jsonPath("$.description").value("Business laptop"))
                .andExpect(jsonPath("$.price").value(3999.99))
                .andExpect(jsonPath("$.stockQuantity").value(10));

        verify(productService).getProductById(1L);
    }

    @Test
    void shouldReturn404WhenProductNotFound() throws Exception {
        // given
        when(productService.getProductById(999L))
                .thenThrow(new ProductNotFoundException(999L));

        // when / then
        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Product not found with id: 999"))
                .andExpect(jsonPath("$.path").value("/api/products/999"));

        verify(productService).getProductById(999L);
    }

    @Test
    void shouldCreateProduct() throws Exception {
        // given
        CreateProductRequest request = new CreateProductRequest(
                "USB-C Hub",
                "Multiport adapter",
                new BigDecimal("199.99"),
                15
        );

        Product createdProduct = new Product(
                "USB-C Hub",
                "Multiport adapter",
                new BigDecimal("199.99"),
                15
        );

        when(productService.createProduct(any(CreateProductRequest.class)))
                .thenReturn(createdProduct);

        // when / then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("USB-C Hub"))
                .andExpect(jsonPath("$.description").value("Multiport adapter"))
                .andExpect(jsonPath("$.price").value(199.99))
                .andExpect(jsonPath("$.stockQuantity").value(15));

        verify(productService).createProduct(any(CreateProductRequest.class));
    }

    @Test
    void shouldReturn400WhenCreateProductRequestIsInvalid() throws Exception {
        // given
        String invalidRequest = """
                {
                  "name": "",
                  "description": "Invalid product",
                  "price": 0,
                  "stockQuantity": -5
                }
                """;

        // when / then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.path").value("/api/products"));

        verify(productService, never()).createProduct(any(CreateProductRequest.class));
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        // given
        UpdateProductRequest request = new UpdateProductRequest(
                "USB-C Hub Pro",
                "Updated adapter",
                new BigDecimal("249.99"),
                20
        );

        Product updatedProduct = new Product(
                "USB-C Hub Pro",
                "Updated adapter",
                new BigDecimal("249.99"),
                20
        );

        when(productService.updateProduct(eq(1L), any(UpdateProductRequest.class)))
                .thenReturn(updatedProduct);

        // when / then
        mockMvc.perform(put("/api/products/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("USB-C Hub Pro"))
                .andExpect(jsonPath("$.price").value(249.99))
                .andExpect(jsonPath("$.stockQuantity").value(20));

        verify(productService).updateProduct(eq(1L), any(UpdateProductRequest.class));
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        // given
        doNothing().when(productService).deleteProduct(1L);

        // when / then
        mockMvc.perform(delete("/api/products/1"))
                .andExpect(status().isNoContent());

        verify(productService).deleteProduct(1L);
    }
}