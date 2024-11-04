package Capston.CosmeticTogether.domain.board.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CommentResponseDTO {
    private String commenter;
    private String commenterURL;
    private String commentAt;
}
