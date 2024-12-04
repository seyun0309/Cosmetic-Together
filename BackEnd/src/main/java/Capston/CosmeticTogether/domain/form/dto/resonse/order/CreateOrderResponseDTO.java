package Capston.CosmeticTogether.domain.form.dto.resonse.order;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CreateOrderResponseDTO {
    private Long orderId;
}
