package Capston.CosmeticTogether.global.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ReissuedTokenResponseDTO {
    private String accessToken;
    private String refreshToken;
}
