package com.cos.security1.config.oauth.provider;

//구글 페이스북 등 보내주는 정보와 이름이 다르므로
public interface OAuth2UserInfo {
	String getProviderId();
	String getProvider();
	String getEmail();
	String getName();
	
	//OAuth2-Client에서 제공해주는 Provider가 있음
	//Provider에서는 구글 페이스북 트위터 등을 제공해준다
	//그런데 네이버는 기본적으로 제공해주는 Provider가 아니다 -> 그래서 따로 등록을 해줘야한다 ->applicaion.yml에
	//authorization-grant-type: authorization_code -> 지금 우리가 하는 것은 Authorization Code Grant 방식임 지금
	//redirect-uri: http://localhost:8080/login/oauth2/code/naver -> 다른로그인 구글이나 이런거는 고정이라 안적어도 되는데 지금 네이버는 지원을 안해주니까
	


	
	
}
