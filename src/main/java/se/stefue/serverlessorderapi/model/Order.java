package se.stefue.serverlessorderapi.model;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Order {

    private final String orderId;
    private final String customerId;
    private OrderStatus status;
    private final Instant createdAt;
    private final List<OrderItem> items;

    public Order(String orderId, String customerId, OrderStatus status, Instant createdAt, List<OrderItem> items) {

        if (isBlank(orderId)) {
            throw new IllegalArgumentException("orderId must not be blank");
        }
        if (isBlank(customerId)) {
            throw new IllegalArgumentException("customerId must not be blank");
        }
        this.status = Objects.requireNonNull(status, "status must not be null");
        this.createdAt = Objects.requireNonNull(createdAt, "createdAt must not be null");

        Objects.requireNonNull(items, "items must not be null");

        if (items.isEmpty()) {
            throw new IllegalArgumentException("items must not be empty");
        }
        if (items.stream().anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("items must not be null");
        }
        this.orderId = orderId;
        this.customerId = customerId;
        this.items = List.copyOf(items);

    }

    public String getOrderId() {
        return orderId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public List<OrderItem> getItems() {
        return new ArrayList<>(items);
    }

    public BigDecimal getTotalAmount() {
        return items.stream()
                .map(OrderItem::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public void updateStatus (OrderStatus newStatus) {
        Objects.requireNonNull(newStatus, "newStatus must not be null");

        if (!status.canTransitionTo(newStatus)) {
            throw new IllegalArgumentException("Invalid status transition from " + status + " to " + newStatus);
        };
        this.status = newStatus;
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
