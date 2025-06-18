package Capston.CosmeticTogether.domain.chatMessage.repository;

import Capston.CosmeticTogether.domain.chatMessage.domain.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByChatRoomIdOrderByCreatedAt(Long chatRoomId);
}
