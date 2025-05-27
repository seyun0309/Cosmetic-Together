package Capston.CosmeticTogether.global.enums;

import lombok.Getter;

@Getter
public enum FormType {
    IMMEDIATE("즉시 입금"),
    AFTER_RECRUITMENT("모집 완료 후 입금");

    private final String description;

    FormType(String description) {
        this.description = description;
    }

    public static FormType formCode(String code) {
        for (FormType type : FormType.values()) {
            if (type.getDescription().equalsIgnoreCase(code)) {
                return type;
            }
        }
        throw new IllegalArgumentException("찾을 수 없는 FormType : " + code);
    }
}
