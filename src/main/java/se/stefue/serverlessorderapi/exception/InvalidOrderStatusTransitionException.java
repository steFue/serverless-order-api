package se.stefue.serverlessorderapi.exception;

import se.stefue.serverlessorderapi.model.OrderStatus;

public class InvalidOrderStatusTransitionException extends RuntimeException {

    public InvalidOrderStatusTransitionException(OrderStatus currentStatus, OrderStatus newStatus) {
        super("Invalid status transition from " + currentStatus + " to " + newStatus);
    }
}
