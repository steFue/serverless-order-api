package se.stefue.serverlessorderapi.repository;

import se.stefue.serverlessorderapi.model.Order;

import java.util.Optional;

public interface OrderRepository {

    void save(Order order);

    Optional<Order> findById(String orderId);

}
