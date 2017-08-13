package org.mst.ubs.oms.ds.service;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by MarianStrugaru on 8/12/2017.
 */
@ResponseStatus(value= HttpStatus.BAD_REQUEST, reason="Invalid Order")  // 404
public class OrderInvalidException extends Exception {
    public OrderInvalidException() {
        super();
    }
    public OrderInvalidException(String s) {
        super(s);
    }
}
