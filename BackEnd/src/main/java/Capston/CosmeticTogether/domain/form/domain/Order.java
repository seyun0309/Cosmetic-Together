package Capston.CosmeticTogether.domain.form.domain;


import Capston.CosmeticTogether.domain.member.domain.Member;
import Capston.CosmeticTogether.global.common.BaseEntity;
import Capston.CosmeticTogether.global.enums.OrderStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;

@SuperBuilder
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "orders")
public class Order extends BaseEntity {

    @JoinColumn(nullable = false)
    @ManyToOne
    private Member buyer;

    @OneToMany(mappedBy = "order")
    private List<OrderProduct> orderProducts;

    @Column
    private String recipientName;

    @Column
    private String recipientPhone;

    @Column
    private String recipientAddress;

    @Column(nullable = false)
    private int totalPrice;

    @ManyToOne
    @JoinColumn(name = "delivery_id", nullable = false)
    private Delivery delivery;

    @ManyToOne
    @JoinColumn(name = "form_id", nullable = false)
    private Form form;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    public void saveProducts(List<OrderProduct> orderProducts) {
        this.orderProducts = orderProducts;
    }

    public void updateOrderStatus(OrderStatus status) {
        this.orderStatus = status;
    }
}
