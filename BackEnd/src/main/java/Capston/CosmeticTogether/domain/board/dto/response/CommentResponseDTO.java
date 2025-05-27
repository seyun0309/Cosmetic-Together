package Capston.CosmeticTogether.domain.board.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class CommentResponseDTO {
    private String profileUrl;
    private String commenter;
    private String commentAt;
    private String comment;
}
