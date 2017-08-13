package org.mst.ubs.oms.fe.security;

import org.mst.ubs.oms.security.User;
import org.mst.ubs.oms.ds.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CurrentUserDetailsService implements UserDetailsService {

	private final UserService userService;

	@Autowired
	public CurrentUserDetailsService(UserService userService) {
		this.userService = userService;
	}

	@Override
	public CurrentUser loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userService.getUserByUsername(username).orElseThrow(
				() -> new UsernameNotFoundException(String.format("User with username=%s was not found", username)));
		return new CurrentUser(user);
	}

}
