package org.mst.ubs.oms.ds.service;

/**
 * Created by MarianStrugaru on 8/12/2017.
 */
public class AccessNotAllowedException extends Exception {
    public AccessNotAllowedException() {
        super();
    }
    public AccessNotAllowedException(String s) {
        super(s);
    }
}
