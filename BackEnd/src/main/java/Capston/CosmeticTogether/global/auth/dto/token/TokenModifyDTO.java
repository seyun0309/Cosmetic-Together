package Capston.CosmeticTogether.global.auth.dto.token;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class TokenModifyDTO {
    @NotBlank
    @Schema(description = "Refresh Token")
    private String refreshToken;
}
