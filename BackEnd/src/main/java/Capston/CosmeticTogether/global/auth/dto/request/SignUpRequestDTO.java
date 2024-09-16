package Capston.CosmeticTogether.global.auth.dto.request;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SignUpRequestDTO {
    @NotBlank
    private String userName;

    @NotBlank
    @Email
    private String email;

    @NotBlank
    @Pattern(regexp = "^[a-zA-Z0-9!@#$%^&*]{8,16}$", message = "비밀번호는 8~16자의 영문 대소문자, 숫자, 특수문자로 이루어져야 합니다.")
    private String password;

    @Pattern(regexp = "^01[0-9]-\\d{3,4}-\\d{4}$", message = "유효한 전화번호를 입력하세요")
    private String phone;

    @NotBlank
    @Size(min = 2, max = 10, message = "닉네임은 2 ~ 10자 이하로 작성해주세요")
    private String nickname;

    private String address;
}
