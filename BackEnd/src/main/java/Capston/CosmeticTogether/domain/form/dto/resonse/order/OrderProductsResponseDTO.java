package Capston.CosmeticTogether.domain.form.dto.resonse.order;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class OrderProductsResponseDTO {
    private Long productId;
    private String productName;
    private String price;
    private String product_url;
    private String quantity;
}
