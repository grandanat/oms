package org.mst.ubs.oms.ds.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mst.ubs.oms.ds.dao.jpa.OrderRepository;
import org.mst.ubs.oms.ds.model.Order;
import org.springframework.beans.factory.annotation.Autowired;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by MarianStrugaru on 8/12/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class OrderServiceImplTest {

    @Mock
    OrderRepository orderRepository;

//    @Autowired
    @InjectMocks
    OrderService fixure = new OrderServiceImpl();

    @Test
    public void shouldGetOrders() throws Exception {

        Order o1 = createTestOrder(1L, 1L);
        Order o2 = createTestOrder(1L, 2L);

        given(orderRepository.findByUserId(1L)).willReturn(
                Arrays.asList(new Order[]{o1, o2}));

        // When
        List<Order> orders = fixure.getOrders(1L);

        // Then
        assertThat(orders, contains(o1, o2));
    }

    @Test
    public void addOrder() throws Exception {
        Order o1 = createTestOrder(1L, 1L);

        given(orderRepository.findOne(1L))
                .willReturn(o1);
        given(orderRepository.save(o1))
                .willReturn(o1);

        // When
        Order order = fixure.addOrder(1L, o1);

        // Then
        assertThat(order, equalTo(o1));
    }

    @Test
    public void deleteOrder() throws Exception {
        Order o1 = createTestOrder(14L, 12L);
        given(orderRepository.findOne(12L))
                .willReturn(o1);

        // When
        fixure.deleteOrder(14L, 12L);

        // Then
        verify(orderRepository, times(1)).delete(12L);
    }

    private Order createTestOrder(long userId, long id) {
        return Order.newOrder()
                .id(1L)
                .orderId("order"+id)
                .userId(userId)
                .price(new BigDecimal(10))
                .quantity(new BigDecimal(100))
                .productCode("AMZ")
                .build();
    }
}