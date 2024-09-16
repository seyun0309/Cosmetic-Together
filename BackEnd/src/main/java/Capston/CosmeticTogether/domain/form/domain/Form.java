package Capston.CosmeticTogether.domain.form.domain;

import Capston.CosmeticTogether.domain.member.domain.Member;
import Capston.CosmeticTogether.global.common.BaseEntity;
import Capston.CosmeticTogether.global.enums.FormStatus;
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
@Table(name = "form")
public class Form extends BaseEntity {

    @JoinColumn(nullable = false)
    @ManyToOne
    private Member organizer;

    @Column(length = 100, nullable = false)
    private String title;

    @Column
    private String form_description;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormStatus form_status;
}
