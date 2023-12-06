package com.example.demo;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
@JsonIgnoreProperties(ignoreUnknown = true, value = { "authorities", "accountNonExpired", "enabled",
		"credentialsNonExpired" })
public class User implements UserDetails {

	@Id
	private String username;
	private String password;
	private boolean accountNonLocked;
//	private Collection<GrantedAuthority> authorities;

	User() {
//		this.authorities = new ArrayList<>();
//		this.authorities.add(new SimpleGrantedAuthority("read"));
		this.accountNonLocked = true;
	}

	public User(String name) {
		this();
		this.username = name;
		this.password = "";

	}

	public User(String name, String password) {
		this();
		this.username = name;
		this.password = password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	public void setUsername(String name) {
		this.username = name;
	}

	@Override
	public String getPassword() {
		return this.password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	@Override
	public String toString() {
		return username;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (!(o instanceof User))
			return false;
		User user = (User) o;
		return Objects.equals(this.username, user.username);
	}

	@Override
	public int hashCode() {
		return Objects.hash(this.username);
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
//		return this.authorities;
		return List.of(() -> "read");
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	public void setAccountNonLocked(Boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public boolean getAccountNonLocked() {
		return accountNonLocked;
	}
}
