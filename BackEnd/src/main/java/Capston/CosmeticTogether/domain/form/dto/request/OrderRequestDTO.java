package Capston.CosmeticTogether.domain.form.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderRequestDTO {

    @NotBlank
    private String recipientName;

    @NotBlank
    private String recipientPhone;

    @NotBlank
    private String recipientAddress;

    @NotEmpty
    private List<Long> productsId;

    @NotEmpty
    private List<Integer> orderQuantity;

    @NotEmpty
    private Long deliveryId;

    @NotBlank
    private int totalPrice;
}
