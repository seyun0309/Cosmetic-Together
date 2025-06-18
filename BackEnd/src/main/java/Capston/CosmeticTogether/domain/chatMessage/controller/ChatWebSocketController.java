package Capston.CosmeticTogether.domain.chatMessage.controller;

import Capston.CosmeticTogether.domain.chatMessage.dto.request.ChatMessageRequestDTO;
import Capston.CosmeticTogether.domain.chatMessage.dto.response.ChatMessageResponseDTO;
import Capston.CosmeticTogether.domain.chatMessage.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatWebSocketController {
    private final ChatService chatService;

    @MessageMapping("/chat.sendMessage")
    @SendTo("/topic/chatroom")
    public ChatMessageResponseDTO sendMessage(@Payload ChatMessageRequestDTO requestDTO) {
        return chatService.sendMessage(
                requestDTO.getSenderId(),
                requestDTO

                // @AuthenticationPrincipal Long senderId
        );
    }
}
