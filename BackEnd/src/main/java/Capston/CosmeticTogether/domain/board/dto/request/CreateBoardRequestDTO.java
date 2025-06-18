package Capston.CosmeticTogether.domain.board.dto.request;


import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateBoardRequestDTO {
    private String description;
}
