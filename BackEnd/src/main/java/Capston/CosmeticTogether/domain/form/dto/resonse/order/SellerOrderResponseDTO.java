package Capston.CosmeticTogether.domain.form.dto.resonse.order;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class SellerOrderResponseDTO {
    private String totalOrders;
    private String totalSales;
    List<SellerOrderListDTO> orders;
}
