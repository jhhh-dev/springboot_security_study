package com.cos.security1.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cos.security1.model.User;

//crud 함수를 jpaRepository가 들고있음
//@Repository라는 어노테이션 없어도 IoC됨. 이유는 JpaRepository를 상속했기 때문
public interface UserRepository extends JpaRepository<User, Integer>{ //id가 Integer니까 //crud 함수 사용가능함
	// findBy규칙 -> Username 문법
	// select * from user where username = 1?
	public User findByUsername(String username); //jpa네임함수? 찾아서 확인하기 jpa query method
	
	//select * from user where email = 1?
	//public User findByEmail()
	
}
