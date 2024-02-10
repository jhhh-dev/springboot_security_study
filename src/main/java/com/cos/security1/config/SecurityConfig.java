package com.cos.security1.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import com.cos.security1.config.oauth.PrincipalOauth2UserService;

@Configuration
@EnableWebSecurity //스프링 시큐리티 필터가 스프링 필터체인에 등록이 됩니다
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true) //secured 어노테이션 활성화 @Secured("ADMIN") 사용됨//preAuthorize @PostAuthorize 활성화
public class SecurityConfig{
	
	@Autowired
	private PrincipalOauth2UserService principalOauth2UserService;
	
	//해당 메서드의 리턴되는 오브젝트를 IoC로 등록해준다
	//@Bean
	//public BCryptPasswordEncoder encodePwd() {
	//	return new BCryptPasswordEncoder();
	//}

	
    @Bean
//    @Order(SecurityProperties.BASIC_AUTH_ORDER)
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//    	http.authorizeHttpRequests().requestMatchers("/**").access("has");
    	http
            // 토큰을 사용하기 때문에 csrf 설정 disable
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(requests -> requests
                    .requestMatchers("/user/**").authenticated()
                    .requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGER")
                    .requestMatchers("/admin/**").hasRole("ADMIN")
                    .anyRequest().permitAll());
    	
    	http.formLogin().loginPage("/loginForm")
    		.loginProcessingUrl("/login") //login 주소가 호출되면 시큐리티가 낚아채서 대신 로그인 해줌
    		.defaultSuccessUrl("/"); //login 성공 시 이동 주소 - 요청했던 페이지로 이동함...
    		
    	//1. 코드받기(인증이됐다는뜻-구글에 로그인이된 정상적인 사용자라는 뜻) 
    	//2. 엑세스토큰받기(권한이 생김 프로필 접근권한)
    	//3. 사용자프로필 정보를 가져오고
    	//4-1. 그 정보를 토대로 회원자입을 자동으로 진행시키기도함-구글이 가지고있는 정보만 필요로하면 바로 회원가입
    	//4-2. (이메일, 전화번호, 이름, 아이디) 쇼핑몰이면 -> (집주소 vip등급 이런 추가정보가 필요하면) 추가정보 넣는 창으로
    	
    	// 구글에서 로그인이 되면 - Tip 코드x를 받아오는게 아니라 코드를 이용해서 엑세스토큰+사용자프로필정보를 한번에 받아온다
    	// 구글로 로그인을 하면 사용자의 username과 password는 우리 서버만 아는 것으로 넣으면 됨 사용자는 로그인할 때 사용하지 않으니까
    	// username = google_110028638078379041009 -> 이렇게 sub 넣으면 중복되지않으니까
    	// password = 겟인데어 -> 암호화해서 넣을것음 그냥 서버만 알게 넣는 것임 null을 넣을 수는 없으니까
    	// email = 
    	// role = ROLE_USER
    	// provider = google -> 구글 로그인인 거 알아야하니
    	// providerId = sub
    	http.oauth2Login().loginPage("/loginForm")
    		.userInfoEndpoint()
    		.userService(principalOauth2UserService); //구글 로그인이 완료된 뒤의 후처리가 필요함
    	
        return http.build();
    }

}
