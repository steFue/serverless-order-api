package se.stefue.serverlessorderapi.mapper;

import se.stefue.serverlessorderapi.dto.OrderItemResponse;
import se.stefue.serverlessorderapi.dto.OrderResponse;
import se.stefue.serverlessorderapi.model.Order;

import java.util.List;

public class OrderResponseMapper {

    public OrderResponse toResponse(Order order) {
        List<OrderItemResponse> items = order.getItems().stream()
                .map(item -> new OrderItemResponse(
                        item.productId(),
                        item.name(),
                        item.quantity(),
                        item.price()
                ))
                .toList();
        return new OrderResponse(
                order.getOrderId(),
                order.getCustomerId(),
                order.getStatus().name(),
                order.getTotalAmount(),
                order.getCreatedAt(),
                items
        );
    }
}
