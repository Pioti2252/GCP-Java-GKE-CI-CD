package pl.piotr.java_shop.order;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public record OrderResponse(
        Long id,
        String customerEmail,
        Instant createdAt,
        OrderStatus status,
        BigDecimal totalAmount,
        List<OrderItemResponse> items
) {
    public static OrderResponse from(Order order) {
        return new OrderResponse(
                order.getId(),
                order.getCustomerEmail(),
                order.getCreatedAt(),
                order.getStatus(),
                order.getTotalAmount(),
                order.getItems()
                        .stream()
                        .map(OrderItemResponse::from)
                        .toList()
        );
    }
}