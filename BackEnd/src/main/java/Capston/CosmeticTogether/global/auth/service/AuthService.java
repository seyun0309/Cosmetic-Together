package Capston.CosmeticTogether.global.auth.service;

import Capston.CosmeticTogether.domain.member.domain.Member;
import Capston.CosmeticTogether.domain.member.repository.MemberRepository;
import Capston.CosmeticTogether.global.auth.dto.kakao.KakaoProfile;
import Capston.CosmeticTogether.global.auth.dto.request.LoginRequestDTO;
import Capston.CosmeticTogether.global.auth.dto.request.SignUpRequestDTO;
import Capston.CosmeticTogether.global.auth.dto.response.SignUpResponseDTO;
import Capston.CosmeticTogether.global.auth.dto.security.SecurityMemberDTO;
import Capston.CosmeticTogether.global.auth.dto.token.GeneratedTokenDTO;
import Capston.CosmeticTogether.global.auth.dto.token.OAuthToken;
import Capston.CosmeticTogether.global.enums.AuthType;
import Capston.CosmeticTogether.global.enums.ErrorCode;
import Capston.CosmeticTogether.global.enums.Role;
import Capston.CosmeticTogether.global.error.exception.BusinessException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Value("${spring.security.oauth2.client.registration.kakao.client-id}")
    private String clientId;

    @Transactional
    public GeneratedTokenDTO kakaoLogin(String code) {
        RestTemplate rt = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8");

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.add("grant_type", "authorization_code");
        params.add("client_id", clientId);
        params.add("redirect_uri", "http://localhost:8080/auth/kakao/callback");
        params.add("code", code);

        // HttpHeader 와 HttpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoTokenRequest =
                new HttpEntity<>(params, headers); // header 와 body 값을 가지고 있는 entity 값이 된다.

        // Http 요청하기 - Post 방식으로 - 그리고 Response 변수의 응답 받음.
        ResponseEntity<String> response = rt.exchange(
                "https://kauth.kakao.com/oauth/token",
                HttpMethod.POST,
                kakaoTokenRequest,
                String.class // String 타입으로 응답 데이터를 받겠다.
        );

        // Gson, Json, Simple, ObjectMapper라이브러리를 사용하여 자바객체로 만들 수 있다.
        ObjectMapper objectMapper = new ObjectMapper();
        OAuthToken oauthToken = null;
        try {
            oauthToken = objectMapper.readValue(response.getBody(), OAuthToken.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        RestTemplate rt2 = new RestTemplate();

        // HttpHeader 오브젝트 생성
        HttpHeaders headers2 = new HttpHeaders();
        headers2.add("Authorization", "Bearer " + oauthToken.getAccess_token());
        headers2.add("Content-type", "application/x-www-form-urlencoded;charset=utf-8"); // 내가 지금 전송할 body data 가
        // key=velue 형임을 명시

        // HttpHeader 와 HttpBody를 하나의 오브젝트에 담기
        HttpEntity<MultiValueMap<String, String>> kakaoProfileRequest = new HttpEntity<>(headers2);

        // Http 요청하기 - Post 방식으로 - 그리고 Response 변수의 응답 받음.
        ResponseEntity<String> response2 = rt2.exchange("https://kapi.kakao.com/v2/user/me", HttpMethod.POST,
                kakaoProfileRequest, String.class // String 타입으로 응답 데이터를 받겠다.
        );

        // Gson, Json, Simple, ObjectMapper
        ObjectMapper objectMapper2 = new ObjectMapper();
        KakaoProfile kakaoProfile = null;
        try {
            kakaoProfile = objectMapper2.readValue(response2.getBody(), KakaoProfile.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        Member member = Member.builder()
                .userName(kakaoProfile.getProperties().getNickname()) // 사용자 닉네임을 userName에 저장
                .email("") // 이메일을 email 필드에 저장
                .password(UUID.randomUUID().toString()) // 비밀번호는 더미값 혹은 UUID로 설정
                .phone("")
                .nickname(kakaoProfile.getProperties().getNickname()) // 카카오 닉네임을 nickname에 저장
                .profile_url(kakaoProfile.getProperties().getProfile_image()) // 프로필 이미지를 profile_url에 저장
                .role(Role.GUEST) // Role은 일반 유저로 설정
                .authType(AuthType.KAKAO) // AuthType은 카카오로 설정
                .build();

        memberRepository.save(member);

        return GeneratedTokenDTO.builder()
                .accessToken(oauthToken.getAccess_token())
                .refreshToken(oauthToken.getRefresh_token())
                .nickName(member.getNickname())
                .role(member.getRole().toString())
                .build();
    }
    @Transactional
    public SignUpResponseDTO signUp(SignUpRequestDTO signUpRequestDTO) {
        String hashedPassword = passwordEncoder.encode(signUpRequestDTO.getPassword());
        Member member = Member.builder()
                .userName(signUpRequestDTO.getUserName())
                .email(signUpRequestDTO.getEmail())
                .password(hashedPassword)
                .phone(signUpRequestDTO.getPhone())
                .nickname(signUpRequestDTO.getNickname())
                .address(signUpRequestDTO.getAddress())
                .role(Role.USER)
                .authType(AuthType.REGULAR)
                .build();

        memberRepository.save(member);

        return SignUpResponseDTO.builder()
                .userName(member.getUserName())
                .email(member.getEmail())
                .build();
    }
//
//    public boolean checkLoginIdDuplicate(String LoginId) {
//        return memberRepository.existsBy(LoginId);

//    }

    public boolean checkNickNameDuplicate(String nickName) {
        return memberRepository.existsByNickname(nickName);
    }

    @Transactional
    public GeneratedTokenDTO login(LoginRequestDTO loginRequestDTO) {
        Optional<Member> findLoginId = memberRepository.findByEmail(loginRequestDTO.getEmail());

        if (findLoginId.isPresent()) {
            Member member = findLoginId.get();

            if (passwordEncoder.matches(loginRequestDTO.getPassword(), member.getPassword())) {
                SecurityMemberDTO securityMemberDTO = SecurityMemberDTO.builder()
                        .id(member.getId())
                        .userName(member.getUserName())
                        .email(member.getEmail())
                        .nickName(member.getNickname())
                        .role(member.getRole())
                        .build();

                return jwtProvider.generateTokens(securityMemberDTO);
            } else {
                throw new BusinessException(ErrorCode.INVALID_PASSWORD);
            }
        } else {
            throw new BusinessException(ErrorCode.MEMBER_NOT_FOUND);
        }
    }

    @Transactional
    public void logout() {
        long currentUserId = jwtProvider.extractIdFromTokenInHeader();
        Member member = memberRepository.findById(currentUserId).orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        member.setRefreshToken(null);
        memberRepository.save(member);
    }
}
