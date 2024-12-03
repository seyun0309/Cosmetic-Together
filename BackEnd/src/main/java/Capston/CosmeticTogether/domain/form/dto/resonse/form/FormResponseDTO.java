package Capston.CosmeticTogether.domain.form.dto.resonse.form;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class FormResponseDTO {
    private Long formId;
    private String thumbnail;
    private String title;
    private String organizerName;
    private String organizer_url;
    private String formStatus;
}
