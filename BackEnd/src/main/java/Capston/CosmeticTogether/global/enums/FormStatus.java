package Capston.CosmeticTogether.global.enums;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum FormStatus {
    PENDING("판매예정"),
    ACTIVE("판매중"),
    CLOSED("마감");

    private final String description;

    FormStatus(String description) {
        this.description = description;
    }
}
