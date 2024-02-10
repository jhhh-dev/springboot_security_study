package com.cos.security1.config.auth;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import com.cos.security1.model.User;

import lombok.Data;

// 시큐리티가 /login 주소 요청이 오면 낚아채서 로그인을 진행시킨다
// 로그인 진생이 완료가 되면 session을 만들어줍니다.
// 시큐리티가 가지고 있는 session 공간이 있는데 키값으로 구분하는 -> Security ContextHolder
// 시큐리티가 가지고있는 세션에 들어가는 오브젝트가 정해져있음 -> 오브젝트가 Authentication 타입 객체임
// Authentication 안에 User 정보가 있어야 됨.
// User오브젝트타입 => UserDetails  타입 객체여야함
// 즉
// Security Session => Authentication => UserDetails
// 세션안에는 Authentication 객체가 들어가고 Authentication 안에는 UserDetails 객체가 들어감

// 여기서는 UserDetails를 PrincipalDetail로

@Data
public class PrincipalDetails implements UserDetails, OAuth2User{
	
	private User user; //콤포지션
	private Map<String, Object> attribute;
	
	//일반로그인
	public PrincipalDetails(User user) {
		this.user = user;
	}
	//OAuth 로그인
	public PrincipalDetails(User user, Map<String, Object> attributes) {
		this.user = user;
		this.attribute = attributes;
	}
	
	//해당 User의 권한을 리턴하는 곳
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Collection<GrantedAuthority> collect = new ArrayList<>();
		collect.add(new GrantedAuthority() {
			@Override
			public String getAuthority() {
				return user.getRole();
			}
		});
		return collect;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
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
		//우리 사이트에서 1년동안 회원이 로그인을 안하면 휴먼계정으로 하기로함
		//user.getLoginDate() 가져와서
		//현재시간-로그인시간 => 1년 초과하면 false로 하거나 이런게 쓴다
		//지금은 그런 설정을 안 할거라 다 true로 
		return true;
	}

	@Override
	public Map<String, Object> getAttributes() {
		//이거는 Map<String, Object> 타입인데 이거를 여기에 그대로 넣을 것임
		return attribute;
	}

	@Override
	public String getName() { //중요x 안쓴다 쓸거면 sub을 넣어서
		return null;
	}
	
}
