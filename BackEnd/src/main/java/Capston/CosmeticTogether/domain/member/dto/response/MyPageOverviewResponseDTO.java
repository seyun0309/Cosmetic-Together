package Capston.CosmeticTogether.domain.member.dto.response;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MyPageOverviewResponseDTO {
    private String profileUrl;
    private String nickName;
}
