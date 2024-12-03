package Capston.CosmeticTogether.domain.form.dto.resonse.order;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class OrderDetailResponseDTO {
    private String orderDate;
    private String productPrice;
    private String shippingFee;
    private String totalPayment;
    private String recipientName;
    private String recipientPhone;
    private String recipientAddress;
    private List<OrderProductsResponseDTO> orderProducts;
    private String deliveryOption;
    private String deliveryCost;
}
