package Capston.CosmeticTogether.domain.board.domain;

import Capston.CosmeticTogether.domain.favorites.domain.Favorites;
import Capston.CosmeticTogether.domain.form.domain.Form;
import Capston.CosmeticTogether.domain.member.domain.Member;
import Capston.CosmeticTogether.global.common.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;

@SuperBuilder
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "board")
public class Board extends BaseEntity {

    @Column
    private String description;

    @Setter
    @OneToMany(mappedBy = "board")
    private List<BoardImage> boardImages;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Member member;

    public void update(String description) {
        this.description = description;
    }
}
