package pl.piotr.java_shop.order;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pl.piotr.java_shop.product.Product;
import pl.piotr.java_shop.product.ProductRepository;
import pl.piotr.java_shop.product.ProductNotFoundException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public OrderService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    @Transactional
    public Order createOrder(CreateOrderRequest request) {
        Order order = new Order(request.customerEmail());

        BigDecimal totalAmount = BigDecimal.ZERO;

        for (CreateOrderItemRequest itemRequest : request.items()) {
            Product product = productRepository.findById(itemRequest.productId())
                    .orElseThrow(() -> new ProductNotFoundException(itemRequest.productId()));

            if (product.getStockQuantity() < itemRequest.quantity()) {
                throw new InsufficientStockException(
                        itemRequest.productId(),
                        itemRequest.quantity(),
                        product.getStockQuantity()
                );
            }

            product.setStockQuantity(product.getStockQuantity() - itemRequest.quantity());

            OrderItem orderItem = new OrderItem(product, itemRequest.quantity());
            order.addItem(orderItem);

            totalAmount = totalAmount.add(orderItem.getLineTotal());
        }

        order.setTotalAmount(totalAmount);

        return orderRepository.save(order);
    }
}