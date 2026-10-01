package com.nestify.core;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.nestify.entities.User;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UserPrincipal implements UserDetails {
	private final Long id;
	private final String email;
	private final String password;
	private final Collection<? extends GrantedAuthority> authorities;
	
	public static UserPrincipal create(User user) {
		return new UserPrincipal(
				user.getId(),
				user.getEmail(),
				user.getPassword(),
				Collections.emptyList()
		);
	}
	
	@Override
	public String getUsername() {
		return email; // Spring Security için 'username' bizim sistemimizde kullanıcının email'idir
	}
	
	@Override
	public boolean isAccountNonExpired() {
		return true;
	}
	
	@Override
	public boolean isAccountNonLocked() {
		return true;
	}
	
	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}
	
	@Override
	public boolean isEnabled() {
		return true;
	}
}
