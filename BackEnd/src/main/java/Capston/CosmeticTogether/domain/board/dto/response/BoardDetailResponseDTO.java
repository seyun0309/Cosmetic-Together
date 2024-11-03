package Capston.CosmeticTogether.domain.board.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class BoardDetailResponseDTO {
    private String writerNickName;
    private String profileUrl;
    private String description;
    private List<String> boardUrl;
    private Long likeCount;
    private String postTime;
    private List<CommentResponseDTO> comments;
}
