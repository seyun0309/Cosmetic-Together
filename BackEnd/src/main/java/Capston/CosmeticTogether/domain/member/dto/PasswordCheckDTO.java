package Capston.CosmeticTogether.domain.member.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordCheckDTO {
    @NotBlank(message = "비밀번호를 입력해주세요")
    private String password;
}
