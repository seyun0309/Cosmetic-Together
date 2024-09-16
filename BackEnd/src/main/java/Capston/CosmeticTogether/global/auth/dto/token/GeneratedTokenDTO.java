package Capston.CosmeticTogether.global.auth.dto.token;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class GeneratedTokenDTO {
    private String accessToken;
    private String refreshToken;
    private String nickName;
    private String role;
}
