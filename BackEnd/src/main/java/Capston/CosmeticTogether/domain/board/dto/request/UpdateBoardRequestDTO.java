package Capston.CosmeticTogether.domain.board.dto.request;


import lombok.*;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateBoardRequestDTO {
    private String description;
    private List<String> deleteImageUrls;
}
