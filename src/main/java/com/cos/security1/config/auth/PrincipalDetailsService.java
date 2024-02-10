package com.cos.security1.config.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

//시큐리티 설정에서 .loginProcessingUrl("login");
// /login 요청이 오면 자동으로 UserDetailsService 타입으로 IoC되어 있는 loadUserByUsername함수가 실행함 - 규칙임
// /login 요청 -> UserDetailsService 호출 -> loadUserByUsername메서드 실행함

@Service
public class PrincipalDetailsService implements UserDetailsService{
	
	@Autowired
	private UserRepository userRepository;
	
	//넘어오는 파라미터 이름도 잘 설정해줘야함 username 이렇게 name을 설정해야함
	//다르게 만들라면  config에서 설정을 바꿔줘야함 -> .usernameParameter("username2") 이렇게
	
	//시큐리티 세션(내부 Authentication(여기 내부에 new PrincipalDetail(userEntity) - userDetails))
	//이렇게하면 로그인 됨
	
	//해당 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User userEntity = userRepository.findByUsername(username);
		if(userEntity != null) {
			System.out.println(username);
			System.out.println(userEntity);
			return new PrincipalDetails(userEntity);
		}
		return null;
	}
	

}
