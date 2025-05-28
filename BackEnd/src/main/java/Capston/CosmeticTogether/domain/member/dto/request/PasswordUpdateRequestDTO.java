package Capston.CosmeticTogether.domain.member.dto.request;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PasswordUpdateRequestDTO {
    private String password;
}
