package Capston.CosmeticTogether.domain.chatMessage.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@Builder
public class ChatMessageResponseDTO {
    private Long senderId;
    private String content;
    private String sendAt;
}
