package Capston.CosmeticTogether.domain.favorites.domain;

import Capston.CosmeticTogether.domain.board.domain.Board;
import Capston.CosmeticTogether.domain.form.domain.Form;
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
@Table(name = "favorites")
public class Favorites extends BaseEntity {

    @JoinColumn(nullable = false)
    @ManyToOne
    private Member member;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Form form;

    @Column(nullable = false)
    private boolean is_valid;

    public void setValid(boolean b) {
        this.is_valid = b;
    }
}
