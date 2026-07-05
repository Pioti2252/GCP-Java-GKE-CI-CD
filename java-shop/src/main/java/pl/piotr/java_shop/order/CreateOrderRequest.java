package pl.piotr.java_shop.order;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

public record CreateOrderRequest(
        @NotBlank(message = "Customer email is required")
        @Email(message = "Customer email must be valid")
        String customerEmail,

        @NotEmpty(message = "Order must contain at least one item")
        List<@Valid CreateOrderItemRequest> items
) {
}