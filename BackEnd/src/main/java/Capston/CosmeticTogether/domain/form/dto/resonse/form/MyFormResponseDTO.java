package Capston.CosmeticTogether.domain.form.dto.resonse.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class MyFormResponseDTO {
    private Long formId;
    private String thumbnail;
    private String title;
    private String salesPeriod;
}
