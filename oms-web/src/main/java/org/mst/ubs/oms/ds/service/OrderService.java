package org.mst.ubs.oms.ds.service;

import org.mst.ubs.oms.ds.model.Order;

import java.util.List;

public interface OrderService {

    List<Order> getOrders(Long userId);

    Order getOrder(Long userId, Long orderId) throws AccessNotAllowedException, OrderNotFoundException;

    Order addOrder(Long userId, Order order) throws OrderInvalidException;

    void deleteOrder(Long userId, Long orderId) throws OrderNotFoundException, OrderInvalidException;

}