package org.mst.ubs.oms.ds.service;

import org.mst.ubs.oms.ds.model.Order;
import org.mst.ubs.oms.ds.model.OrderType;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface OrderBookManager {

    /**
     * Get orders
     *
     *
     * @param userId userId <br/>
     *
     * @return a map of orders grouped by price
     */
    Map<Long, Order> getOrdersByUser(Long userId);

    /**
     * Get orders
     *
     * @param userId userId <br/>
     * @param orderId orderId <br/>
     *
     * @return order
     */
    Order getOrder(Long userId, Long orderId);

    /**
     * Add new order
     *
     * Orders for the same product, on the same side, with the same price should be kept in the order as they arrive
     *
     * @param order new order to add <br/>
     *
     * @see Order
     */
    void addOrder(Long userId, Order order);

    /**
     * Delete existyng order
     *
     * @param orderId unique identifier of existing order
     */
    void deleteOrder(Long userId, Long orderId);

    /**
     * Get the best price for the product and side.
     *
     * For buy orders - the highest price
     * For sell orders - the lowest price
     *
     * @param product identifier of an product
     * @param type either buy or sell
     * @return the best price, or -1 if there're no orders for the product on this side
     */
    BigDecimal getBestPrice(String product, OrderType type);

    /**
     * Get total number of orders for the product on given side with given price
     *
     * @param product identifier of an product
     * @param side either buy or sell
     * @param price requested price level
     * @return total number of orders, or -1 if there're no orders for the product on this side with this price
     */
    int getOrderNumForPrice(String product, OrderType side, BigDecimal price);

    /**
     * Get cumulative quantity of all orders for the product on given side with given price
     *
     * @param product identifier of an product
     * @param side either buy or sell
     * @param price requested price level
     * @return total quantity, or -1 if there're no orders for the product on this side with this price
     */
    BigDecimal getTotalQuantityForPrice(String product, OrderType side, BigDecimal price);

    /**
     * Get cumulative volume ( sum of price * quantity ) of all orders for the product on given side with given price
     *
     * @param product identifier of an product
     * @param type either buy or sell
     * @param price requested price level
     * @return total volume, or -1 if there're no orders for the product on this side with this price
     */
    BigDecimal getTotalVolumeForPrice(String product, OrderType type, BigDecimal price);

    /**
     * Get all orders for the product on given side with given price
     *
     * @param product identifier of an product
     * @param type either buy or sell
     * @param price requested price level
     * @return all orders, or empty list if there're no orders for the product on this side with this price
     */
    List<Order> getOrdersForPrice(String product, OrderType type, BigDecimal price);


}
