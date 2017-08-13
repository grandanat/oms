package org.mst.ubs.oms.fe;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;
import org.mst.ubs.oms.SpringBootWebApplication;
import org.mst.ubs.oms.ds.model.Order;
import org.mst.ubs.oms.fe.controller.OrderController;
import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertTrue;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = SpringBootWebApplication.class)
@Profile("test")
public class OrderControllerIT extends BaseEndpointTest {

    @InjectMocks
    OrderController controller;

    @Before
    public void setup() throws Exception {
        super.setup();
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldHaveNoOrders() throws Exception {
        mockMvc.perform(get("/orders")
                .with(user("user").password("password").roles("USER")
                )
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));
    }

    @Test
    public void shouldCreateRetrieveDelete() throws Exception {
        Order order = Order.newOrder()
                .orderId("order1")
                .price(new BigDecimal(10))
                .quantity(new BigDecimal(100))
                .productCode("AMZ")
                .build();

        byte[] r1Json = toJson(order);

        //CREATE
        MvcResult result = mockMvc.perform(post("/orders")
                        .with(user("user").password("password").roles("USER")
                 )
                .content(r1Json)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrlPattern("/orders"))
                .andReturn();
        long id = getResourceIdFromUrl(result.getResponse().getRedirectedUrl());

        //RETRIEVE
        mockMvc.perform(get("/orders/" + id)
                    .with(user("user").password("password").roles("USER")
                )
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is((int) id)))
                .andExpect(jsonPath("$.productCode", is(order.getProductCode())))
                .andExpect(jsonPath("$.orderId", is(order.getOrderId())))
                .andExpect(jsonPath("$.price", is(order.getPrice())))
                .andExpect(jsonPath("$.quantity", is(order.getQuantity())));

        //DELETE
        mockMvc.perform(delete("/orders/" + id)
                .with(user("user").password("password").roles("USER")
                ))
                .andExpect(status().isNoContent());

        //RETRIEVE should fail
        mockMvc.perform(get("/orders/" + id)
                .with(user("user").password("password").roles("USER")
                )
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        //todo: you can test the 404 error body too.

    }

    /*
    ******************************
     */

    private long getResourceIdFromUrl(String locationUrl) {
        String[] parts = locationUrl.split("/");
        return Long.valueOf(parts[parts.length - 1]);
    }


    private byte[] toJson(Object r) throws Exception {
        ObjectMapper map = new ObjectMapper();
        return map.writeValueAsString(r).getBytes();
    }

    // match redirect header URL (aka Location header)
    private static ResultMatcher redirectedUrlPattern(final String expectedUrlPattern) {
        return new ResultMatcher() {
            public void match(MvcResult result) {
                Pattern pattern = Pattern.compile("\\A" + expectedUrlPattern + "\\z");
                assertTrue(pattern.matcher(result.getResponse().getRedirectedUrl()).find());
            }
        };
    }

}
