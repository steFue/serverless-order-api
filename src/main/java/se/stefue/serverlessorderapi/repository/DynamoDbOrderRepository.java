package se.stefue.serverlessorderapi.repository;

import se.stefue.serverlessorderapi.model.Order;
import se.stefue.serverlessorderapi.persitence.OrderEntity;
import se.stefue.serverlessorderapi.persitence.OrderEntityMapper;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

import java.util.Objects;
import java.util.Optional;

public class DynamoDbOrderRepository implements OrderRepository {

    private final DynamoDbTable<OrderEntity> orderTable;
    private final OrderEntityMapper orderEntityMapper;

    public DynamoDbOrderRepository(DynamoDbEnhancedClient enhancedClient, String tableName, OrderEntityMapper orderEntityMapper) {
        Objects.requireNonNull(enhancedClient, "enhancedClient must not be null");

        if (tableName == null ||tableName.isBlank()) {
            throw new IllegalArgumentException("tableName must not be null or blank");
        }

        this.orderEntityMapper = Objects.requireNonNull(orderEntityMapper, "orderEntityMapper must not be null");
        this.orderTable = enhancedClient.table(tableName, TableSchema.fromClass(OrderEntity.class));
    }

    @Override
    public void save (Order order) {
        Objects.requireNonNull(order, "order must not be null");

        orderTable.putItem(orderEntityMapper.toEntity(order));
    }

    @Override
    public Optional<Order> findById(String orderId) {
        if  (orderId == null || orderId.isBlank()) {
            throw new IllegalArgumentException("orderId must not be null");
        }

        OrderEntity entity = orderTable.getItem(Key.builder().partitionValue(orderId).build());

        if (entity == null) {
            return Optional.empty();
        }
        return Optional.of(orderEntityMapper.toDomain(entity));
    }
}
