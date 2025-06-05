package Capston.CosmeticTogether.domain.follow.controller;

import Capston.CosmeticTogether.ResponseMessage;
import Capston.CosmeticTogether.domain.follow.dto.response.FollowResponseDTO;
import Capston.CosmeticTogether.domain.follow.dto.response.GetFollowAndFollowingMemberDTO;
import Capston.CosmeticTogether.domain.follow.service.FollowService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "팔로우 및 언팔로우", description = "팔로우, 언팔로우, 팔로워 리스트 조회, 팔로우 리스트 조회")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class FollowController {
    private final FollowService followService;

    @PostMapping("/follow/board/{boardId}")
    @Operation(summary = "[API] 상대방 팔로우 하기 - 토큰 필요", description = "게시글 ID를 URL 경로에 포함시켜 전달하면 작성자를 팔로우 또는 언팔로우 진행")
    public ResponseEntity<ResponseMessage> followOrUnFollowByBoardId(@PathVariable("boardId") Long boardId) {
        FollowResponseDTO response = followService.followOrUnFollowByBoardId(boardId);
        if(response.isValid()) {
            return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), response.getFollowingNickName() + "님을 팔로우하였습니다"));
        } else {
            return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), response.getFollowingNickName() + "님을 언팔로우하였습니다"));
        }
    }

    @PostMapping("/follow/form/{formId}")
    @Operation(summary = "[API] 상대방 팔로우 하기 - 토큰 필요", description = "폼 ID를 URL 경로에 포함시켜 전달하면 작성자를 팔로우 또는 언팔로우 진행")
    public ResponseEntity<ResponseMessage> followOrUnFollowByFormId(@PathVariable("formId") Long formId) {
        FollowResponseDTO response = followService.followOrUnFollowByFormId(formId);
        if(response.isValid()) {
            return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), response.getFollowingNickName() + "님을 팔로우하였습니다"));
        } else {
            return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), response.getFollowingNickName() + "님을 언팔로우하였습니다"));
        }
    }

    @PostMapping("/follow/mypage/{memberId}")
    @Operation(summary = "[API] 상대방 팔로우 하기 - 토큰 필요", description = "멤버 ID를 URL 경로에 포함시켜 전달하면 작성자를 팔로우 또는 언팔로우 진행")
    public ResponseEntity<ResponseMessage> followOrUnFollowByMemberId(@PathVariable("memberId") Long memberId) {
        FollowResponseDTO response = followService.followOrUnFollowByMemberId(memberId);
        if(response.isValid()) {
            return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), response.getFollowingNickName() + "님을 팔로우하였습니다"));
        } else {
            return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), response.getFollowingNickName() + "님을 언팔로우하였습니다"));
        }
    }
}
