package Capston.CosmeticTogether.domain.board.domain;

import Capston.CosmeticTogether.domain.comment.domain.Comment;
import Capston.CosmeticTogether.domain.favorites.domain.Favorites;
import Capston.CosmeticTogether.domain.form.domain.Form;
import Capston.CosmeticTogether.domain.member.domain.Member;
import Capston.CosmeticTogether.global.common.BaseEntity;
import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @OneToMany(mappedBy = "board")
    @JsonIgnore
    private List<Comment> commentList;

    // 더미테이블 삽입시 사용되는 생성자
    public Board(String description, List<BoardImage> boardImages, Member member) {
        this.description = description;
        this.boardImages = boardImages;
        this.member = member;
    }

    public void update(String description) {
        this.description = description;
    }
}
