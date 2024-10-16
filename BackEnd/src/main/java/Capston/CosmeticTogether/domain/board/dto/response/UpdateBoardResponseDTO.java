package Capston.CosmeticTogether.domain.board.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class UpdateBoardResponseDTO {
    private String description;
    private List<String> boardUrl;
}
