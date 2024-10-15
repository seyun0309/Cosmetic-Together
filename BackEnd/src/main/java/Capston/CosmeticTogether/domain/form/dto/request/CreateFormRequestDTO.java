package Capston.CosmeticTogether.domain.form.dto.request;


import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;


@Getter
@AllArgsConstructor
@Builder
public class CreateFormRequestDTO {
    @NotBlank
    private String title;

    @NotBlank
    private String form_description;

    @NotBlank
    private LocalDateTime startDate;

    @NotBlank
    private LocalDateTime endDate;

    @NotBlank
    private List<String> productName;

    @NotBlank
    private List<Integer> price;

    @NotBlank
    private List<Integer> stock;

    @NotBlank
    private List<Integer> maxPurchaseLimit;
}
