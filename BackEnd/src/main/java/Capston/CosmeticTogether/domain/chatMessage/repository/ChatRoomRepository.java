package Capston.CosmeticTogether.domain.chatMessage.repository;

import Capston.CosmeticTogether.domain.chatMessage.domain.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {
}
