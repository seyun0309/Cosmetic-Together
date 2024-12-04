package Capston.CosmeticTogether.domain.form.dto.request;


import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateOrderStatusRequestDTO {
    @NotNull
    private String orderStatus;
}
