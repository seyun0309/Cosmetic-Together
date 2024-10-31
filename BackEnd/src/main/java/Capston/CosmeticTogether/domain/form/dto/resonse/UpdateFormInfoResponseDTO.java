package Capston.CosmeticTogether.domain.form.dto.resonse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;


@Getter
@AllArgsConstructor
@Builder
public class UpdateFormInfoResponseDTO {
    private String thumbnail;
    private String organizerName;
    private String organizer_profileUrl;
    private String title;
    private String form_description;
    private String formUrl;
    private String startDate;
    private String endDate;
    private List<String> productName;
    private List<String> price;
    private List<String> product_url;
    private List<String> maxPurchaseLimit;
    private List<String> stock;
    private List<String> productStatuses;
    private List<String> deliveryOption;
    private List<String> deliveryCost;
}

