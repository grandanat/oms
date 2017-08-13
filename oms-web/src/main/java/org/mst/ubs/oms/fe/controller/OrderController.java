package org.mst.ubs.oms.fe.controller;

import org.mst.ubs.oms.ds.model.Order;
import org.mst.ubs.oms.fe.security.CurrentUser;
import org.mst.ubs.oms.ds.service.AccessNotAllowedException;
import org.mst.ubs.oms.ds.service.OrderInvalidException;
import org.mst.ubs.oms.ds.service.OrderNotFoundException;
import org.mst.ubs.oms.ds.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrderService service;

    @RequestMapping(method = RequestMethod.GET)
    public List<Order> getOrders(@AuthenticationPrincipal Principal principal) {
        CurrentUser activeUser = (CurrentUser) ((Authentication) principal).getPrincipal();
        return service.getOrders(activeUser.getId());
    }

    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Order getOrder(@AuthenticationPrincipal Principal principal, @PathVariable("id") Long id) throws OrderNotFoundException, AccessNotAllowedException {
        CurrentUser activeUser = (CurrentUser) ((Authentication) principal).getPrincipal();
        return service.getOrder(activeUser.getId(), id);
    }

    @RequestMapping(method = RequestMethod.POST)
    public Order addOrder(@AuthenticationPrincipal Principal principal, @RequestBody Order order) throws OrderInvalidException {
        CurrentUser activeUser = (CurrentUser) ((Authentication) principal).getPrincipal();
        return service.addOrder(activeUser.getId(), order);
    }


    @RequestMapping(value = "/{id}", method = RequestMethod.DELETE)
    public void deleteOrder(@AuthenticationPrincipal Principal principal, @PathVariable("id") Long id) throws OrderInvalidException, OrderNotFoundException {
        CurrentUser activeUser = (CurrentUser) ((Authentication) principal).getPrincipal();
        service.deleteOrder(activeUser.getId(), id);
    }
}
