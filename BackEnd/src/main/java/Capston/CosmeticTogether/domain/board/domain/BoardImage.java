package Capston.CosmeticTogether.domain.board.domain;

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
@Table(name = "board_img")
public class BoardImage extends BaseEntity {

    @Column(nullable = false)
    private String imageUrl;

    @ManyToOne
    @JoinColumn
    private Board board;

}
