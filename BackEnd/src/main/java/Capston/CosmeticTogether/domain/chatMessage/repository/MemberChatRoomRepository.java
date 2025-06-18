package Capston.CosmeticTogether.domain.chatMessage.repository;

import Capston.CosmeticTogether.domain.chatMessage.domain.ChatRoom;
import Capston.CosmeticTogether.domain.chatMessage.domain.MemberChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface MemberChatRoomRepository extends JpaRepository<MemberChatRoom, Long> {
    List<MemberChatRoom> findByMemberId(Long memberId);
    Optional<MemberChatRoom> findByMemberIdAndChatRoomId(Long memberId, Long chatRoomId);
}
