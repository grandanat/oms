package org.mst.ubs.oms.ds.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mst.ubs.oms.ds.dao.jpa.ProductRepository;
import org.mst.ubs.oms.ds.model.Product;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.mockito.BDDMockito.given;

/**
 * Created by MarianStrugaru on 8/12/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class ProductServiceImplTest {

    @Mock
    ProductRepository productRepository;

    @InjectMocks
    ProductService fixure = new ProductServiceImpl();

    @Test
    public void shouldGetProducts() throws Exception {

        Product p1 = new Product("UBS", "UBS Asset");
        Product p2 = new Product("AMZ", "Amazon Asset");
        Product p3 = new Product("GOOG", "Google Asset");

        given(productRepository.findAll()).willReturn(
                Arrays.asList(new Product[]{p1, p2, p3}));

        // When
        List<Product> products = fixure.getProducts();

        // Then
        assertThat(products, contains(p1, p2, p3));
    }


}