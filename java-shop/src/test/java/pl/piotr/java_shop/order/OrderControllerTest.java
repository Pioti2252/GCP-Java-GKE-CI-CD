package pl.piotr.java_shop.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import pl.piotr.java_shop.exception.GlobalExceptionHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import pl.piotr.java_shop.product.Product;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(OrderController.class)
@Import(GlobalExceptionHandler.class)
class OrderControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private OrderService orderService;

    @Test
    void shouldReturnAllOrders() throws Exception {
        // given
        Order order = new Order("test@example.com");
        order.setTotalAmount(new BigDecimal("7999.98"));

        Product product = new Product(
                "Laptop Lenovo ThinkPad",
                "Business laptop",
                new BigDecimal("3999.99"),
                8
        );

        OrderItem orderItem = new OrderItem(product, 2);
        order.addItem(orderItem);

        when(orderService.getAllOrders()).thenReturn(List.of(order));

        // when / then
        mockMvc.perform(get("/api/orders"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].customerEmail").value("test@example.com"))
                .andExpect(jsonPath("$[0].status").value("CREATED"))
                .andExpect(jsonPath("$[0].totalAmount").value(7999.98))
                .andExpect(jsonPath("$[0].items.length()").value(1))
                .andExpect(jsonPath("$[0].items[0].productName").value("Laptop Lenovo ThinkPad"))
                .andExpect(jsonPath("$[0].items[0].quantity").value(2));

        verify(orderService).getAllOrders();
    }

    @Test
    void shouldReturnOrderById() throws Exception {
        // given
        Order order = new Order("test@example.com");
        order.setTotalAmount(new BigDecimal("3999.99"));

        Product product = new Product(
                "Laptop Lenovo ThinkPad",
                "Business laptop",
                new BigDecimal("3999.99"),
                9
        );

        OrderItem orderItem = new OrderItem(product, 1);
        order.addItem(orderItem);

        when(orderService.getOrderById(1L)).thenReturn(order);

        // when / then
        mockMvc.perform(get("/api/orders/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerEmail").value("test@example.com"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.totalAmount").value(3999.99))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].productName").value("Laptop Lenovo ThinkPad"))
                .andExpect(jsonPath("$.items[0].quantity").value(1));

        verify(orderService).getOrderById(1L);
    }

    @Test
    void shouldReturn404WhenOrderNotFound() throws Exception {
        // given
        when(orderService.getOrderById(999L))
                .thenThrow(new OrderNotFoundException(999L));

        // when / then
        mockMvc.perform(get("/api/orders/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Not Found"))
                .andExpect(jsonPath("$.message").value("Order not found with id: 999"))
                .andExpect(jsonPath("$.path").value("/api/orders/999"));

        verify(orderService).getOrderById(999L);
    }

    @Test
    void shouldCreateOrder() throws Exception {
        // given
        CreateOrderRequest request = new CreateOrderRequest(
                "test@example.com",
                List.of(
                        new CreateOrderItemRequest(1L, 2)
                )
        );

        Order createdOrder = new Order("test@example.com");
        createdOrder.setTotalAmount(new BigDecimal("7999.98"));

        Product product = new Product(
                "Laptop Lenovo ThinkPad",
                "Business laptop",
                new BigDecimal("3999.99"),
                8
        );

        OrderItem orderItem = new OrderItem(product, 2);
        createdOrder.addItem(orderItem);

        when(orderService.createOrder(any(CreateOrderRequest.class)))
                .thenReturn(createdOrder);

        // when / then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.customerEmail").value("test@example.com"))
                .andExpect(jsonPath("$.status").value("CREATED"))
                .andExpect(jsonPath("$.totalAmount").value(7999.98))
                .andExpect(jsonPath("$.items.length()").value(1))
                .andExpect(jsonPath("$.items[0].productName").value("Laptop Lenovo ThinkPad"))
                .andExpect(jsonPath("$.items[0].quantity").value(2));

        verify(orderService).createOrder(any(CreateOrderRequest.class));
    }

    @Test
    void shouldReturn400WhenCreateOrderRequestIsInvalid() throws Exception {
        // given
        String invalidRequest = """
                {
                  "customerEmail": "wrong-email",
                  "items": []
                }
                """;

        // when / then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidRequest))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.path").value("/api/orders"));

        verify(orderService, never()).createOrder(any(CreateOrderRequest.class));
    }

    @Test
    void shouldReturn400WhenStockIsInsufficient() throws Exception {
        // given
        CreateOrderRequest request = new CreateOrderRequest(
                "test@example.com",
                List.of(
                        new CreateOrderItemRequest(1L, 999)
                )
        );

        when(orderService.createOrder(any(CreateOrderRequest.class)))
                .thenThrow(new InsufficientStockException(1L, 999, 8));

        // when / then
        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.error").value("Bad Request"))
                .andExpect(jsonPath("$.message").value("Insufficient stock for product id: 1. Requested: 999, available: 8"))
                .andExpect(jsonPath("$.path").value("/api/orders"));

        verify(orderService).createOrder(any(CreateOrderRequest.class));
    }
}