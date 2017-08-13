package org.mst.ubs.oms.ds.dao.jpa;

import org.mst.ubs.oms.ds.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByUserId(Long userId);
}
