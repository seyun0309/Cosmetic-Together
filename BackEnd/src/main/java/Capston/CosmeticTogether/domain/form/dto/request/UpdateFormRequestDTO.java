package Capston.CosmeticTogether.domain.form.dto.request;


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
public class UpdateFormRequestDTO {

    private String title;
    private String form_description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
}
