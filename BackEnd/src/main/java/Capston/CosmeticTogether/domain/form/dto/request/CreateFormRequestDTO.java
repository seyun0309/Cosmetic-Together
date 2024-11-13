package Capston.CosmeticTogether.domain.form.dto.request;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateFormRequestDTO {
    @NotBlank
    private String title;

    @NotBlank
    private String form_description;

    @NotNull
    private String startDate;

    @NotNull
    private String endDate;

    @NotEmpty
    private List<String> productName;

    @NotEmpty
    private List<Integer> price;

    @NotEmpty
    private List<Integer> stock;

    @NotEmpty
    private List<Integer> maxPurchaseLimit;

    @NotEmpty
    private List<String> deliveryOption;

    @NotEmpty
    private List<String> deliveryCost;

    @NotEmpty
    private String deliveryInstructions;
}
