package Capston.CosmeticTogether.domain.member.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MemberUpdateRequestDTO {
    private String userName;

    private String email;

    private String phone;

    private String nickname;

    private String address;

    private String profile_url;

    private String status_msg;

    private String background_url;
}
