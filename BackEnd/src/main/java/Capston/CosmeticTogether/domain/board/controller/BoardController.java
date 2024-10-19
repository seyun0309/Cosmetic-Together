package Capston.CosmeticTogether.domain.board.controller;

import Capston.CosmeticTogether.domain.board.dto.request.CreateBoardRequestDTO;
import Capston.CosmeticTogether.domain.board.dto.response.GetBoardResponseDTO;
import Capston.CosmeticTogether.domain.board.dto.response.UpdateBoardResponseDTO;
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
    @Operation(summary = "[API] 게시글 작성 - 토큰 필요", description = "이미지 파일(images)과 게시글 내용(request)을 서버에 전송하여 게시글을 등록합니다 / 이미지는 선택사항, 게시글 내용은 필수")
    public ResponseEntity<String> uploadBoard(@RequestPart(required = false, name = "images") List<MultipartFile> images,
                                              @RequestPart(name = "request") @Valid CreateBoardRequestDTO createBoardRequestDTO) {

        boardService.uploadBoard(images, createBoardRequestDTO);
        return ResponseEntity.ok("게시글이 등록되었습니다");
    }

    // 게시글 단일조회
    //TODO 댓글도 같이 리턴해야 함
    @GetMapping("/{boardId}")
    @Operation(summary = "[API] 게시글 단일 조회(댓글 포함)", description = "해당 게시글의 boardId를 URL 경로에 포함시켜 하나의 게시글 정보를 조회합니다. "
            + "결과로는 작성자 정보, 게시글의 내용(이미지 포함), 작성 일자 데이터를 포함한 JSON 형식의 응답이 반환됩니다.")
    public ResponseEntity<GetBoardResponseDTO> getBoard(@PathVariable("boardId") Long boardId) {
        GetBoardResponseDTO response = boardService.getBoard(boardId);
        return ResponseEntity.ok(response);
    }

    // 최신 게시글 조회
    @GetMapping("/recent")
    @Operation(summary = "[API] 최신 게시글 조회", description = "최신 게시글들(List)을 리턴합니다")
    public ResponseEntity<List<GetBoardResponseDTO>> getRecentBoard() {
        List<GetBoardResponseDTO> response = boardService.getRecentBoard();
        return ResponseEntity.ok(response);
    }

    // 팔로잉 게시글 조회
    @GetMapping("/following")
    @Operation(summary = "[API] 팔로잉 게시글 조회 - 토큰 필요", description = "사용자가 팔로잉한 사용자의 최신 게시글들(List)을 리턴합니다")
    public ResponseEntity<List<GetBoardResponseDTO>> getFollowingMemberBoard() {
        List<GetBoardResponseDTO> response = boardService.getFollowingMemberBoard();
        return ResponseEntity.ok(response);
    }

    // 게시글 수정 정보 띄우기
    @PostMapping("/info/{boardId}")
    @Operation(summary = "[UI] 게시글 수정 정보 화면에 띄우기 - 토큰 필요", description = "해당 게시글의 boardId를 URL 경로에 포함시켜 수정 단계에서 화면에 해당 게시글 정보를 화면에 띄우는 역할")
    public ResponseEntity<UpdateBoardResponseDTO> getBoardUpdateInfo(@PathVariable("boardId") Long boardId) {
        UpdateBoardResponseDTO response = boardService.getBoardUpdateInfo(boardId);
        return ResponseEntity.ok(response);
    }

    // 게시글 수정
    @PatchMapping("/{boardId}")
    @Operation(summary = "[API] 본인 게시글 수정 - 토큰 필요", description = "해당 게시글의 boardId를 URL 경로에 포함하고 이미지 파일(images)과 게시글 내용(request)을 서버에 전송하여 게시글 수정")
    public ResponseEntity<String> updateBoard(@PathVariable("boardId") Long boardId,
                                              @RequestPart(required = false, name = "images") List<MultipartFile> images,
                                              @RequestPart(required = false, name = "request") @Valid CreateBoardRequestDTO createBoardRequestDTO) {

        boardService.updateBoard(boardId, images, createBoardRequestDTO);
        return ResponseEntity.ok("게시글이 수정되었습니다");
    }

    // 게시글 삭제
    @DeleteMapping("/{boardId}")
    @Operation(summary = "[API] 본인 게시글 삭제 - 토큰 필요", description = "해당 게시글의 boardId를 URL 경로에 포함시켜 게시글 삭제")
    public ResponseEntity<String> deleteBoard(@PathVariable("boardId") Long boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.ok("게시글이 삭제되었습니다");
    }
}
