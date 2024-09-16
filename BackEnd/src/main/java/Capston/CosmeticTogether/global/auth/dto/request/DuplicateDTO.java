package Capston.CosmeticTogether.global.auth.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class DuplicateDTO {
    @Getter
    @Setter
    public static class Email {
        @JsonProperty("email")
        private String email;
    }

    @Getter
    @Setter
    public static class NickName {
        @JsonProperty("nickName")
        private String nickName;
    }
}
