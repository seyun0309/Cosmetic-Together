package Capston.CosmeticTogether.global.enums;

public enum ProductStatus {
    INSTOCK("INSTOCK", "판매중"),
    OUTSTOCK("OUTSTOCK", "품절");

    private final String code ;
    private final String description;

    ProductStatus(String code,  String description) {
        this.code = code;
        this.description = description;
    }
}
