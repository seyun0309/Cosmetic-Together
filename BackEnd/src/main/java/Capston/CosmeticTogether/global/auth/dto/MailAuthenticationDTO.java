package Capston.CosmeticTogether.global.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailAuthenticationDTO {

    @Email
    @NotNull
    private String email;

    @NotNull
    private String authCode;
}
