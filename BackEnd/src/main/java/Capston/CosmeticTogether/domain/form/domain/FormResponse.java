package Capston.CosmeticTogether.domain.form.domain;


import Capston.CosmeticTogether.domain.form.domain.Form;
import Capston.CosmeticTogether.domain.member.domain.Member;
import Capston.CosmeticTogether.global.common.BaseEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
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
@Table(name = "form_response")
public class FormResponse extends BaseEntity {

    @JoinColumn(nullable = false)
    @ManyToOne
    private Member buyer;

    @JoinColumn(nullable = false)
    @ManyToOne
    private Form form;

    @Column(nullable = false)
    @Min(value = 1)
    private int quantity;

    @Column
    private String delivery_msg;
}
