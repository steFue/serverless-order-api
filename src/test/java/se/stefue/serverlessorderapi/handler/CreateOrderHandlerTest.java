package se.stefue.serverlessorderapi.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.stefue.serverlessorderapi.config.ObjectMapperFactory;
import se.stefue.serverlessorderapi.dto.CreateOrderRequest;
import se.stefue.serverlessorderapi.mapper.OrderResponseMapper;
import se.stefue.serverlessorderapi.model.Order;
import se.stefue.serverlessorderapi.model.OrderItem;
import se.stefue.serverlessorderapi.model.OrderStatus;
import se.stefue.serverlessorderapi.service.OrderService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CreateOrderHandlerTest {

    private OrderService orderService;
    private CreateOrderHandler handler;
    private Context context;


    @BeforeEach
    void setUp() {
        orderService = mock(OrderService.class);

        handler = new CreateOrderHandler(
                orderService,
                new OrderResponseMapper(),
                ObjectMapperFactory.create()
        );
        context = mock(Context.class);
        LambdaLogger logger = mock(LambdaLogger.class);
        when(context.getLogger()).thenReturn(logger);
    }

    @Test
    void shouldReturn201WhenOrderIsCreated() {
        Order createdOrder = new Order(
                "ord-1",
                "cust-1",
                OrderStatus.CREATED,
                Instant.parse("2026-04-23T10:15:30Z"),
                List.of(new OrderItem("p1 ", "Coffee Beans", 2, new BigDecimal("199.00")))

        );

        when(orderService.createOrder(any(CreateOrderRequest.class))).thenReturn(createdOrder);

        String requestBody = """
                {
                  "customerId": "cust-1",
                  "items": [
                    {
                      "productId": "p1",
                      "name": "Coffee Beans",
                      "quantity": 2,
                      "price": 199.00
                    }
                  ]
                }
                """;

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withBody(requestBody);

        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        assertEquals(201, response.getStatusCode());
        assertEquals("application/json", response.getHeaders().get("Content-Type"));

    }


    @Test
    void shouldReturn400WhenRequestBodyIsMissing() {
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withBody(null);

        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        assertEquals(400, response.getStatusCode());
        assertEquals("{\"message\":\"Request body must not be blank\"}", response.getBody());
    }

    @Test
    void shouldReturn400WhenRequestBodyIsInvalidJson() {
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withBody("{ invalid json }");

        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        assertEquals(400, response.getStatusCode());
        assertEquals("{\"message\":\"Invalid JSON request body\"}", response.getBody());
    }

    @Test
    void shouldReturn500WhenUnexpectedErrorOccurs() {
        when(orderService.createOrder(any(CreateOrderRequest.class)))
                .thenThrow(new RuntimeException("Boom"));

        String requestBody = """
                {
                  "customerId": "cust-1",
                  "items": [
                    {
                      "productId": "p1",
                      "name": "Coffee Beans",
                      "quantity": 2,
                      "price": 199.00
                    }
                  ]
                }
                """;

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withBody(requestBody);

        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        assertEquals(500, response.getStatusCode());
        assertEquals("{\"message\":\"Internal server error\"}", response.getBody());
    }
}
