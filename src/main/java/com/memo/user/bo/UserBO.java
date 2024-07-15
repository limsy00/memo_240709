package com.memo.user.bo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.memo.user.entity.UserEntity;
import com.memo.user.repository.UserRepository;

@Service
public class UserBO {
	
	@Autowired
	private UserRepository userRepository;
	
	// input: login
	// output: UserEntity 채워져 있거나 null
	public UserEntity getUserEntityByLoginId(String loginId) { // singUp
		return userRepository.findByLoginId(loginId);
	}
	
	// input: loginId, password
	// output: UserEntity or null
	public UserEntity getUserEntityByLoginIdPassword(String loginId, String password) { // singIn 
		return userRepository.findByLoginIdAndPassword(loginId, password);
	}
	
	// input: 4파라미터
	// output: UserEntity
	public UserEntity addUser(String loginId, String password, String name, String email) { // singUp
		return userRepository.save(UserEntity.builder()
				.loginId(loginId)
				.password(password)
				.name(name)
				.email(email)
				.build());
	}
	
	
}
