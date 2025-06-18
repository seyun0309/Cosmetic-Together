package Capston.CosmeticTogether.global.enums;

import lombok.Getter;

@Getter
public enum ProductStatus {
    INSTOCK("판매중"),
    OUTSTOCK("품절");

    private final String description;

    ProductStatus(String description) {
        this.description = description;
    }
}
