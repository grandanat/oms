package org.mst.ubs.oms.ds.dao.jpa;

import org.mst.ubs.oms.security.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {

    User findByUsername(String username);

}
