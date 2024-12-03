package Capston.CosmeticTogether.domain.form.dto.resonse.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class SellerOrderListDTO {
    private Long formId;
    private Long orderId;
    private String orderDate;
    private String buyerName;
    private String totalPrice;
}
