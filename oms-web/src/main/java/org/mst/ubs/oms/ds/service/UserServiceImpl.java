package org.mst.ubs.oms.ds.service;

import org.mst.ubs.oms.ds.dao.jpa.UserRepository;
import org.mst.ubs.oms.security.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;

	@Override
	public User findById(Long id) {
		return userRepository.findOne(id);
	}

	@Override
	public Optional<User> getUserByUsername(String username) {
		return Optional.ofNullable(userRepository.findByUsername(username));
	}

	@Override
	public void save(User user) {
		user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
		userRepository.save(user);
	}
}
