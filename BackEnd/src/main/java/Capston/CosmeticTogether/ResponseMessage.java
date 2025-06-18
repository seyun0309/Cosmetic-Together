package Capston.CosmeticTogether;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class ResponseMessage {
    private int status;
    private String message;
}
