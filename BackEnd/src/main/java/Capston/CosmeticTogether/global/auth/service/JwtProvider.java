package Capston.CosmeticTogether.global.auth.service;


import Capston.CosmeticTogether.domain.member.domain.Member;
import Capston.CosmeticTogether.domain.member.repository.MemberRepository;
import Capston.CosmeticTogether.global.auth.dto.security.SecurityMemberDTO;
import Capston.CosmeticTogether.global.auth.dto.token.GeneratedTokenDTO;
import Capston.CosmeticTogether.global.config.JwtProperties;
import Capston.CosmeticTogether.global.error.exception.BusinessException;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

import static Capston.CosmeticTogether.global.enums.ErrorCode.MEMBER_NOT_FOUND;
import static Capston.CosmeticTogether.global.enums.ErrorCode.MISMATCH_REFRESH_TOKEN;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtProvider {
    private final JwtProperties jwtConfig;
    private final MemberRepository memberRepository;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private final HttpServletRequest request;

    @Getter
    private Key signingKey;
    private JwtParser jwtParser;
    private static final Long ACCESS_TOKEN_PERIOD = 1000L * 60L * 60L; // 1시간
    private static final Long REFRESH_TOKEN_PERIOD = 1000L * 60L * 60L * 24L * 14L; // 2주

    @PostConstruct
    protected void init() {
        String secretKey = Base64.getEncoder().encodeToString(jwtConfig.getSecret().getBytes());
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secretKey);
        signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        jwtParser = Jwts.parserBuilder().setSigningKey(signingKey).build();
    }

    @Transactional
    public GeneratedTokenDTO generateTokens(SecurityMemberDTO securityMemberDTO) {
        String accessToken = generateToken(securityMemberDTO, ACCESS_TOKEN_PERIOD);
        String refreshToken = generateToken(securityMemberDTO, REFRESH_TOKEN_PERIOD);
        String nickName = securityMemberDTO.getNickName();

        saveRefreshToken(securityMemberDTO.getId(), refreshToken);

        return GeneratedTokenDTO.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .nickName(nickName)
                .role(memberRepository.findRoleById(securityMemberDTO.getId()))
                .build();
    }

    private String generateToken(SecurityMemberDTO securityMemberDTO, Long tokenPeriod) {
        Claims claims = Jwts.claims().setSubject("id");
        claims.put("email", securityMemberDTO.getEmail());
        claims.put("role", securityMemberDTO.getRole().name());
        claims.setId(String.valueOf(securityMemberDTO.getId()));
        Date now = new Date();

        return Jwts.builder().setClaims(claims).setIssuedAt(now).setExpiration(new Date(now.getTime() + tokenPeriod)).signWith(signingKey, signatureAlgorithm).compact();
    }

    @Transactional
    public GeneratedTokenDTO reissueToken(String refreshToken) {
        GeneratedTokenDTO generatedTokenDTO;
        String reissuedRefreshToken;
        String reissuedAccessToken;
        Claims claims = verifyToken(refreshToken);
        SecurityMemberDTO securityMemberDTO = SecurityMemberDTO.fromClaims(claims);

        Optional<Member> findMember = memberRepository.findById(securityMemberDTO.getId());

        if (findMember.isEmpty()) {
            throw new BusinessException(MEMBER_NOT_FOUND);
        }

        Member member = findMember.get();

        if (member.getRefreshToken() == null) {
            throw new BusinessException(MISMATCH_REFRESH_TOKEN);
        }

        if (!member.getRefreshToken().equals(refreshToken)) {
            throw new BusinessException(MISMATCH_REFRESH_TOKEN);
        }

        reissuedRefreshToken = generateToken(securityMemberDTO, REFRESH_TOKEN_PERIOD);
        reissuedAccessToken = generateToken(securityMemberDTO, ACCESS_TOKEN_PERIOD);
        member.setRefreshToken(refreshToken);

        memberRepository.save(member);

        generatedTokenDTO = GeneratedTokenDTO.builder().accessToken(reissuedAccessToken).refreshToken(reissuedRefreshToken).build();

        return generatedTokenDTO;
    }

    public Claims verifyToken(String token) {
        try {
            return jwtParser.parseClaimsJws(token).getBody();
        } catch (io.jsonwebtoken.security.SecurityException | MalformedJwtException e) {
            throw new JwtException("잘못된 JWT 서명입니다.");
        } catch (ExpiredJwtException e) {
            throw new JwtException("만료된 JWT 토큰입니다.");
        } catch (UnsupportedJwtException e) {
            throw new JwtException("지원되지 않는 JWT 토큰입니다.");
        } catch (IllegalArgumentException e) {
            throw new JwtException("잘못된 JWT 토큰입니다.");
        }
    }

    private void saveRefreshToken(Long id, String refreshToken) {
        Optional<Member> findMember = memberRepository.findById(id);
        findMember.ifPresent(member -> memberRepository.updateRefreshToken(member.getId(), refreshToken));
    }

    public long extractIdFromTokenInHeader() {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            String token = header.substring(7);
            return extractIdFromToken(token);
        } else {
            throw new IllegalArgumentException("Token not found in header.");
        }
    }
    public long extractIdFromToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(signingKey).parseClaimsJws(token);
            String idString = claims.getBody().get("jti", String.class);
            return Long.parseLong(idString);
        } catch (JwtException | IllegalArgumentException | NullPointerException e) {
            throw new IllegalArgumentException("Error extracting ID from token.");
        }
    }
}
