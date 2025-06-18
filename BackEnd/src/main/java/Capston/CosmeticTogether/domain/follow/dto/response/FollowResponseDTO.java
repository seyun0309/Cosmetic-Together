package Capston.CosmeticTogether.domain.follow.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class FollowResponseDTO {
    private boolean isValid;
    private String followingNickName;
}
