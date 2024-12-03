package Capston.CosmeticTogether.domain.form.dto.resonse.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class CreateFormResponseDTO {
    private Long formId;
    private List<Long> productId;
}
