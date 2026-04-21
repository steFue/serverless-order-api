package se.stefue.serverlessorderapi.dto;

import se.stefue.serverlessorderapi.model.OrderStatus;

import java.util.Objects;

public record UpdateOrderStatusRequest(OrderStatus status) {
    public UpdateOrderStatusRequest {
        Objects.requireNonNull(status, "status must not be null");
    }
}
