package com.cos.security1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
public class PasswordEncoderConfig {
	//해당 메서드의 리턴되는 오브젝트를 IoC로 등록해준다\
	//원래 SecurtiryConfig에 등록해놨는데 스프링 순환 참조(Circular reference) 오류발생으로 따로 빼서 등록해준다
	@Bean
	public BCryptPasswordEncoder encodePwd() {
		return new BCryptPasswordEncoder();
	}
}
