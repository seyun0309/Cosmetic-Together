package Capston.CosmeticTogether.domain.form.dto.resonse;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@AllArgsConstructor
@Builder
public class ProductResponseDTO {
    private String productName;
    private String price;
    private String product_url;
    private String maxPurchaseLimit;
    private String stock;
    private String productStatuses;
}
