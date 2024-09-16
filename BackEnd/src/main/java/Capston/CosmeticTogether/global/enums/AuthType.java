package Capston.CosmeticTogether.global.enums;

import lombok.Getter;

@Getter
public enum AuthType {
    REGULAR("REGULAR", "일반"),
    KAKAO("KAKAO", "카카오");

    private final String code ;
    private final String description;

    AuthType(String code,  String description) {
        this.code = code;
        this.description = description;
    }
}
