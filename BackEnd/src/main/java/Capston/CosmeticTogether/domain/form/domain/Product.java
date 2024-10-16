package Capston.CosmeticTogether.domain.form.domain;


import Capston.CosmeticTogether.global.common.BaseEntity;
import Capston.CosmeticTogether.global.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "product")
public class Product extends BaseEntity {

    @Column(nullable = false)
    private String productName;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private int stock;

    @Column
    private String product_url;

    @Column(nullable = false)
    private int maxPurchaseLimit;

    @Setter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    @ManyToOne
    @JoinColumn(nullable = false)
    private Form form;

    public void update(String productName, Integer price, Integer stock, Integer maxPurchaseLimit, String productUrl) {
        if (productName != null) {
            this.productName = productName;
        }
        if (price != null) {
            this.price = price;
        }
        if (stock != null) {
            this.stock = stock;
        }
        if (maxPurchaseLimit != null) {
            this.maxPurchaseLimit = maxPurchaseLimit;
        }
        if (productUrl != null) {
            this.product_url = productUrl;
        }
    }

}
