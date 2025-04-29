package Capston.CosmeticTogether.domain.chatMessage.domain;


import Capston.CosmeticTogether.domain.member.domain.Member;
import Capston.CosmeticTogether.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "message")
public class ChatMessage extends BaseEntity {

    @ManyToOne
    @JoinColumn(nullable = false)
    private ChatRoom chatRoom;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Member sender;

    @Column(nullable = false)
    private String content;

}
