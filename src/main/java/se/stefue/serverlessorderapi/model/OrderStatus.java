package se.stefue.serverlessorderapi.model;

public enum OrderStatus {
    CREATED,
    PAID,
    SHIPPED,
    CANCELLED;

    public boolean canTransitionTo(OrderStatus newStatus) {
        if (newStatus == null) {
            return false;
        }

        return switch (this) {
            case CREATED -> newStatus == PAID || newStatus == CANCELLED;
            case PAID -> newStatus == SHIPPED || newStatus == CANCELLED;
            case SHIPPED, CANCELLED -> false;
        };
    }
}
