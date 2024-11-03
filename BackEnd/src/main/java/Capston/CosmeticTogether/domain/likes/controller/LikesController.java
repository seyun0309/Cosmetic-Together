package Capston.CosmeticTogether.domain.likes.controller;


import Capston.CosmeticTogether.domain.likes.service.LikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "좋아요 [게시글]", description = "좋아요, 좋아요 취소")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/like")
public class LikesController {
    private final LikeService likeService;

    @PostMapping("/{boardId}")
    @Operation(summary = "[API] 게시글 좋아요 및 좋아요 취소 - 토큰필요", description = "게시글을 사용자의 좋아요 목록에 추가 또는 삭제합니다")
    public ResponseEntity<String> likeOrUnlikeBoard(@PathVariable("boardId") Long boardId) {
        boolean isValid = likeService.likeOrUnlikeBoard(boardId);
        if(isValid) {
            return ResponseEntity.ok("게시글이 내 좋아요 목록에 추가되었습니다");
        } else {
            return ResponseEntity.ok("게시글이 내 좋아요 목록에 삭제되었습니다");
        }
    }
}
