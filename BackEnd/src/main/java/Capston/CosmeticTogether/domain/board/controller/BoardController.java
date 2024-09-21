package Capston.CosmeticTogether.domain.board.controller;

import Capston.CosmeticTogether.domain.board.dto.request.CreateBoardRequestDTO;
import Capston.CosmeticTogether.domain.board.dto.response.BoardResponseDTO;
import Capston.CosmeticTogether.domain.board.service.BoardService;
import Capston.CosmeticTogether.domain.board.service.S3ImageService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/board")
public class BoardController {
    private final S3ImageService s3ImageService;
    private final BoardService boardService;

    // 게시글 등록
    @PostMapping()
    @Operation(summary = "게시글 작성", description = "image(게시글 사진), request(게시글 내용)으로 서버에 데이터를 보내면 게시글 등록됩니다 / 토큰 필요")
    public ResponseEntity<String> uploadBoard(@RequestPart(required = false, name = "image") MultipartFile image,
                                              @RequestPart(name = "request") @Valid CreateBoardRequestDTO createBoardRequestDTO) {
        String imgUrl = s3ImageService.upload(image);
        boardService.uploadBoard(imgUrl, createBoardRequestDTO);

        return ResponseEntity.ok("게시글이 등록되었습니다");
    }

    // 게시글 단일조회
    //TODO 댓글도 같이 리턴해야 함
    //TODO 찜 개수 리턴
    @GetMapping("/{boardId}")
    @Operation(summary = "게시글 단일 조회", description = "게시글 id를 서버에 보내주면 하나의 게시글을 터치했을 때 해당 게시글의 세부 내용(댓글 포함)을 리턴합니다")
    public ResponseEntity<BoardResponseDTO> getBoard(@PathVariable("boardId") Long boardId) {
        BoardResponseDTO response = boardService.getBoard(boardId);
        return ResponseEntity.ok(response);
    }

    // 최신 게시글 조회
    //TODO 찜 개수 리턴
    @GetMapping("/recent")
    @Operation(summary = "최신 게시글 조회", description = "최신 게시글들(List)을 리턴합니다")
    public ResponseEntity<List<BoardResponseDTO>> getRecentBoard() {
        List<BoardResponseDTO> response = boardService.getRecentBoard();
        return ResponseEntity.ok(response);
    }

    // 팔로잉 게시글 조회
    //TODO 찜 개수 리턴
    @GetMapping("/following")
    @Operation(summary = "팔로잉 게시글 조회", description = "사용자가 팔로잉한 사용자의 최신 게시글들(List)을 리턴합니다")
    public ResponseEntity<List<BoardResponseDTO>> getFollowingMemberBoard() {
        List<BoardResponseDTO> response = boardService.getFollowingMemberBoard();
        return ResponseEntity.ok(response);
    }

    // 게시글 수정
    @PatchMapping("/{boardId}")
    @Operation(summary = "본인 게시글 수정", description = "url에 boardId를 넣고 해당 api를 호출하고 image(게시글 사진), request(게시글 내용)을 서버에 보내주면 게시글 수정이 완료됩니다 / 토큰 필요")
    public ResponseEntity<String> updateBoard(@PathVariable("boardId") Long boardId,
                                              @RequestPart(required = false, name = "image") MultipartFile image,
                                              @RequestPart(name = "request") @Valid CreateBoardRequestDTO createBoardRequestDTO) {

        String imgUrl = s3ImageService.upload(image);
        boardService.updateBoard(boardId, imgUrl, createBoardRequestDTO);
        return ResponseEntity.ok("게시글이 수정되었습니다");
    }

    // 게시글 삭제
    @DeleteMapping("/{boardId}")
    @Operation(summary = "본인 게시글 삭제", description = "url에 boardId를 넣고 해당 api를 호출하면 게시글이 삭제됩니다  / 토큰 필요")
    public ResponseEntity<String> deleteBoard(@PathVariable("boardId") Long boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.ok("게시글이 삭제되었습니다");
    }
}
