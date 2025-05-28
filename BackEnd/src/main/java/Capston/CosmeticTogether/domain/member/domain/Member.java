package Capston.CosmeticTogether.domain.member.domain;

import Capston.CosmeticTogether.domain.follow.domain.Follow;
import Capston.CosmeticTogether.domain.member.dto.request.MemberUpdateRequestDTO;
import Capston.CosmeticTogether.global.common.BaseEntity;
import Capston.CosmeticTogether.global.enums.AuthType;
import Capston.CosmeticTogether.global.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member extends BaseEntity {

    @Column(nullable = false)
    private String userName;

    @Column(length = 20, nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    @JsonIgnore
    private String password;

    @Column(length = 15, nullable = false)
    private String phone;

    @Column(length = 30, nullable = false, unique = true)
    private String nickname;

    @Column
    private String address;

    @Column
    private String profileUrl;

    @Column(length = 100)
    private String statusMsg;

    @Column
    private String backgroundUrl;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private AuthType authType;

    @OneToMany(mappedBy = "follower")
    @JsonIgnore
    private List<Follow> followingList;

    @OneToMany(mappedBy = "following")
    @JsonIgnore
    private List<Follow> followerList;

    @Setter
    private String refreshToken;

    // 초기 데이터 삽입시 사용하는 생성자
    public Member(String userName, String email, String password, String phone, String nickName, String address, Role role, AuthType authType, String profileUrl) {
        this.userName = userName;
        this.email = email;
        this.password = password;
        this.phone = phone;
        this.nickname = nickName;
        this.address = address;
        this.role = role;
        this.authType = authType;
        this.profileUrl = profileUrl;
    }

    public void updateMemberInfo(MemberUpdateRequestDTO memberUpdateRequestDTO, Role role) {
        this.userName = memberUpdateRequestDTO.getUserName();
        this.email = memberUpdateRequestDTO.getEmail();
        this.phone = memberUpdateRequestDTO.getPhone();
        this.nickname = memberUpdateRequestDTO.getNickname();
        this.address = memberUpdateRequestDTO.getAddress();
        this.profileUrl = memberUpdateRequestDTO.getProfile_url();
        this.backgroundUrl = memberUpdateRequestDTO.getBackground_url();
        this.role = role;
    }

    public void updateAddress(String address) {
        this.address = address;
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updatePassword(String hashedPassword) {
        this.password = hashedPassword;
    }
}
