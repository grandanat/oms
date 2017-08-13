package org.mst.ubs.oms.security;

public interface SecurityService {
    String findLoggedInUsername();

    void autologin(String username, String password);
}
