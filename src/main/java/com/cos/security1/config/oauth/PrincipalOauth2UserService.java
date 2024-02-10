package com.cos.security1.config.oauth;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.cos.security1.config.auth.PrincipalDetails;
import com.cos.security1.config.oauth.provider.FacebookUserInfo;
import com.cos.security1.config.oauth.provider.GoogleUserInfo;
import com.cos.security1.config.oauth.provider.NaverUserInfo;
import com.cos.security1.config.oauth.provider.OAuth2UserInfo;
import com.cos.security1.model.User;
import com.cos.security1.repository.UserRepository;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService{
	
	@Autowired
	private BCryptPasswordEncoder bCryptPasswordEncoder;
	@Autowired
	private UserRepository userRepository;
	
	//여기서 후처리가 됨
	//구글로 부터 받은 userRequest 데이터에 대한 후처리되는 함수
	//해당 함수 종료시 @AuthenticationPrincipal 어노테이션이 만들어진다
	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
		System.out.println("userRequest : " + userRequest);
		System.out.println("userRequest : " + userRequest.getClientRegistration()); //registrationid로 어떤 Oauth로 로그인했는지 알 수 있음
		System.out.println("userRequest : " + userRequest.getAccessToken()); //
		System.out.println("userRequest : " + super.loadUser(userRequest).getAttributes()); 
		
		OAuth2User oAuth2User = super.loadUser(userRequest);
		//구글로그인버튼클릭 -> 구글로그인창->로그인완료->code를리턴(OAuth-Client라이브러리가 받음)->AccessToken요청 받음
		//->userRequest정보 ->loadUser함수 호출-> 구글로부터 회원프로필 받음(loadUser함수가 받아줌)
		System.out.println("getAttributes : " + oAuth2User.getAttributes());
		
		//회원가입진행
		OAuth2UserInfo oAuth2UserInfo = null;
		if(userRequest.getClientRegistration().getRegistrationId().equals("google")) {
			System.out.println("구글 로그인 요청");
			oAuth2UserInfo = new GoogleUserInfo(oAuth2User.getAttributes());
		}else if(userRequest.getClientRegistration().getRegistrationId().equals("facebook")) {
			System.out.println("페이스북 로그인 요청");
			oAuth2UserInfo = new FacebookUserInfo(oAuth2User.getAttributes());
		}else if(userRequest.getClientRegistration().getRegistrationId().equals("naver")) {
			System.out.println("네이버 로그인 요청");
			oAuth2UserInfo = new NaverUserInfo((Map)oAuth2User.getAttributes().get("response")); //네이버는 response로 리턴해주니까
		}else {
			System.out.println("구글과 페이스북 네이버 로그인만 가넝합니다...더 노력할게요...");
		}
		
//		String provider = userRequest.getClientRegistration().getRegistrationId(); //google
//		String prodiderId = oAuth2User.getAttribute("sub");
//		String username = provider + "_" + prodiderId;
//		String password = bCryptPasswordEncoder.encode("비밀번호"); //의미없음
//		String email = oAuth2User.getAttribute("email");
//		String role = "ROLE_USER";
		
		String provider = oAuth2UserInfo.getProvider();
		String providerId = oAuth2UserInfo.getProviderId();
		String username = provider + "_" + providerId;
		String password = bCryptPasswordEncoder.encode("비밀번호"); //의미없음
		String email = oAuth2UserInfo.getEmail();
		String role = "ROLE_USER";
		
		User userEntity = userRepository.findByUsername(username);
		
		if(userEntity == null) {
			System.out.println("로그인이 최초입니다. 회원가입을 진행하겠습니다.");
			userEntity = User.builder()
					.username(username)
					.password(password)
					.email(email)
					.role(role)
					.provider(provider)
					.providerId(providerId)
					.build();
			userRepository.save(userEntity);
		} else {
			System.out.println("로그인을 이미 한적이 있습니다. 당신은 자동회원가입이 되어있습니다.");
		}
		
		//이 정보로 강제회원가입시킴
		return new PrincipalDetails(userEntity, oAuth2User.getAttributes()); //이 객체가 이제 Authentication에 들어간다(userDetails, Oauth2User)
	}
}
