package com.cos.security1.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

@Controller //view를 리턴하겠다는 의미
public class IndexController {
	
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	
	//정리
	//스프링시큐리티는 시큐리티세션을 가지고있다 (원래의 세션과 별개로)
	//시큐리티세션에 들어갈 수 있는 타입은 Authentication객체만 들어갈 수 있다
	//Authentication을 우리가 꺼내쓰려고 DI로
	//Authentication에 들어갈 수 있는 타입은 2가지이다 - UserDetails타입, OAuth2User타입
	// 	UserDetails타입 - 일반로그인하면 여기에 들어가고
	// 	OAuth2User타입 - OAuth로그인을 하면 여기에
	//타입이 다르므로 처리를 할때 각각해주어야하니까 -> 두 타입을 상속받는 x클래스를 만들어서 처리를 한다
	//즉 x를 Authentication에 담는다
	
	//PrincipalDetailsService여기에서 PrincipalDetails new PrincipalDetail(userEntity)를 담는다
	//그래서 PrincipalDetails클래스를 implements UserDetails, OAuth2User 이렇게 하면 
	//-> 어떤 종류로 로그인을 해도 PrincipalDetails 타입을 사용할 수 있게 된다
	//@AuthenticationPrincipal PrincipalDetail principalDetail
	
	//PrincipalDetail을 왜 만드는가
	//회원가입을 하면 User객체를 받아와야하는데 UserDetails,OAuth2User에는 User를 담고있지않다
	//그래서 PrincipalDetail를 만들어서 User오브젝트를 넣어놨음
	//그리고 UserDetails,OAuth2User를 implement해서 PrincipalDetail를 언제든지 사용가능하게 만든다
	
	@GetMapping("/test/login")
	public @ResponseBody String TestLogin(
			Authentication authentication,
			@AuthenticationPrincipal PrincipalDetails userDetails) { //DI(의존성주입)
		System.out.println("/test/login================");
		System.out.println("authentication : " + authentication.getPrincipal()); //com.cos.security1.config.auth.PrincipalDetail@3c39754c 오브젝트타입임
		PrincipalDetails principalDetail = (PrincipalDetails) authentication.getPrincipal();
		System.out.println("principalDetail.getUser()" + principalDetail.getUser());
		//User(id=3, username=user, password=$2a$10$siTCok.0l9pQsCnEW6PgCOmEDX9lElJNAD/hV5Eh6tgjkndaWPvvi, email=user@user.user, role=ROLE_USER, provider=null, providerId=null, createDate=2024-02-05 20:10:12.90676)
		
		//@AuthenticationPrincipal 어노테이션을 통해서 세션정보에 접근이 가능하다
		//userDetails타입을 가지고 있다
		System.out.println("userDetails : " + userDetails.getUser());
		
		//즉 User오브젝트 찾는 방법 두가지
		//1. Authentication authentication으로 의존성주입->다운캐스팅을 통해서
		//2. @AuthenticationPrincipal를 통해서 찾기
		//principalDetail.getUser(), userDetails.getUser()는 같은 정보를 가지고있다
		
		return "세션 정보 확인하기" + principalDetail.getUser();
	}
	
	@GetMapping("/test/oauth/login")
	public @ResponseBody String TestOAuthLogin(
			Authentication authentication,
			@AuthenticationPrincipal OAuth2User oAuth) { //DI(의존성주입)
		System.out.println("/test/oauth/login================");
		System.out.println("authentication : " + authentication.getPrincipal()); 
		OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
		System.out.println("principalDetail.getUser()" + oAuth2User.getAttributes());
		System.out.println("oAuth : " + oAuth.getAttributes());
		
		//구글로그인으로 하면 Authentication authentication, @AuthenticationPrincipal OAuth2User oAuth로 정보를 받아온다
		
		return "oauth 세션 정보 확인하기" + oAuth2User.getAttributes();
	}
	
	//localhost:8080/
	//localhost:8080
	@GetMapping({"","/"})
	public String index() {
		//머스테치 - 스프링이 권장하고 있는 템플릿
		//기본폴더 src/main/resources
		//뷰리졸버 설정 : templates (prefix), .mustache (suffix) - yml에 설정되어있음 삭제해도 됨 디폴트라
		return "index"; //src/main/resources/templates/index.mustache 이것을 찾음 아무 설정이 없을 때
	}
	
	//이렇게 상속을 받아서 메서드를 오버라이딩하는 이유는
	//PrincipalDetailsService implements UserDetailsService
	//PrincipalOauth2UserService extends DefaultOAuth2UserService
	//1. 두 메서더의 return타입을 PrincipalDetails로 묶기 위해서
	//		각각 리턴타입을 UserDetails, OAuth2User로 할때는 굳이 오버라이딩 할 필요는 없다
	//2. PrincipalOauth2UserService를 이용해서 OAuth로 로그인을 할때 회원가입을 시키기 위해서
	
	//OAuth 로그인을 해도 PrincipalDetails
	//일반 로그인을 해도 PrincipalDetails
	@GetMapping("/user")
	public @ResponseBody String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
		System.out.println("principalDetails : " + principalDetails.getUser());
		
		//일반
		//principalDetails : User(id=3, username=user, password=$2a$10$siTCok.0l9pQsCnEW6PgCOmEDX9lElJNAD/hV5Eh6tgjkndaWPvvi, email=user@user.user, role=ROLE_USER, provider=null, providerId=null, createDate=2024-02-05 20:10:12.90676)
		//구글

		return "user";
	}
	
	@GetMapping("/admin")
	public @ResponseBody String admin() {
		return "admin";
	}
	
	@GetMapping("/manager")
	public @ResponseBody String manager() {
		return "manager";
	}
	
	//스프링시큐리티가 낚아채감 - securityConfig 생성하면 작동 안함.
	@GetMapping("/loginForm")
	public String loginForm() {
		return "loginForm";
	}
	
	@GetMapping("/joinForm")
	public String joinForm() {
		return "joinForm";
	}
	
	@PostMapping("/join")
	public String join(User user) {
		System.out.println(user);
		user.setRole("USER");
		String rawPassword = user.getPassword();
		String encPassword = bCryptPasswordEncoder.encode(rawPassword);
		user.setPassword(encPassword);
		userRepository.save(user); //회원가입하기
		//비밀번호1234 => 시큐리티 로그인 할 수 없음 -> 패스워드가 암호화가 안되어있음
		return "redirect:/loginForm";
	}
	
	@Secured("ROLE_ADMIN")//권한 한개만 설정할때
	@GetMapping("/info")
	public @ResponseBody String info() {
		return "개인정보";
	}
	
	@PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')") //hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN') 여러개 걸때 사용 //@PostAuthorize이거는 잘 안씀
	@GetMapping("/data")
	public @ResponseBody String data() {
		return "data";
	}
	
//	@GetMapping("/joinProc")
//	public @ResponseBody String joinProc() {
//		return "회원가입 완료됨!";
//	}
}
