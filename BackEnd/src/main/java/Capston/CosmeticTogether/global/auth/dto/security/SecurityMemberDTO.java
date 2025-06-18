package Capston.CosmeticTogether.global.auth.dto.security;

import Capston.CosmeticTogether.global.enums.Role;
import io.jsonwebtoken.Claims;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class SecurityMemberDTO {
    private final Long id;
    private final String email;
    private final String nickName;
    private final Role role;

    public static SecurityMemberDTO fromClaims(Claims claims) {
        return SecurityMemberDTO.builder()
                .id(Long.valueOf(claims.getId()))
                .email(claims.get("email", String.class))
                .nickName(claims.get("nickName", String.class))
                .role(Role.fromValue(claims.get("role", String.class)))
                .build();
    }
}
