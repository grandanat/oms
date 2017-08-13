package org.mst.ubs.oms.ds.dao.jpa;

import org.hamcrest.Matchers;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mst.ubs.oms.ds.model.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;

/**
 * Created by MarianStrugaru on 8/12/2017.
 */
//@RunWith(SpringJUnit4ClassRunner.class)
@RunWith(SpringRunner.class)
@DataJpaTest
public class OrderRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private OrderRepository fixture;

    @Test
    public void shouldFindOrderByUserId() {
        // Given
        Order testOrder1 = Order.newOrder()
                .id(1L)
                .orderId("order1")
                .userId(12345L)
                .price(new BigDecimal(10))
                .quantity(new BigDecimal(100))
                .productCode("AMZ")
                .build();
        Order testOrder2 = Order.newOrder()
                .id(1L)
                .orderId("order1")
                .userId(12344L)
                .price(new BigDecimal(10))
                .quantity(new BigDecimal(100))
                .productCode("AMZ")
                .build();
        entityManager.persist(testOrder1);
        entityManager.persist(testOrder2);

        // When
        List<Order> order1 = fixture.findByUserId(12345L);
        List<Order> order2 = fixture.findByUserId(12346L);

        // Then
        assertThat(order1, contains(testOrder1));
        assertThat(order2, contains(testOrder2));
    }

    @Test
    public void shouldNotFindOrderByUserId() {
        // Given
        Order testOrder1 = Order.newOrder()
                .id(1L)
                .orderId("order1")
                .userId(12345L)
                .price(new BigDecimal(10))
                .quantity(new BigDecimal(100))
                .productCode("AMZ")
                .build();
        entityManager.persist(testOrder1);

        // When
        List<Order> order1 = fixture.findByUserId(12346L);

        // Then
        assertThat(order1, Matchers.empty());
    }
}