package se.stefue.serverlessorderapi.model;

import java.math.BigDecimal;
import java.util.Objects;

public record OrderItem(
        String productId,
        String name,
        int quantity,
        BigDecimal price
) {
    public OrderItem {
        if (isBlank(productId)) {
            throw new IllegalArgumentException("productId must not be blank");
        }

        if (isBlank(name)) {
            throw new IllegalArgumentException("name must not be blank");
        }

        if (quantity <= 0) {
            throw new IllegalArgumentException("quantity must be greater than 0");
        }
        Objects.requireNonNull(price, "price must not be null");

        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("price must not be negative");
        }

    }
    public BigDecimal lineTotal() {
        return price.multiply(BigDecimal.valueOf(quantity));
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

}
