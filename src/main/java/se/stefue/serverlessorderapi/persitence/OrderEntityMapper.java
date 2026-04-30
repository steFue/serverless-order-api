package se.stefue.serverlessorderapi.persitence;

import se.stefue.serverlessorderapi.model.Order;
import se.stefue.serverlessorderapi.model.OrderItem;
import se.stefue.serverlessorderapi.model.OrderStatus;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class OrderEntityMapper {

    public OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity();
        entity.setOrderId(order.getOrderId());
        entity.setCustomerId(order.getCustomerId());
        entity.setStatus(order.getStatus().name());
        entity.setCreatedAt(order.getCreatedAt().toString());
        entity.setItems(
                order.getItems().stream()
                        .map(this::toEntity)
                        .toList()
        );
        return entity;
    }

    public Order toDomain(OrderEntity entity) {
        List<OrderItem> items = entity.getItems().stream()
                .map(this::toDomain)
                .toList();

        return new Order(
                entity.getOrderId(),
                entity.getCustomerId(),
                OrderStatus.valueOf(entity.getStatus()),
                Instant.parse(entity.getCreatedAt()),
                items
        );
    }

    private OrderItemEntity toEntity(OrderItem item) {
        OrderItemEntity entity = new OrderItemEntity();
        entity.setProductId(item.productId());
        entity.setName(item.name());
        entity.setQuantity(item.quantity());
        entity.setPrice(item.price().toPlainString());
        return entity;
    }

    private OrderItem toDomain(OrderItemEntity entity) {
        return new OrderItem(
                entity.getProductId(),
                entity.getName(),
                entity.getQuantity(),
                new BigDecimal(entity.getPrice())
        );
    }
}
