package Capston.CosmeticTogether.domain.likes.domain;


import Capston.CosmeticTogether.domain.board.domain.Board;
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
@Table(name = "likes")
public class Likes extends BaseEntity {

    @JoinColumn(columnDefinition = "varchar(100)",nullable = false)
    @ManyToOne
    private Member member;

    @JoinColumn(columnDefinition = "varchar(100)",nullable = false)
    @ManyToOne
    private Board board;

    @Column(nullable = false)
    private boolean is_valid;

}
