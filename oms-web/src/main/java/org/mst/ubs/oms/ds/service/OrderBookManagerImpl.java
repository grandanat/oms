package org.mst.ubs.oms.ds.service;

import org.mst.ubs.oms.ds.model.Order;
import org.mst.ubs.oms.ds.model.OrderType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.mst.ubs.oms.ds.model.OrderType.BUY;

/**
 * Implementation of an OrderBook manager based on maps structures:
 * First is a hashmap for storing orders by orderId. Allows fast operations based on order Id. Especially find.
 * Other 2 hashMaps one for each side, that stores orders by product key. For each product, the orders are grouped and order by price
 * by using a TreeMap
 * <p>
 * The purpose of the second maps is to group orders by product and by ordered price.
 * Also, the treemaps keeps the relevant information of orderBookLevel, total quantity and total volume up to date.
 * This information is actualized for any operations that modify the order book.
 * <p>
 *
 * Note: This implementation is not threadsafe. Is focused on data structures and time complexity,
 * and not for multithreaded environments
 */
@Service
public class OrderBookManagerImpl implements OrderBookManager {

    /**
     * map structure for keeping orders by user, then by orderId
     */
    Map<Long, Map<Long, Order>> orders = new HashMap<>();

    /**
     * map structure for keeping BUY type orders grouped by productCode and then price
     */
    Map<String, TreeMap<BigDecimal, OrderBookLevel>> buyOrderBookLevels = new HashMap<>();
    /**
     * map structure for keeping SELL type orders grouped by productCode and then price
     */
    Map<String, TreeMap<BigDecimal, OrderBookLevel>> sellOrderBookLevels = new HashMap<>();

    /**
     * class for storing orders with same price for a product
     * total volume, quantity and price is computed each time new order is added or modified
     */
    private class OrderBookLevel {
        private BigDecimal totalQuantity = BigDecimal.ZERO;
        private BigDecimal totalVolume = BigDecimal.ZERO;
        private List<Long> orderIds;

        private OrderBookLevel() {
            this.orderIds = new ArrayList<>();
        }

        public List<Long> getOrderIds() {
            return orderIds;
        }

        public int getSize() {
            return orderIds.size();
        }

        public BigDecimal getTotalQuantity() {
            return totalQuantity;
        }

        public BigDecimal getTotalVolume() {
            return totalVolume;
        }

        public void addOrder(Order order) {
            this.orderIds.add(order.getId());
            this.totalQuantity =  this.totalQuantity.add(order.getQuantity());
            this.totalVolume =  this.totalVolume.add(order.getPrice().multiply(order.getQuantity()));
        }

        public void removeOrder(Order order) {
            this.orderIds.remove(order.getId());
            this.totalQuantity =  this.totalQuantity.subtract(order.getQuantity());
            this.totalVolume =  this.totalVolume.subtract(order.getPrice().multiply(order.getQuantity()));
        }

    }

    private Map<String, TreeMap<BigDecimal, OrderBookLevel>> getOrderBookOrderType(OrderType type) {
        switch (type) {
            case BUY:
                return buyOrderBookLevels;
            default: //SELL
                return sellOrderBookLevels;
        }
    }

    /**
     * Returns true if given orderId is found
     *
     * @param orderId
     * @return
     */
    boolean containsOrder(Long userId, Long orderId) {
        if (!orders.containsKey(userId)) {
            return false;
        }
        return orders.get(userId).containsKey(orderId);
    }
    Order insertOrder(Long userId, Order order) {
        if (!orders.containsKey(userId)) {
            orders.put(userId, new HashMap<>());
        }
        return orders.get(userId).put(order.getId(), order);
    }
    Order removeOrder(Long userId, Long orderId) {
        if (!orders.containsKey(userId) || !orders.get(userId).containsKey(orderId)) {
            throw new NoSuchElementException(String.format("Order with id %d not found for user %d", orderId, userId));
        }
        return orders.get(userId).remove(orderId);
    }

    /**
     * Returns true if Order BookeLevel is found for given product, side and price
     *
     * @param product
     * @param type
     * @return
     */
    private boolean containsOrderBookLevel(String product, OrderType type, BigDecimal price) {
        TreeMap<BigDecimal, OrderBookLevel> tree = getOrderBookOrderType(type).get(product);
        if (tree == null) {
            return false;
        }
        return tree.containsKey(price);
    }

    /**
     * Get the OrderBookLevel for the given order
     * If there is no OrderBookLevel, it creates a new one empty.
     *
     * @param order
     * @return
     */
    private OrderBookLevel getOrCreateOrderBookLevel(Order order) {

        Map<String, TreeMap<BigDecimal, OrderBookLevel>> type = getOrderBookOrderType(order.getType());

        if (!type.containsKey(order.getProductCode())) {
            type.put(order.getProductCode(), new TreeMap<>());
        }

        TreeMap<BigDecimal, OrderBookLevel> tree = type.get(order.getProductCode());

        if (!tree.containsKey(order.getPrice())) {
            tree.put(order.getPrice(), new OrderBookLevel());
        }
        return tree.get(order.getPrice());
    }

    /**
     * Remove level from OrderBook
     *
     * @param product
     * @param type
     * @param price
     * @return removed OrderBookLevel
     */
    private OrderBookLevel removeOrderBookLevel(String product, OrderType type, BigDecimal price) {
        TreeMap<BigDecimal, OrderBookLevel> tree = getOrderBookOrderType(type).get(product);
        if (tree == null) {
            return null;
        }

        return tree.remove(price);
    }

    /**
     * Get the orderBook level for given product,type and price
     *
     * @param product
     * @param type
     * @param price
     * @return the order book level for the given product, type and price
     */
    private OrderBookLevel getOrderBookLevel(String product, OrderType type, BigDecimal price) {
        TreeMap<BigDecimal, OrderBookLevel> tree = getOrderBookOrderType(type).get(product);
        if (tree == null) {
            return null;
        }

        return tree.get(price);
    }
    /**
     * {@inheritDoc}
     */
    @Override
    public Map<Long, Order> getOrdersByUser(Long userId) {
        return orders.get(userId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Order getOrder(Long userId, Long orderId) {
        if (orders.containsKey(userId)/* || orders.get(userId).containsKey(orderId)*/) {
            return null;
        }
        return orders.get(userId).get(orderId);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addOrder(Long userId, Order order) {
        Long orderId = order.getId();
        if (containsOrder(userId, orderId)) {
            throw new IllegalArgumentException(String.format("Order with id %d is already present in book", orderId));
        }
        insertOrder(userId, order);
        getOrCreateOrderBookLevel(order).addOrder(order);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void deleteOrder(Long userId, Long orderId) {
        if (!containsOrder(userId, orderId)) {
            throw new NoSuchElementException(String.format("Order with id %d not found", orderId));
        }
        Order order = removeOrder(userId, orderId);
        OrderBookLevel bookLevel = getOrderBookLevel(order.getProductCode(), order.getType(), order.getPrice());
        bookLevel.removeOrder(order);
        if (bookLevel.getSize() == 0) {
            removeOrderBookLevel(order.getProductCode(), order.getType(), order.getPrice());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getBestPrice(String product, OrderType type) {

        TreeMap<BigDecimal, OrderBookLevel> tree = getOrderBookOrderType(type).get(product);
        if (tree == null || tree.isEmpty()) {
            return BigDecimal.ZERO;
        }

        if (type == BUY) {
            return tree.lastKey();
        } else {
            return tree.firstKey();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getOrderNumForPrice(String product, OrderType type, BigDecimal price) {
        if (!containsOrderBookLevel(product, type, price)) {
            return 0;
        }
        return getOrderBookLevel(product, type, price).getSize();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getTotalQuantityForPrice(String product, OrderType type, BigDecimal price) {
        OrderBookLevel bookLevel = getOrderBookLevel(product, type, price);
        if (bookLevel == null) {
            return BigDecimal.ZERO;
        }
        return bookLevel.getTotalQuantity();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BigDecimal getTotalVolumeForPrice(String product, OrderType type, BigDecimal price) {
        OrderBookLevel bookLevel = getOrderBookLevel(product, type, price);
        if (bookLevel == null) {
            return BigDecimal.ZERO;
        }
        return bookLevel.getTotalVolume();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Order> getOrdersForPrice(String product, OrderType type, BigDecimal price) {
        OrderBookLevel bookLevel = getOrderBookLevel(product, type, price);
        if (bookLevel == null) {
            return new ArrayList<>();
        }

        List<Long> orderIds = bookLevel.getOrderIds();

        List<Order> ret = new ArrayList<>(orderIds.size());

        ret.addAll(
                orders.values().stream()
                        .flatMap(m -> m.entrySet().stream())
                        .filter(map -> orderIds.contains(map.getKey()))
                        .collect(Collectors.toMap(p -> p.getKey(), p -> p.getValue())).values()
        );

        return ret;
    }

}
