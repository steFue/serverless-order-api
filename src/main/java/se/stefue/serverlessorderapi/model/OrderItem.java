package se.stefue.serverlessorderapi.model;

import java.math.BigDecimal;

public record OrderItem(
        String productId,
        String name,
        int quantity,
        BigDecimal price
) {

}
