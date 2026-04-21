package se.stefue.serverlessorderapi.dto;

import java.math.BigDecimal;
import java.util.Objects;

public record CreateOrderItemRequest(
        String productId,
        String name,
        int quantity,
        BigDecimal price
) {
    public CreateOrderItemRequest {
        if (isBlank(productId)) {
            throw new IllegalArgumentException("productId cannot be blank");
        }
        if (isBlank(name)) {
            throw new IllegalArgumentException("name cannot be blank");
        }
        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity cannot be negative");
        }

        Objects.requireNonNull(price, "price cannot be null");

        if (price.signum() < 0) {
            throw new IllegalArgumentException("price cannot be negative");
        }

        }

    private static boolean isBlank(String value) {
        return value == null ||value.isBlank();
    }
}
