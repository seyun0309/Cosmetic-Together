package Capston.CosmeticTogether.domain.follow.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class GetFollowAndFollowingMemberDTO {
    private Long memberId;
    private String nickname;
    private String profileUrl;
}
