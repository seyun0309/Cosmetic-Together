package Capston.CosmeticTogether.domain.form.dto.resonse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class DetailFormResponseDTO {
    private String thumbnail;
    private String organizerName;
    private String phone;
    private String address;
    private String email;
    private String organizer_profileUrl;
    private String title;
    private String form_description;
    private String salesPeriod;
    private Long favoriteCount;
    private List<ProductResponseDTO> products;
    private List<DeliveryResponseDTO> deliveries;
}
