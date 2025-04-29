package Capston.CosmeticTogether.domain.chatMessage.dto.request;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ChatMessageRequestDTO {
    private Long senderId; //TODO 배포시 지우기
    private Long chatRoomId;
    private String content;
}
