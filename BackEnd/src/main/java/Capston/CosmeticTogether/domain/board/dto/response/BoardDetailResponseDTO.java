package Capston.CosmeticTogether.domain.board.dto.response;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class BoardDetailResponseDTO {
    private Long boardId;
    private String writerNickName;
    private String profileUrl;
    private String description;
    private List<String> boardUrl;
    private boolean liked;
    private boolean following;
    private String postTime;
    private List<CommentResponseDTO> comments;
}
