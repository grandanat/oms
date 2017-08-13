package org.mst.ubs.oms.ds.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by MarianStrugaru on 8/12/2017.
 */
@ResponseStatus(value= HttpStatus.FORBIDDEN, reason="Order not allowed")
public class OrderNotAllowedException extends Exception {
    public OrderNotAllowedException() {
        super();
    }
    public OrderNotAllowedException(String s) {
        super(s);
    }
}
