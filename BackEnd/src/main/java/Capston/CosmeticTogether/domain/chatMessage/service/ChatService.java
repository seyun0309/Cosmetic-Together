package Capston.CosmeticTogether.domain.chatMessage.service;

import Capston.CosmeticTogether.domain.chatMessage.domain.ChatMessage;
import Capston.CosmeticTogether.domain.chatMessage.domain.ChatRoom;
import Capston.CosmeticTogether.domain.chatMessage.domain.MemberChatRoom;
import Capston.CosmeticTogether.domain.chatMessage.dto.request.ChatMessageRequestDTO;
import Capston.CosmeticTogether.domain.chatMessage.dto.response.ChatMessageResponseDTO;
import Capston.CosmeticTogether.domain.chatMessage.repository.ChatMessageRepository;
import Capston.CosmeticTogether.domain.chatMessage.repository.ChatRoomRepository;
import Capston.CosmeticTogether.domain.chatMessage.repository.MemberChatRoomRepository;
import Capston.CosmeticTogether.domain.member.domain.Member;
import Capston.CosmeticTogether.domain.member.repository.MemberRepository;
import Capston.CosmeticTogether.global.enums.ErrorCode;
import Capston.CosmeticTogether.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final MemberChatRoomRepository memberChatRoomRepository;
    private final MemberRepository memberRepository; // Member 찾아야 하니까 필요함

    @Transactional
    public ChatMessageResponseDTO sendMessage(Long senderId, ChatMessageRequestDTO requestDTO) {
        Long chatRoomId = requestDTO.getChatRoomId();
        String content = requestDTO.getContent();

        Member sender = memberRepository.findById(senderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.MEMBER_NOT_FOUND));

        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseGet(() -> {
                    ChatRoom newRoom = ChatRoom.builder()
                            .roomName("예시").build();
                    chatRoomRepository.save(newRoom);

                    // 채팅방에 사용자 추가
                    MemberChatRoom memberChatRoom = MemberChatRoom.builder()
                            .member(sender)
                            .chatRoom(newRoom)
                            .build();
                    memberChatRoomRepository.save(memberChatRoom);

                    return newRoom;
                });

        ChatMessage chatMessage = ChatMessage.builder()
                .chatRoom(chatRoom)
                .sender(sender)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();

        chatMessageRepository.save(chatMessage);

        return ChatMessageResponseDTO.builder()
                .senderId(senderId)
                .content(content)
                .sendAt(chatMessage.getCreatedAt().format(DateTimeFormatter.ofPattern("HH:mm")))
                .build();
    }


    @Transactional(readOnly = true)
    public List<ChatMessageResponseDTO> getChatMessages(Long chatRoomId) {
        return chatMessageRepository.findByChatRoomIdOrderByCreatedAt(chatRoomId)
                .stream()
                .map(m -> ChatMessageResponseDTO.builder()
                        .senderId(m.getSender().getId())
                        .content(m.getContent())
                        .sendAt(m.getCreatedAt().format(DateTimeFormatter.ofPattern(("HH:mm"))))
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<ChatRoom> getMyChatRooms(Long memberId) {
        return memberChatRoomRepository.findByMemberId(memberId)
                .stream()
                .map(MemberChatRoom::getChatRoom)
                .collect(Collectors.toList());
    }

    @Transactional
    public void exitChatRoom(Long memberId, Long chatRoomId) {
        MemberChatRoom memberChatRoom = memberChatRoomRepository.findByMemberIdAndChatRoomId(memberId, chatRoomId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CHATROOM_NOT_FOUND));
        memberChatRoomRepository.delete(memberChatRoom);
    }
}
