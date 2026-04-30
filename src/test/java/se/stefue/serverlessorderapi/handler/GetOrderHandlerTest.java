package se.stefue.serverlessorderapi.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import se.stefue.serverlessorderapi.config.ObjectMapperFactory;
import se.stefue.serverlessorderapi.exception.OrderNotFoundException;
import se.stefue.serverlessorderapi.mapper.OrderResponseMapper;
import se.stefue.serverlessorderapi.model.Order;
import se.stefue.serverlessorderapi.model.OrderItem;
import se.stefue.serverlessorderapi.model.OrderStatus;
import se.stefue.serverlessorderapi.service.OrderService;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class GetOrderHandlerTest {

    private OrderService orderService;
    private GetOrderHandler handler;
    private Context context;

    @BeforeEach
    void setUp() {

        orderService = mock(OrderService.class);

        handler = new GetOrderHandler(
                orderService,
                new OrderResponseMapper(),
                ObjectMapperFactory.create()
        );

        context = mock(Context.class);
        LambdaLogger logger = mock(LambdaLogger.class);
        when(context.getLogger()).thenReturn(logger);
    }

    @Test
    void shouldReturn200WhenOrderExists() {
        Order order = new Order(
                "ord-1",
                "cust-1",
                OrderStatus.CREATED,
                Instant.parse("2026-04-23T10:15:30Z"),
                List.of(new OrderItem("p1", "Coffee Beans", 2, new BigDecimal("199.00")))
        );

        when(orderService.getOrderById("ord-1")).thenReturn(order);

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withPathParameters(Map.of("orderId", "ord-1"));

        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        assertEquals(200, response.getStatusCode());
        assertEquals("application/json", response.getHeaders().get("Content-Type"));
    }

    @Test
    void shouldReturn400WhenOrderIdPathParameterIsMissing() {
        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withPathParameters(null);

        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        assertEquals(400, response.getStatusCode());
        assertEquals("{\"message\":\"Missing path parameter: orderId\"}", response.getBody());
    }

    @Test
    void shouldReturn404WhenOrderDoesNotExist() {

        when(orderService.getOrderById("missing-order"))
                .thenThrow(new OrderNotFoundException("missing-order"));

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withPathParameters(Map.of("orderId", "missing-order"));

        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        assertEquals(404, response.getStatusCode());
        assertEquals("{\"message\":\"Order not found: missing-order\"}", response.getBody());
    }

    @Test
    void shouldReturn500WhenUnexpectedErrorOccurs() {
        when(orderService.getOrderById("ord-1"))
                .thenThrow(new RuntimeException("unexpected error"));

        APIGatewayProxyRequestEvent request = new APIGatewayProxyRequestEvent()
                .withPathParameters(Map.of("orderId", "ord-1"));

        APIGatewayProxyResponseEvent response = handler.handleRequest(request, context);

        assertEquals(500, response.getStatusCode());
        assertEquals("{\"message\":\"Internal server error\"}", response.getBody());
    }
}
