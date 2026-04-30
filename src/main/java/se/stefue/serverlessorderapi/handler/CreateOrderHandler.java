package se.stefue.serverlessorderapi.handler;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import se.stefue.serverlessorderapi.config.ObjectMapperFactory;
import se.stefue.serverlessorderapi.config.OrderServiceFactory;
import se.stefue.serverlessorderapi.dto.CreateOrderRequest;
import se.stefue.serverlessorderapi.dto.ErrorResponse;
import se.stefue.serverlessorderapi.dto.OrderResponse;
import se.stefue.serverlessorderapi.mapper.OrderResponseMapper;
import se.stefue.serverlessorderapi.service.OrderService;

import java.util.Map;

public class CreateOrderHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private final OrderService orderService;
    private final OrderResponseMapper orderResponseMapper;
    private final ObjectMapper objectMapper;

    /*public CreateOrderHandler() {
        this.orderService = new OrderService(
            new InMemoryOrderRepository(),
                Clock.systemUTC()
        );
        this.orderResponseMapper = new OrderResponseMapper();
        this.objectMapper = new ObjectMapper().findAndRegisterModules();

    }*/
    public CreateOrderHandler() {
        this.orderService = OrderServiceFactory.createOrderService();
        this.orderResponseMapper = new OrderResponseMapper();
        this.objectMapper = ObjectMapperFactory.create();
    }

    public CreateOrderHandler(OrderService orderService, OrderResponseMapper orderResponseMapper, ObjectMapper objectMapper) {
        this.orderService = orderService;
        this.orderResponseMapper = orderResponseMapper;
        this.objectMapper = objectMapper;
    }

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent request, Context context) {
        try {
            CreateOrderRequest createOrderRequest = parseRequestBody(request);

            context.getLogger().log("Creating order for customerId: " + createOrderRequest.customerId());

            OrderResponse response = orderResponseMapper.toResponse(
                    orderService.createOrder(createOrderRequest)
            );

            return jsonResponse(201, response);
        } catch (IllegalArgumentException e) {
            context.getLogger().log("Invalid request: " + e.getMessage());

            return jsonResponse(400, new ErrorResponse(e.getMessage()));
        } catch (JsonProcessingException e) {
            context.getLogger().log("Failed to parse request body: " + e.getMessage());
            return jsonResponse(400, new ErrorResponse("Invalid JSON request body"));
        } catch (Exception e) {
            context.getLogger().log("Unexpected error: " + e.getMessage());
            return jsonResponse(500, new ErrorResponse("Internal server error"));
        }
    }

    private CreateOrderRequest parseRequestBody(APIGatewayProxyRequestEvent request) throws JsonProcessingException {
        String body = request.getBody();

        if (body == null || body.isBlank()) {
            throw new IllegalArgumentException("Request body must not be blank");
        }
        return objectMapper.readValue(body, CreateOrderRequest.class);

    }

    private APIGatewayProxyResponseEvent jsonResponse(int statusCode, Object body) {

        try {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(statusCode)
                    .withHeaders(Map.of("Content-Type", "application/json"))
                    .withBody(objectMapper.writeValueAsString(body));
        } catch (JsonProcessingException e) {
            return new APIGatewayProxyResponseEvent()
                    .withStatusCode(500)
                    .withHeaders(Map.of("Content-Type", "application/json"))
                    .withBody("{\"message\":\"Failed to serialize response\"}");
        }
    }

}
