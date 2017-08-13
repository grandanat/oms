package org.mst.ubs.oms.ds.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by MarianStrugaru on 8/12/2017.
 */
@ResponseStatus(value= HttpStatus.NOT_FOUND, reason="No such Order")  // 404
public class OrderNotFoundException extends Exception {
    public OrderNotFoundException() {
        super();
    }
    public OrderNotFoundException(String s) {
        super(s);
    }
}
