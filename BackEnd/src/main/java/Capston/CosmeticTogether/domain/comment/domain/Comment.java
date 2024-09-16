package Capston.CosmeticTogether.domain.comment.domain;


import Capston.CosmeticTogether.domain.board.domain.Board;
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
@Table(name = "comment")
public class Comment extends BaseEntity {

    @Column
    private String content;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Board board;
}
