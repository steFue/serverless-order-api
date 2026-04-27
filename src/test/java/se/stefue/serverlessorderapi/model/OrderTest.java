package se.stefue.serverlessorderapi.model;

import org.junit.jupiter.api.Test;
import se.stefue.serverlessorderapi.exception.InvalidOrderStatusTransitionException;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderTest {

    @Test
    void shouldCreateValidOrder() {
        OrderItem item = new OrderItem("p1", "Coffee Beans", 2, new BigDecimal("199.00"));

        Order order = new Order(
                "ord-1",
                "cust-1",
                OrderStatus.CREATED,
                Instant.parse("2026-04-23T10:15:30Z"),
                List.of(item)
        );

        assertEquals("ord-1", order.getOrderId());
        assertEquals("cust-1", order.getCustomerId());
        assertEquals(OrderStatus.CREATED, order.getStatus());
        assertEquals(new BigDecimal("398.00"), order.getTotalAmount());
    }

    @Test
    void shouldUpdateStatusWhenTransitionIsValid() {
        Order order = new Order(
                "ord-1",
                "cust-1",
                OrderStatus.CREATED,
                Instant.parse("2026-04-23T10:15:30Z"),
                List.of(new OrderItem("p1", "Coffee Beans", 2, new BigDecimal("199.00")))
        );

        order.updateStatus(OrderStatus.PAID);

        assertEquals(OrderStatus.PAID, order.getStatus());
    }

    @Test
    void shouldThrowWhenStatusTransitionIsInvalid() {
        Order order = new Order(
                "ord-1",
                "cust-1",
                OrderStatus.SHIPPED,
                Instant.parse("2026-04-23T10:15:30Z"),
                List.of(new OrderItem("p1", "Coffee Beans", 2, new BigDecimal("199.00")))
        );

        assertThrows(InvalidOrderStatusTransitionException.class,
                () -> order.updateStatus(OrderStatus.CREATED));
    }

    @Test
    void shouldThrowWhenItemsAreEmpty() {

        assertThrows(IllegalArgumentException.class, () -> new Order(
                "ord-1",
                "cust-1",
                OrderStatus.CREATED,
                Instant.parse("2026-04-23T10:15:30Z"),
                List.of()
        ));
    }
}
