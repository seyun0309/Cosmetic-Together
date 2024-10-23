package Capston.CosmeticTogether.domain.form.domain;

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
@Table(name = "delivery")
public class Delivery extends BaseEntity {

    @Column(nullable = false)
    private String deliveryOption;

    @Column(nullable = false)
    private int deliveryCost;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Form form;
}
