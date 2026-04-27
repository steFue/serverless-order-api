package se.stefue.serverlessorderapi.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class OrderItemTest {

    @Test
    void shouldCreateValidOrderItem() {
        OrderItem item = new OrderItem("p1", "Coffee Beans", 2, new BigDecimal("199.00"));

        assertEquals("p1", item.productId());
        assertEquals("Coffee Beans", item.name());
        assertEquals(2, item.quantity());
        assertEquals(new BigDecimal("199.00"), item.price());
    }

    @Test
    void shouldCalculateLineTotal() {
        OrderItem item = new OrderItem("p1", "Coffee Beans", 2, new BigDecimal("199.00"));

        assertEquals(new BigDecimal("398.00"), item.lineTotal());
    }

    @Test
    void shouldThrowWhenProductIdIsBlank() {
        assertThrows(IllegalArgumentException.class,
                () -> new OrderItem(" ", "Coffee Beans", 2, new BigDecimal("199.00")));
    }

    @Test
    void shouldThrowWhenQuantityIsZero() {
        assertThrows(IllegalArgumentException.class,
                () -> new OrderItem("p1", "Coffee Beans", 0, new BigDecimal("199.00")));
    }

    @Test
    void shouldThrowWhenPriceIsNegative() {
        assertThrows(IllegalArgumentException.class,
                () -> new OrderItem("p1", "Coffee Beans", 1, new BigDecimal("-10.00")));
    }
}
