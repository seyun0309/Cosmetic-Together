package Capston.CosmeticTogether.domain.chatMessage.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class ChatRoomResponseDTO {
    private Long chatRoomId;
    private String chatRoomName;
}
