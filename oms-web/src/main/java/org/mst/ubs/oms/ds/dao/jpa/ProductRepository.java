package org.mst.ubs.oms.ds.dao.jpa;

import org.mst.ubs.oms.ds.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
