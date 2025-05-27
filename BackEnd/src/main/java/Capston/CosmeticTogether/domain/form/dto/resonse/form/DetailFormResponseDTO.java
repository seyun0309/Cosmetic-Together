package Capston.CosmeticTogether.domain.form.dto.resonse.form;

import Capston.CosmeticTogether.domain.form.dto.resonse.DeliveryResponseDTO;
import Capston.CosmeticTogether.domain.form.dto.resonse.ProductResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class DetailFormResponseDTO {
    private Long organizerId;
    private String thumbnail;
    private String organizerName;
    private String instagram;
    private String phone;
    private String address;
    private String email;
    private String organizer_profileUrl;
    private String title;
    private String form_description;
    private String salesPeriod;
    private Long favoriteCount;
    private String buyerName;
    private String buyerPhone;
    private String buyerEmail;
    private String formType;
    private List<ProductResponseDTO> products;
    private List<DeliveryResponseDTO> deliveries;
}
