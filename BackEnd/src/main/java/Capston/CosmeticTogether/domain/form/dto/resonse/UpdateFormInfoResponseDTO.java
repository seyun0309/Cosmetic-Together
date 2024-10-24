package Capston.CosmeticTogether.domain.form.dto.resonse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;


@Getter
@AllArgsConstructor
@Builder
public class UpdateFormInfoResponseDTO {
    private String title;
    private String formUrl;
    private String startDate;
    private String endDate;
}
