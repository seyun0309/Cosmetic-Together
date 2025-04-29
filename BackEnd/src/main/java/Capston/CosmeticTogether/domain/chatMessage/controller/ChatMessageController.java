package Capston.CosmeticTogether.domain.chatMessage.controller;

import Capston.CosmeticTogether.domain.chatMessage.domain.ChatRoom;
import Capston.CosmeticTogether.domain.chatMessage.dto.request.ChatMessageRequestDTO;
import Capston.CosmeticTogether.domain.chatMessage.dto.response.ChatMessageResponseDTO;
import Capston.CosmeticTogether.domain.chatMessage.service.ChatService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "채팅", description = "")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/chat")
public class ChatMessageController {
    private final ChatService chatService;

    @PostMapping("/send")
    public ChatMessageResponseDTO sendMessage(
            @AuthenticationPrincipal Long senderId,
            @RequestParam Long chatRoomId,
            @RequestBody ChatMessageRequestDTO requestDTO) {

        return chatService.sendMessage(senderId, requestDTO);
    }

    @GetMapping("/{chatRoomId}")
    public ResponseEntity<List<ChatMessageResponseDTO>> getChatMessages(
            @PathVariable Long chatRoomId) {
        return ResponseEntity.ok(chatService.getChatMessages(chatRoomId));
    }

    @GetMapping("/rooms")
    public ResponseEntity<List<ChatRoom>> getMyChatRooms(
            @AuthenticationPrincipal Long memberId) {
        return ResponseEntity.ok(chatService.getMyChatRooms(memberId));
    }

    @DeleteMapping("/{chatRoomId}")
    public ResponseEntity<Void> exitChatRoom(
            @AuthenticationPrincipal Long memberId,
            @PathVariable Long chatRoomId) {
        chatService.exitChatRoom(memberId, chatRoomId);
        return ResponseEntity.ok().build();
    }
}
