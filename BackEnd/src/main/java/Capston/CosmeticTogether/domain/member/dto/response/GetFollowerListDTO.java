package Capston.CosmeticTogether.domain.member.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GetFollowerListDTO {
    private String loginMemberName;
    private Long followerMemberId;
    private String nickname;
    private String profileUrl;
}
