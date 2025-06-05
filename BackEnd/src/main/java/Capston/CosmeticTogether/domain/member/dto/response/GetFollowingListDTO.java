package Capston.CosmeticTogether.domain.member.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GetFollowingListDTO {
    private String loginMemberName;
    private Long followingMemberId;
    private String nickname;
    private String profileUrl;
    private boolean following;
}
