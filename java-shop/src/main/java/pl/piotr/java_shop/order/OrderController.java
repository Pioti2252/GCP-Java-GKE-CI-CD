package pl.piotr.java_shop.order;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping
    public List<OrderResponse> getAllOrders() {
        return orderService.getAllOrders()
                .stream()
                .map(OrderResponse::from)
                .toList();
    }

    @GetMapping("/{id}")
    public OrderResponse getOrderById(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return OrderResponse.from(order);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody CreateOrderRequest request) {
        Order order = orderService.createOrder(request);
        return OrderResponse.from(order);
    }
}