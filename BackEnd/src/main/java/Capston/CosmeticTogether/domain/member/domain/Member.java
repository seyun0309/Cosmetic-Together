package Capston.CosmeticTogether.domain.member.domain;

import Capston.CosmeticTogether.global.common.BaseEntity;
import Capston.CosmeticTogether.global.enums.AuthType;
import Capston.CosmeticTogether.global.enums.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "member")
public class Member extends BaseEntity {

    @Column(nullable = false)
    private String userName;

    @Column(length = 20, nullable = false)
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
    private String profile_url;

    @Column(length = 100)
    private String status_msg;

    @Column
    private String background_url;

    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private AuthType authType;

    @Setter
    private String refreshToken;
}
