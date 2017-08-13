package org.mst.ubs.oms.ds.service;

import org.mst.ubs.oms.ds.dao.jpa.ProductRepository;
import org.mst.ubs.oms.ds.model.Product;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

	@Autowired
	private ProductRepository repository;

	public List<Product> getProducts() {
		return repository.findAll();
	}
}
