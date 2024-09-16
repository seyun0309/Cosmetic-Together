package Capston.CosmeticTogether.global.auth.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SignUpResponseDTO {
    private String userName;
    private String email;
}
