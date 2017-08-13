package org.mst.ubs.oms.ds.service;

import org.hamcrest.Matcher;
import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.mst.ubs.oms.ds.model.Order;
import org.mst.ubs.oms.ds.model.OrderType;

import java.math.BigDecimal;
import java.util.NoSuchElementException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertTrue;
import static org.mst.ubs.oms.ds.model.OrderType.BUY;
import static org.mst.ubs.oms.ds.model.OrderType.SELL;

/**
 * Created by MarianStrugaru on 8/12/2017.
 */
public class OrderBookManagerImplTest {

    @Test
    public void testEmptyOrderBook() throws Exception {
        // Given
        // When
        OrderBookManager orderBookManager = new OrderBookManagerImpl();

        // Then
        assertThat(orderBookManager.getBestPrice("VOD.L", BUY), isBigD(0));
        assertThat(orderBookManager.getTotalVolumeForPrice("VOD.L", BUY, asBigD(1)), isBigD(0));
        assertThat(orderBookManager.getTotalQuantityForPrice("VOD.L", BUY, asBigD(100)), isBigD(0));
        assertTrue(orderBookManager.getOrdersForPrice("VOD.L", BUY, asBigD(100)).isEmpty());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testAddNotAllowedForDuplicateOrder() throws Exception {
        // Given
        OrderBookManager orderBookManager = new OrderBookManagerImpl();
        orderBookManager.addOrder(1L, createTestOrder(1, "VOD.L", BUY, 200, 11));

        // When
        orderBookManager.addOrder(1L, createTestOrder(1, "VOD.L", BUY, 200, 11));
        // Then throw exception
    }

    @Test(expected = NoSuchElementException.class)
    public void testDeleteNotAllowedOnInvalidOrderId() throws Exception {
        // Given
        OrderBookManager orderBookManager = new OrderBookManagerImpl();
        orderBookManager.addOrder(1L, createTestOrder(1, "VOD.L", BUY, 200, 11));

        // When
        orderBookManager.deleteOrder(1L, 2L);
        // Then throw exception
    }

    @Test
    public void testOrderBookGetStatsAfterAdd() throws Exception {
        // Given
        OrderBookManager orderBookManager = new OrderBookManagerImpl();

        // When
        Order order = createTestOrder(1, "VOD.L", BUY, 200, 11);
        orderBookManager.addOrder(1L, order);

        // Then
        assertThat(orderBookManager.getBestPrice("VOD.L", BUY), isBigD(200));
        assertThat(orderBookManager.getBestPrice("VOD.l", BUY), isBigD(0));
        assertThat(orderBookManager.getBestPrice("VOD.L", SELL), isBigD(0));
        assertThat(orderBookManager.getBestPrice("VOD.L", BUY), isBigD(200));//second time still true

        assertThat(orderBookManager.getTotalVolumeForPrice("VOD.L", BUY, asBigD(200)), isBigD(2200));
        assertThat(orderBookManager.getTotalVolumeForPrice("VOD.T", BUY, asBigD(200)), isBigD(0));
        assertThat(orderBookManager.getTotalVolumeForPrice("VOD.L", SELL, asBigD(200)), isBigD(0));
        assertThat(orderBookManager.getTotalVolumeForPrice("VOD.L", BUY, asBigD(201)), isBigD(0));
        assertThat(orderBookManager.getTotalVolumeForPrice("VOD.L", BUY, asBigD(200)), isBigD(2200));//still true

        assertThat(orderBookManager.getTotalQuantityForPrice("VOD.L", BUY, asBigD(200)), isBigD(11));
        assertThat(orderBookManager.getTotalQuantityForPrice("VOD.A", BUY, asBigD(200)), isBigD(0));
        assertThat(orderBookManager.getTotalQuantityForPrice("VOD.L", SELL, asBigD(200)), isBigD(0));
        assertThat(orderBookManager.getTotalQuantityForPrice("VOD.L", BUY, asBigD(201)), isBigD(0));
        assertThat(orderBookManager.getTotalQuantityForPrice("VOD.L", BUY, asBigD(200)), isBigD(11));//still true

        assertThat(orderBookManager.getOrdersForPrice("VOD.L", BUY, asBigD(200)).size(), is(1));
        assertThat(orderBookManager.getOrdersForPrice("VOD.L", BUY, asBigD(200)).get(0), equalTo(order));
        assertThat(orderBookManager.getOrdersForPrice("VOD.l", BUY, asBigD(200)).size(), is(0));
        assertThat(orderBookManager.getOrdersForPrice("VOD.L", BUY, asBigD(100)).size(), is(0));

    }

    @Test
    public void testOrderBookStatsAfterAddUpdateRemove() throws Exception {
        // Given
        OrderBookManager orderBookManager = new OrderBookManagerImpl();

        // When
        Order order = createTestOrder(1, "VOD.L", BUY, 200, 11);
        orderBookManager.addOrder(1L, order);
        // Then
        assertThat(orderBookManager.getBestPrice("VOD.L", BUY), isBigD(200));
        assertThat(orderBookManager.getTotalVolumeForPrice("VOD.L", BUY, asBigD(200)), isBigD(2200));
        assertThat(orderBookManager.getTotalQuantityForPrice("VOD.L", BUY, asBigD(200)), isBigD(11));
        assertThat(orderBookManager.getOrdersForPrice("VOD.L", BUY, asBigD(200)).size(), is(1));
        assertThat(orderBookManager.getOrdersForPrice("VOD.L", BUY, asBigD(200)).get(0), equalTo(order));

        // When
        orderBookManager.deleteOrder(1L, 1L);

        // Then price has modified after remove
        assertThat(orderBookManager.getBestPrice("VOD.L", BUY), isBigD(0));
        // Then volume has modified after remove
        assertThat(orderBookManager.getTotalVolumeForPrice("VOD.L", BUY, asBigD(200)), isBigD(0));
        // Then quantity has modified after remove
        assertThat(orderBookManager.getTotalQuantityForPrice("VOD.L", BUY, asBigD(200)), isBigD(0));
        // Then order list is empty after remove
        assertThat(orderBookManager.getOrdersForPrice("VOD.L", BUY, asBigD(200)).size(), is(0));
    }

    @Test
    public void testOrderBookStatsAfterMultipleAdds() throws Exception {
        // Given
        OrderBookManager orderBookManager = new OrderBookManagerImpl();

        // When
        orderBookManager.addOrder(1L, createTestOrder(1, "VOD.L", BUY, 100, 10));
        orderBookManager.addOrder(1L, createTestOrder(12, "VOD.L", BUY, 100, 20));
        orderBookManager.addOrder(1L, createTestOrder(13, "VOD.L", BUY, 110, 10));

        // Then
        assertThat(orderBookManager.getBestPrice("VOD.L", BUY), isBigD(110));
        assertThat(orderBookManager.getTotalVolumeForPrice("VOD.L", BUY, asBigD(100)), isBigD(3000));
        assertThat(orderBookManager.getTotalVolumeForPrice("VOD.L", BUY, asBigD(110)), isBigD(1100));
        assertThat(orderBookManager.getTotalVolumeForPrice("VOD.L", BUY, asBigD(120)), isBigD(0));
        assertThat(orderBookManager.getTotalQuantityForPrice("VOD.L", BUY, asBigD(100)), isBigD(30));
        assertThat(orderBookManager.getTotalQuantityForPrice("VOD.L", BUY, asBigD(110)), isBigD(10));
        assertThat(orderBookManager.getTotalQuantityForPrice("VOD.L", BUY, asBigD(120)), isBigD(0));
        assertThat(orderBookManager.getOrdersForPrice("VOD.L", BUY, asBigD(100)).size(), is(2));
        assertThat(orderBookManager.getOrdersForPrice("VOD.L", BUY, asBigD(110)).size(), is(1));
        assertThat(orderBookManager.getOrdersForPrice("VOD.L", BUY, asBigD(120)).size(), is(0));
    }

    @Test
    public void testOrderBookStatsAfterMultipeAddUpdateRemove() throws Exception {
        // Given
        OrderBookManager orderBookManager = new OrderBookManagerImpl();

        // When
        orderBookManager.addOrder(1L, createTestOrder(111, "VOD.L", BUY, 100, 10));
        orderBookManager.addOrder(1L, createTestOrder(112, "VOD.L", BUY, 100, 10));
        orderBookManager.addOrder(1L, createTestOrder(113, "VOD.L", BUY, 110, 10));
        orderBookManager.addOrder(1L, createTestOrder(211, "VOD.T", BUY, 400, 20));
        orderBookManager.addOrder(1L, createTestOrder(212, "VOD.T", BUY, 400, 20));
        orderBookManager.addOrder(1L, createTestOrder(311, "VOD.L", SELL, 200, 3));
        orderBookManager.addOrder(1L, createTestOrder(412, "VOD.L", BUY, 300, 15));

        orderBookManager.addOrder(1L, createTestOrder(413, "VOD.L", BUY, 200, 20));
        orderBookManager.addOrder(1L, createTestOrder(423, "VOD.T", BUY, 200, 30));
        orderBookManager.addOrder(1L, createTestOrder(312, "VOD.L", SELL, 200, 140));
        orderBookManager.addOrder(1L, createTestOrder(523, "VOD.T", SELL, 210, 60));

        // Then
        assertThat(orderBookManager.getBestPrice("VOD.L", BUY), isBigD(300));
        assertThat(orderBookManager.getBestPrice("VOD.L", SELL), isBigD(200));
        assertThat(orderBookManager.getBestPrice("VOD.T", BUY), isBigD(400));
        assertThat(orderBookManager.getBestPrice("VOD.T", SELL), isBigD(210));

        assertThat(orderBookManager.getTotalVolumeForPrice("VOD.L", BUY, asBigD(100)), isBigD(2000));
        assertThat(orderBookManager.getTotalVolumeForPrice("VOD.L", BUY, asBigD(110)), isBigD(1100));
        assertThat(orderBookManager.getTotalVolumeForPrice("VOD.L", SELL, asBigD(200)), isBigD(28600));
        assertThat(orderBookManager.getTotalVolumeForPrice("VOD.T", BUY, asBigD(200)), isBigD(6000));
        assertThat(orderBookManager.getTotalVolumeForPrice("VOD.T", SELL, asBigD(210)), isBigD(12600));

        assertThat(orderBookManager.getTotalQuantityForPrice("VOD.L", BUY, asBigD(100)), isBigD(20));
        assertThat(orderBookManager.getTotalQuantityForPrice("VOD.L", BUY, asBigD(110)), isBigD(10));
        assertThat(orderBookManager.getTotalQuantityForPrice("VOD.L", SELL, asBigD(200)), isBigD(143));
        assertThat(orderBookManager.getTotalQuantityForPrice("VOD.T", BUY, asBigD(200)), isBigD(30));
        assertThat(orderBookManager.getTotalQuantityForPrice("VOD.T", SELL, asBigD(210)), isBigD(60));

        assertThat(orderBookManager.getOrdersForPrice("VOD.L", BUY, asBigD(100)).size(), is(2));
        assertThat(orderBookManager.getOrdersForPrice("VOD.L", BUY, asBigD(110)).size(), is(1));
        assertThat(orderBookManager.getOrdersForPrice("VOD.L", SELL, asBigD(200)).size(), is(2));
        assertThat(orderBookManager.getOrdersForPrice("VOD.T", BUY, asBigD(200)).size(), is(1));
        assertThat(orderBookManager.getOrdersForPrice("VOD.T", SELL, asBigD(210)).size(), is(1));


        // When
        orderBookManager.deleteOrder(1L, 112L);
        orderBookManager.addOrder(1L, createTestOrder(721, "VOD.L", BUY, 100, 100));
        orderBookManager.addOrder(1L, createTestOrder(731, "VOD.T", BUY, 100, 300));
        orderBookManager.addOrder(1L, createTestOrder(732, "VOD.L", BUY, 310, 1));
        orderBookManager.addOrder(1L, createTestOrder(734, "VOD.L", BUY, 350, 1000));
        orderBookManager.deleteOrder(1L, 734L);

        // Then
        assertThat(orderBookManager.getBestPrice("VOD.L", BUY), isBigD(310));
        assertThat(orderBookManager.getTotalVolumeForPrice("VOD.L", BUY, asBigD(100)), isBigD(11000));
        assertThat(orderBookManager.getTotalQuantityForPrice("VOD.L", BUY, asBigD(100)), isBigD(110));
        assertThat(orderBookManager.getOrdersForPrice("VOD.L", BUY, asBigD(200)).size(), is(1));
    }

    private Order createTestOrder(int orderId, String productCode, OrderType type,
                                  int price, int quantity) {
        return Order.newOrder()
                .id((long)orderId)
                .orderId("order" + orderId)
                .userId(1L)
                .price(new BigDecimal(price))
                .quantity(new BigDecimal(quantity))
                .productCode(productCode)
                .type(type)
                .build();
    }

    private BigDecimal asBigD(int n) {
        return new BigDecimal(n);
    }

    public static Matcher<BigDecimal> isBigD(int value) {
        return is(IsEqual.equalTo(new BigDecimal(value)));
    }
}