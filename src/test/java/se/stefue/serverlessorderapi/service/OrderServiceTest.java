package se.stefue.serverlessorderapi.service;

import org.junit.jupiter.api.Test;
import se.stefue.serverlessorderapi.dto.CreateOrderItemRequest;
import se.stefue.serverlessorderapi.dto.CreateOrderRequest;
import se.stefue.serverlessorderapi.dto.UpdateOrderStatusRequest;
import se.stefue.serverlessorderapi.model.Order;
import se.stefue.serverlessorderapi.model.OrderStatus;
import se.stefue.serverlessorderapi.repository.InMemoryOrderRepository;
import se.stefue.serverlessorderapi.repository.OrderRepository;

import java.math.BigDecimal;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class OrderServiceTest {

    private final OrderRepository orderRepository = new InMemoryOrderRepository();
    private final Clock fixedClock = Clock.fixed(
            Instant.parse("2026-04-23T10:15:30Z"),
            ZoneOffset.UTC
    );
    private final OrderService orderService = new OrderService(orderRepository, fixedClock);

    @Test
    void shouldCreateOrder() {
        CreateOrderRequest request = new CreateOrderRequest(
                "cust-1",
                List.of(new CreateOrderItemRequest("p1", "Coffee Beans", 2, new BigDecimal("199.00")))
        );

        Order createdOrder = orderService.createOrder(request);

        assertEquals("cust-1", createdOrder.getCustomerId());
        assertEquals(OrderStatus.CREATED, createdOrder.getStatus());
        assertEquals(Instant.parse("2026-04-23T10:15:30Z"), createdOrder.getCreatedAt());
        assertEquals(new BigDecimal("398.00"), createdOrder.getTotalAmount());
    }

    @Test
    void shouldGetOrderById() {
        CreateOrderRequest request = new CreateOrderRequest(
                "cust-1",
                List.of(new CreateOrderItemRequest("p1", "Coffee Beans", 2, new BigDecimal("199.00")))
        );

        Order createdOrder = orderService.createOrder(request);
        Order foundOrder = orderService.getOrderById(createdOrder.getOrderId());
        assertEquals(createdOrder.getOrderId(), foundOrder.getOrderId());
    }

    @Test
    void shouldThrowWhenOrderIsNotFound() {
        CreateOrderRequest request = new CreateOrderRequest(
                "cust-1",
                List.of(new CreateOrderItemRequest("p1", "Coffee Beans", 2, new BigDecimal("199.00")))
        );

        Order createdOrder = orderService.createOrder(request);

        Order updatedOrder = orderService.updateOrderStatus(
                createdOrder.getOrderId(),
                new UpdateOrderStatusRequest(OrderStatus.PAID)
        );

        assertEquals(OrderStatus.PAID, updatedOrder.getStatus());
    }
}
