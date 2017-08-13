package org.mst.ubs.oms.ds.service;

import org.mst.ubs.oms.security.User;

import java.util.Optional;

public interface UserService {

    void save(User user);

    Optional<User> getUserByUsername(String username);

    User findById(Long id);

}