package Capston.CosmeticTogether.global.enums;

import lombok.Getter;

@Getter
public enum Role {
    USER("USER", "유저"),
    GUEST("GUEST", "게스트");

    private final String code ;
    private final String description;

    Role(String code,  String description) {
        this.code = code;
        this.description = description;
    }
}
