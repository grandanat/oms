package org.mst.ubs.oms.fe.controller;

import org.mst.ubs.oms.ds.model.Product;
import org.mst.ubs.oms.ds.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService service;

    @RequestMapping(method = RequestMethod.GET)
    public List<Product> getProducts() {
        return service.getProducts();
    }

}
