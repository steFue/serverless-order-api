package se.stefue.serverlessorderapi.config;

import se.stefue.serverlessorderapi.persitence.OrderEntityMapper;
import se.stefue.serverlessorderapi.repository.DynamoDbOrderRepository;
import se.stefue.serverlessorderapi.service.OrderService;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;

import java.time.Clock;

public class OrderServiceFactory {

    private static final String TABLE_NAME_ENV = "ORDERS_TABLE_NAME";

    private OrderServiceFactory() {

    }

    public static OrderService createOrderService() {
        String tableName = System.getenv(TABLE_NAME_ENV);

        if (tableName == null || tableName.isBlank()) {
            throw new IllegalStateException("Missing environment variable: " + TABLE_NAME_ENV);
        }

        DynamoDbEnhancedClient enhancedClient = DynamoDbConfig.createEnhancedClient();

        DynamoDbOrderRepository repository = new DynamoDbOrderRepository(
                enhancedClient,
                tableName,
                new OrderEntityMapper()
        );

        return new OrderService(repository, Clock.systemUTC());
    }
}
