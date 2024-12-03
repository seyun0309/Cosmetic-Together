package Capston.CosmeticTogether.domain.form.dto.resonse.order;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class OrderResponseDTO {
    private Long formId;
    private Long orderId;
    private String orderDate;
    private String thumbnail;
    private String title;
    private String totalPrice;
}
