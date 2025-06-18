package Capston.CosmeticTogether.domain.board.controller;

import Capston.CosmeticTogether.ResponseMessage;
import Capston.CosmeticTogether.domain.board.dto.request.CreateBoardRequestDTO;
import Capston.CosmeticTogether.domain.board.dto.request.UpdateBoardRequestDTO;
import Capston.CosmeticTogether.domain.board.dto.response.BoardDetailResponseDTO;
import Capston.CosmeticTogether.domain.board.dto.response.BoardSummaryResponseDTO;
import Capston.CosmeticTogether.domain.board.dto.response.UpdateBoardResponseDTO;
import Capston.CosmeticTogether.domain.board.service.BoardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "게시글", description = "등록, 단일 조회, 최신 조회, 팔로잉 조회, 키워드 조회, 수정, 삭제")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/board")
public class BoardController {
    private final BoardService boardService;

    // 게시글 등록
    @PostMapping(name = "/image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Operation(summary = "[API] 게시글 작성 - 토큰 필요", description = "이미지 파일(images)과 게시글 내용(request)을 서버에 전송하여 게시글을 등록합니다 / 이미지는 선택사항, 게시글 내용은 필수")
    public ResponseEntity<ResponseMessage> uploadBoard(@RequestPart(required = false, name = "images") List<MultipartFile> images,
                                              @RequestPart(name = "request") @Valid CreateBoardRequestDTO createBoardRequestDTO) {

        boardService.uploadBoard(images, createBoardRequestDTO);
        return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), "게시글이 등록되었습니다"));
    }

    // 게시글 단일조회
    @GetMapping("/{boardId}")
    @Operation(summary = "[API] 게시글 단일 조회(댓글 포함)", description = "해당 게시글의 boardId를 URL 경로에 포함시켜 하나의 게시글 정보를 조회합니다. "
            + "결과로는 작성자 정보, 게시글의 내용(이미지 포함), 작성 일자 데이터를 포함한 JSON 형식의 응답이 반환됩니다.")
    public ResponseEntity<BoardDetailResponseDTO> getBoard(@PathVariable("boardId") Long boardId) {
        BoardDetailResponseDTO response = boardService.getBoard(boardId);
        return ResponseEntity.ok(response);
    }

    // 최신 게시글 조회
    @GetMapping("/recent")
    @Operation(summary = "[API] 최신 게시글 조회", description = "최신 게시글들(List)을 리턴합니다")
    public ResponseEntity<List<BoardSummaryResponseDTO>> getRecentBoard() {
        List<BoardSummaryResponseDTO> response = boardService.getRecentBoard();
        return ResponseEntity.ok(response);
    }

    // 팔로잉 게시글 조회
    @GetMapping("/following")
    @Operation(summary = "[API] 팔로잉 게시글 조회 - 토큰 필요", description = "사용자가 팔로잉한 사용자의 최신 게시글들(List)을 리턴합니다")
    public ResponseEntity<List<BoardSummaryResponseDTO>> getFollowingMemberBoard() {
        List<BoardSummaryResponseDTO> response = boardService.getFollowingMemberBoard();
        return ResponseEntity.ok(response);
    }

    // 키워드 조회
    @GetMapping()
    @Operation(summary = "[API] 키워드 통한 게시글 검색", description = "사용자가 키워드를 검색창에 넣으면 키워드가 포함된 게시글을 불러옵니다")
    public ResponseEntity<List<BoardSummaryResponseDTO>> searchBoardByKeyword(@RequestParam(value = "keyword") String keyword) {
        List<BoardSummaryResponseDTO> response = boardService.searchBoardByKeyword(keyword);
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
    @PostMapping("/{boardId}")
    @Operation(summary = "[API] 본인 게시글 수정 - 토큰 필요", description = "해당 게시글의 boardId를 URL 경로에 포함하고 이미지 파일(images)과 게시글 내용(request)을 서버에 전송하여 게시글 수정")
    public ResponseEntity<ResponseMessage> updateBoard(@PathVariable("boardId") Long boardId,
                                              @RequestPart(required = false, name = "images") List<MultipartFile> images,
                                              @RequestPart(required = false, name = "request") @Valid UpdateBoardRequestDTO updateBoardRequestDTO) {

        boardService.updateBoard(boardId, images, updateBoardRequestDTO);

        return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), "게시글이 수정되었습니다"));
    }

    // 게시글 삭제
    @DeleteMapping("/{boardId}")
    @Operation(summary = "[API] 본인 게시글 삭제 - 토큰 필요", description = "해당 게시글의 boardId를 URL 경로에 포함시켜 게시글 삭제")
    public ResponseEntity<ResponseMessage> deleteBoard(@PathVariable("boardId") Long boardId) {
        boardService.deleteBoard(boardId);
        return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), "게시글이 삭제되었습니다"));
    }
}
