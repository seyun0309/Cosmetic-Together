package Capston.CosmeticTogether.domain.comment.controller;

import Capston.CosmeticTogether.ResponseMessage;
import Capston.CosmeticTogether.domain.board.dto.request.CreateBoardRequestDTO;
import Capston.CosmeticTogether.domain.comment.dto.request.CreateCommentRequestDTO;
import Capston.CosmeticTogether.domain.comment.service.CommentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "댓글 API", description = "댓글 등록, 삭제")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/comment")
public class CommentController {

    private final CommentService commentService;

    // 댓글 등록
    @PostMapping()
    @Operation(summary = "[API] 댓글 등록 - 토큰 필요", description = "게시글 번호와 댓글 내용을 서버에 전송하여 댓글을 등록합니다")
    public ResponseEntity<ResponseMessage> createComment(@RequestBody CreateCommentRequestDTO createCommentRequestDTO) {
        commentService.createComment(createCommentRequestDTO);
        return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), "댓글이 등록되었습니다"));
    }

//
//    // 댓글 수정시
//    @PostMapping("/{commentId}")
//    @Operation(summary = "[UI] 댓글 수정 - 토큰 필요")

    // 댓글 삭제
    @DeleteMapping("{commentId}")
    @Operation(summary = "[API] 댓글 삭제 - 토큰 필요", description = "URL 경로에 commentId를 포함시켜 댓글 삭제 진행")
    public ResponseEntity<ResponseMessage> deleteComment(@RequestParam("commentId") Long commentId) {
        commentService.deleteComment(commentId);
        return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), "댓글이 삭제되었습니다"));
    }
}
