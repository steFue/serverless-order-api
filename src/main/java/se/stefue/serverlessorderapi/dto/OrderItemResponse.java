package se.stefue.serverlessorderapi.dto;

import java.math.BigDecimal;

public record OrderItemResponse(
        String productId,
        String name,
        int quantity,
        BigDecimal price
) {
}
