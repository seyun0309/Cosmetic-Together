package Capston.CosmeticTogether.global.enums;

import lombok.Getter;

@Getter
public enum OrderStatus {
    COMPLETED("주문 완료"),
    PAYMENT_CONFIRMED("결제 완료"),
    SHIPPING("배송 중"),
    DELIVERED("배송 완료");

    private final String description;

    OrderStatus(String description) {
        this.description = description;
    }
}
