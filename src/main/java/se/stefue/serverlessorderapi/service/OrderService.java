package se.stefue.serverlessorderapi.service;

import se.stefue.serverlessorderapi.dto.CreateOrderItemRequest;
import se.stefue.serverlessorderapi.dto.CreateOrderRequest;
import se.stefue.serverlessorderapi.dto.UpdateOrderStatusRequest;
import se.stefue.serverlessorderapi.exception.OrderNotFoundException;
import se.stefue.serverlessorderapi.model.Order;
import se.stefue.serverlessorderapi.model.OrderItem;
import se.stefue.serverlessorderapi.model.OrderStatus;
import se.stefue.serverlessorderapi.repository.OrderRepository;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class OrderService {

    private final OrderRepository orderRepository;
    private final Clock clock;

    public OrderService(OrderRepository orderRepository, Clock clock) {
        this.orderRepository = Objects.requireNonNull(orderRepository, "orderRepository cannot be null");
        this.clock = Objects.requireNonNull(clock, "clock cannot be null");
    }

    public Order createOrder(CreateOrderRequest request) {
        Objects.requireNonNull(request, "request cannot be null");

        List<OrderItem> items = request.items().stream()
                .map(this::toOrderItem)
                .toList();

        Order order = new Order(
                generateOrderId(),
                request.customerId(),
                OrderStatus.CREATED,
                Instant.now(clock),
                items
        );
        orderRepository.save(order);
        return order;
    }

    public Order getOrderById(String orderId) {
        validateOrderId(orderId);

        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    public Order updateOrderStatus(String orderId, UpdateOrderStatusRequest request) {
        validateOrderId(orderId);
        Objects.requireNonNull(request, "request cannot be null");

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));

        order.updateStatus(request.status());
        orderRepository.save(order);

        return order;
    }

    private OrderItem toOrderItem(CreateOrderItemRequest request) {
        return new OrderItem(
                request.productId(),
                request.name(),
                request.quantity(),
                request.price()
        );
    }

    private String generateOrderId() {
        return UUID.randomUUID().toString();
    }

    private void validateOrderId(String orderId) {
        if (orderId == null ||orderId.isBlank()) {
            throw new IllegalArgumentException("orderId cannot be null or blank");
        }
    }
}
