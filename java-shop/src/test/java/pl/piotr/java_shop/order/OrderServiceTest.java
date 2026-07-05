package pl.piotr.java_shop.order;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pl.piotr.java_shop.product.Product;
import pl.piotr.java_shop.product.ProductNotFoundException;
import pl.piotr.java_shop.product.ProductRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class OrderServiceTest {

    private OrderRepository orderRepository;
    private ProductRepository productRepository;
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        orderRepository = Mockito.mock(OrderRepository.class);
        productRepository = Mockito.mock(ProductRepository.class);
        orderService = new OrderService(orderRepository, productRepository);
    }

    @Test
    void shouldReturnAllOrders() {
        // given
        Order order1 = new Order("test1@example.com");
        Order order2 = new Order("test2@example.com");

        when(orderRepository.findAll()).thenReturn(List.of(order1, order2));

        // when
        List<Order> orders = orderService.getAllOrders();

        // then
        assertThat(orders).hasSize(2);
        assertThat(orders.get(0).getCustomerEmail()).isEqualTo("test1@example.com");
        assertThat(orders.get(1).getCustomerEmail()).isEqualTo("test2@example.com");

        verify(orderRepository).findAll();
    }

    @Test
    void shouldReturnOrderById() {
        // given
        Order order = new Order("test@example.com");

        when(orderRepository.findById(1L)).thenReturn(Optional.of(order));

        // when
        Order result = orderService.getOrderById(1L);

        // then
        assertThat(result.getCustomerEmail()).isEqualTo("test@example.com");

        verify(orderRepository).findById(1L);
    }

    @Test
    void shouldThrowExceptionWhenOrderNotFound() {
        // given
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> orderService.getOrderById(999L))
                .isInstanceOf(OrderNotFoundException.class)
                .hasMessage("Order not found with id: 999");

        verify(orderRepository).findById(999L);
    }

    @Test
    void shouldCreateOrderAndDecreaseProductStock() {
        // given
        Product product = new Product(
                "Laptop Lenovo ThinkPad",
                "Business laptop",
                new BigDecimal("3999.99"),
                10
        );

        CreateOrderRequest request = new CreateOrderRequest(
                "test@example.com",
                List.of(
                        new CreateOrderItemRequest(1L, 2)
                )
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Order result = orderService.createOrder(request);

        // then
        assertThat(result.getCustomerEmail()).isEqualTo("test@example.com");
        assertThat(result.getStatus()).isEqualTo(OrderStatus.CREATED);
        assertThat(result.getItems()).hasSize(1);
        assertThat(result.getTotalAmount()).isEqualByComparingTo("7999.98");

        OrderItem item = result.getItems().get(0);

        assertThat(item.getProduct().getName()).isEqualTo("Laptop Lenovo ThinkPad");
        assertThat(item.getQuantity()).isEqualTo(2);
        assertThat(item.getUnitPrice()).isEqualByComparingTo("3999.99");
        assertThat(item.getLineTotal()).isEqualByComparingTo("7999.98");

        assertThat(product.getStockQuantity()).isEqualTo(8);

        verify(productRepository).findById(1L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldCreateOrderWithMultipleItems() {
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

        CreateOrderRequest request = new CreateOrderRequest(
                "test@example.com",
                List.of(
                        new CreateOrderItemRequest(1L, 2),
                        new CreateOrderItemRequest(2L, 1)
                )
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(laptop));
        when(productRepository.findById(2L)).thenReturn(Optional.of(mouse));

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // when
        Order result = orderService.createOrder(request);

        // then
        assertThat(result.getItems()).hasSize(2);
        assertThat(result.getTotalAmount()).isEqualByComparingTo("8129.97");

        assertThat(laptop.getStockQuantity()).isEqualTo(8);
        assertThat(mouse.getStockQuantity()).isEqualTo(49);

        verify(productRepository).findById(1L);
        verify(productRepository).findById(2L);
        verify(orderRepository).save(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenProductDoesNotExist() {
        // given
        CreateOrderRequest request = new CreateOrderRequest(
                "test@example.com",
                List.of(
                        new CreateOrderItemRequest(999L, 1)
                )
        );

        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        // when / then
        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(ProductNotFoundException.class)
                .hasMessage("Product not found with id: 999");

        verify(productRepository).findById(999L);
        verify(orderRepository, never()).save(any(Order.class));
    }

    @Test
    void shouldThrowExceptionWhenStockIsInsufficient() {
        // given
        Product product = new Product(
                "Laptop Lenovo ThinkPad",
                "Business laptop",
                new BigDecimal("3999.99"),
                3
        );

        CreateOrderRequest request = new CreateOrderRequest(
                "test@example.com",
                List.of(
                        new CreateOrderItemRequest(1L, 5)
                )
        );

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        // when / then
        assertThatThrownBy(() -> orderService.createOrder(request))
                .isInstanceOf(InsufficientStockException.class)
                .hasMessage("Insufficient stock for product id: 1. Requested: 5, available: 3");

        assertThat(product.getStockQuantity()).isEqualTo(3);

        verify(productRepository).findById(1L);
        verify(orderRepository, never()).save(any(Order.class));
    }
}