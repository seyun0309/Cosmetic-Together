package Capston.CosmeticTogether.domain.board.domain;

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
@Table(name = "board")
public class Board extends BaseEntity {

    @Column
    private String description;

    @Column
    private String board_url;

    @Column(nullable = false)
    private int favorite_count;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Member member;
}
