package Capston.CosmeticTogether.domain.comment.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateCommentRequestDTO {

    @NotNull
    private Long boardId;

    @NotNull
    private String comment;
}
