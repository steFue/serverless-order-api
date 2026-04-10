package se.stefue.serverlessorderapi.model;

import java.time.Instant;
import java.util.List;

public class Order {

    private final String orderId;
    private final String customerId;
    private OrderStatus status;
    private final Instant createdAt;
    private final List<OrderItem> items;

    public Order(String orderId, String customerId, Instant createdAt, List<OrderItem> items) {
        this.orderId = orderId;
        this.customerId = customerId;
        this.createdAt = createdAt;
        this.items = items;
    }
}
