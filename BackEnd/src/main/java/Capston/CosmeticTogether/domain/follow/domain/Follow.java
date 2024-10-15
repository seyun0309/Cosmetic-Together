package Capston.CosmeticTogether.domain.follow.domain;


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
@Table(name = "follow")
public class Follow extends BaseEntity {

    @JoinColumn(name = "follower_member_id")
    @ManyToOne
    private Member follower;

    @JoinColumn(name = "following_member_id")
    @ManyToOne
    private Member following;
}
