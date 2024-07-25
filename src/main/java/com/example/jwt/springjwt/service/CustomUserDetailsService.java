package com.example.jwt.springjwt.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.jwt.springjwt.dto.CustomUserDetails;
import com.example.jwt.springjwt.entity.UserEntity;
import com.example.jwt.springjwt.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;
	
	public CustomUserDetailsService( UserRepository userRepository) {
		this.userRepository=userRepository;
	}
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub
		UserEntity userData = userRepository.findByUsername(username);
		
		if(userData != null) {
			return new CustomUserDetails(userData);
		}
		
		return null;
	}

}
