package se.stefue.serverlessorderapi.dto;

import java.util.List;
import java.util.Objects;

public record CreateOrderRequest(
        String customerId,
        List<CreateOrderItemRequest> items
) {
    public CreateOrderRequest {
        if (isBlank(customerId)) {
            throw new IllegalArgumentException("customerId cannot be blank");
        }
        Objects.requireNonNull(items, "items cannot be null");
        if (items.isEmpty()) {
            throw new IllegalArgumentException("items cannot be empty");
        }
        if (items.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("items cannot be null");
        }
        items = List.copyOf(items);
    }

    private static boolean isBlank(String value) {
        return value == null ||value.isBlank();
    }
}
