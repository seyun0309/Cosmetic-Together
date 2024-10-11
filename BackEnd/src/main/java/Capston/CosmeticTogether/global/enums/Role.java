package Capston.CosmeticTogether.global.enums;

import lombok.Getter;

@Getter
public enum Role {
    USER("USER"),
    GUEST("GUEST");

    private final String value ;

    Role(String value) {
        this.value = value;
    }

    public static Role fromValue(String value) {
        for (Role role : values()) {
            if (role.value.equalsIgnoreCase(value)) {
                return role;
            }
        }
        throw new IllegalArgumentException("지원하지 않는 권한입니다: " + value);
    }
}
