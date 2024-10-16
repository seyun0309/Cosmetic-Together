package Capston.CosmeticTogether.global.enums;

import lombok.Getter;
import lombok.Setter;

@Getter
public enum FormStatus {
    ACTIVE("S001", "판매중"),
    CLOSED("S002", "마감");

    private final String code ;
    private final String description;

    FormStatus(String code, String description) {
        this.code = code;
        this.description = description;
    }
}
