package Capston.CosmeticTogether.domain.board.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class BoardResponseDTO {
    private String writerNickName;
    private String description;
    private String imgUrl;
}
