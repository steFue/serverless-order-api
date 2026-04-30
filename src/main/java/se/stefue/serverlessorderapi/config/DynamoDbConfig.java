package se.stefue.serverlessorderapi.config;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;

public class DynamoDbConfig {


    public DynamoDbConfig() {
    }

    public static DynamoDbEnhancedClient createEnhancedClient() {
        DynamoDbClient dynamoDbclient = DynamoDbClient.create();

        return DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbclient)
                .build();
    }
}
