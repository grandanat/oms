package org.mst.ubs.oms.ds.service;

import org.mst.ubs.oms.ds.dao.jpa.OrderRepository;
import org.mst.ubs.oms.ds.model.Order;
import org.mst.ubs.oms.utils.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrderServiceImpl implements OrderService {

    @Autowired
    private OrderBookManager orderBookManager;

    public List<Order> getOrders(Long userId) {
        return CollectionUtils.asList(orderBookManager.getOrdersByUser(userId).values());
    }

    public Order getOrder(Long userId, Long orderId) throws AccessNotAllowedException, OrderNotFoundException {
        Order order = orderBookManager.getOrder(userId, orderId);
        if (order == null || order.getUserId() != userId) {
            throw new OrderNotFoundException(String.format("Order with id %d not found!", orderId));
        }

        return order;
    }

    public Order addOrder(Long userId, Order order) throws OrderInvalidException {
        if (!checkIfOrderIsValidForCreation(userId, order)) {
            throw new OrderInvalidException(String.format("Order with id %d is not valid!", order.getId()));
        }
        orderBookManager.addOrder(userId, order);
        return order;
    }

    public void deleteOrder(Long userId, Long orderId) throws OrderNotFoundException, OrderInvalidException {

        Order order = orderBookManager.getOrder(userId, orderId);

        if (order == null || order.getUserId() != userId) {
            throw new OrderNotFoundException(String.format("Order with id %d not found!", orderId));
        }

        if (!checkIfOrderIsValidForDeletion(userId, order)) {
            throw new OrderInvalidException(String.format("Order with id %d is not valid!", order.getId()));
        }
        orderBookManager.deleteOrder(userId, order.getId());
    }

    private boolean checkIfOrderIsValidForCreation(Long userId, Order order) {
        return true;
    }

    private boolean checkIfOrderIsValidForDeletion(Long userId, Order order) {
        return true;
    }
}
