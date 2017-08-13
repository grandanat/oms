package org.mst.ubs.oms.fe;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mst.ubs.oms.ds.model.Order;
import org.mst.ubs.oms.ds.service.OrderService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.util.NestedServletException;

import javax.transaction.Transactional;
import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.mockito.BDDMockito.given;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@Transactional
public class OrderControllerTest extends BaseEndpointTest {

    @MockBean
    private OrderService orderService;

    private Order testOrder;

    @Before
    public void setup() throws Exception {

        super.setup();

        testOrder = Order.newOrder()
                .id(1L)
                .orderId("order1")
                .price(new BigDecimal(10))
                .quantity(new BigDecimal(100))
                .productCode("AMZ")
                .build();


    }

    @Test
    public void getOrderById() throws Exception {
        given(orderService.getOrder(null,1L))
                .willReturn(testOrder);

        mockMvc.perform(get("/orders/{id}", 1)
                        .with(user("user").password("password").roles("USER"))
                )
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().contentType(JSON_MEDIA_TYPE))
                .andExpect(jsonPath("$.id", is(testOrder.getId())))
                .andExpect(jsonPath("$.productCode", is(testOrder.getProductCode())))
                .andExpect(jsonPath("$.orderId", is(testOrder.getOrderId())))
                .andExpect(jsonPath("$.price", is(testOrder.getPrice())))
                .andExpect(jsonPath("$.quantity", is(testOrder.getQuantity())));
        ;
    }

    @Test(expected = NestedServletException.class)
    public void handleGenericException() throws Exception {

        given(orderService.getOrder(null, 1L))
                .willThrow(new RuntimeException("Failed to get person by id"));

        mockMvc.perform(get("/orders/{id}", 1)
                        .with(user("user").password("password").roles("USER"))
                )
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(content().string(""))
        ;
    }

    @Test(expected = NestedServletException.class)
    public void handleGenericException2() throws Exception {

        given(orderService.getOrder(null, 1L))
                .willThrow(new RuntimeException("Failed to get person by id"));

        mockMvc.perform(get("/orders/{id}", 1)
                        .with(user("user").password("password2").roles("USER"))
                )
                .andDo(print())
                .andExpect(status().is5xxServerError())
                .andExpect(content().string(""))
        ;
    }
}
