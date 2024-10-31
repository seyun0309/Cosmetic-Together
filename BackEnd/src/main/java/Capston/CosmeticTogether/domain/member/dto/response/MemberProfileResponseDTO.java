package Capston.CosmeticTogether.domain.member.dto.response;

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
public class MemberProfileResponseDTO {
    @NotBlank
    private String userName;

    @NotBlank
    @Email
    private String email;

    //TODO 비밀번호를 넣을지 생각해봐야 할듯
//    private String password;
    @NotBlank
    @Pattern(regexp = "^01[0-9]-\\d{3,4}-\\d{4}$", message = "유효한 전화번호를 입력하세요")
    private String phone;

    @NotBlank
    @Size(min = 2, max = 10, message = "닉네임은 2 ~ 10자 이하로 작성해주세요")
    private String nickname;

    @NotBlank
    private String address;

    @NotBlank
    private String profile_url;

    @NotBlank
    @Size(min = 1, max = 100, message = "상태 메세지는 2 ~ 100자 이하로 작성해주세요")
    private String status_msg;

    @NotBlank
    private String background_url;
}
