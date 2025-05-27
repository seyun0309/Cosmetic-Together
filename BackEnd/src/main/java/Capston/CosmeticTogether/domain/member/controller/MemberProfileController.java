package Capston.CosmeticTogether.domain.member.controller;


import Capston.CosmeticTogether.ResponseMessage;
import Capston.CosmeticTogether.domain.board.dto.response.BoardSummaryResponseDTO;
import Capston.CosmeticTogether.domain.form.dto.resonse.form.FormResponseDTO;
import Capston.CosmeticTogether.domain.member.dto.request.AddressUpdateRequestDTO;
import Capston.CosmeticTogether.domain.member.dto.request.MemberUpdateRequestDTO;
import Capston.CosmeticTogether.domain.member.dto.response.MemberProfileResponseDTO;
import Capston.CosmeticTogether.domain.member.dto.PasswordCheckDTO;
import Capston.CosmeticTogether.domain.member.dto.response.MyPageOverviewResponseDTO;
import Capston.CosmeticTogether.domain.member.service.MemberProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "마이페이지", description = "비밀번호 체크, 사용자 정보 수정, 좋아요 게시글 조회, 찜 폼 조회, 본인 작성 게시글 조회, 본인 작성 폼 조회")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/mypage")
public class MemberProfileController {
    private final MemberProfileService memberProfileService;

    @GetMapping()
    @Operation(summary = "마이페이지 첫 화면에 필요한 정보 - 토큰 필요", description = "마이페이지 첫 화면에 넣을 사용자 프로필 사진, 닉네임 리턴")
    public ResponseEntity<MyPageOverviewResponseDTO> getMyPageOverview() {
        MyPageOverviewResponseDTO response = memberProfileService.getMyPageOverView();
        return ResponseEntity.ok(response);
    }

    // 비밀번호 체크하는 거
    @PostMapping("/check")
    @Operation(summary = "정보 수정 전 비밀번호 체크 - 토큰필요", description = "마이페이지에서 자신의 정보를 수정하기 전에 먼저 비밀번호를 체크합니다")
    public ResponseEntity<ResponseMessage> checkPassword(@RequestBody PasswordCheckDTO passwordCheckDTO) {
        boolean isChecked = memberProfileService.checkPassword(passwordCheckDTO);

        if(isChecked) {
            return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), "비밀번호 인증이 완료되었습니다"));
        } else {
            return ResponseEntity.ok(new ResponseMessage(HttpStatus.UNAUTHORIZED.value(), "비밀번호가 불일치합니다 다시 작성해주세요"));
        }
    }

    // 사용자 정보 화면에 띄우는 거
    @GetMapping("/info")
    @Operation(summary = "[UI] 사용자 정보 리턴 - 토큰필요", description = "사용자의 정보를 수정하는 화면에서 기존의 있던 사용자 정보를 화면단에 띄우는 데에 사용합니다")
    public ResponseEntity<MemberProfileResponseDTO> getMemberProfile() {
        MemberProfileResponseDTO response = memberProfileService.getMemberProfile();
        return ResponseEntity.ok(response);
    }

    // 사용자 정보 수정하는 거
    @GetMapping("/users")
    @Operation(summary = "사용자 정보 수정 - 토큰필요", description = "image1(프로필 사진), image2(배경사진), request(사용자 정보)로 서버에 데이터를 보내면 사용자 정보 수정이 완료됩니다")
    public ResponseEntity<ResponseMessage> updateMemberProfile(@RequestPart(required = false, name = "image1") MultipartFile profileUrl,
                                                      @RequestPart(required = false, name = "image2") MultipartFile backgroundUrl,
                                                      @RequestPart(name = "request") @Valid MemberUpdateRequestDTO memberUpdateRequestDTO) {
        memberProfileService.updateMemberProfile(profileUrl, backgroundUrl, memberUpdateRequestDTO);
        return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), "회원 정보가 수정되었습니다"));
    }

    // 주소 변경
    @PostMapping("/address")
    @Operation(summary = "주소 수정 - 토큰필요", description = "주소를 수정합니다")
    public ResponseEntity<ResponseMessage> updateUserAddress(@RequestBody AddressUpdateRequestDTO addressUpdateRequestDTO) {
        memberProfileService.updateUserAddress(addressUpdateRequestDTO.getAddress());
        return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), "주소가 수정되었습니다"));
    }

    //좋아요 게시글 조회
    @GetMapping("/liked-board")
    @Operation(summary = "사용자가 좋아요 한 게시글 조회 - 토큰필요", description = "사용자가 좋아요를 한 게시글들을 조회합니다")
    public ResponseEntity<List<BoardSummaryResponseDTO>> getLikedBoard() {
        List<BoardSummaryResponseDTO> response = memberProfileService.getLikedBoard();
        return ResponseEntity.ok(response);
    }

    //찜 폼 조회
    @GetMapping("/favorite-form")
    @Operation(summary = "사용자가 찜한 폼 조회 - 토큰필요", description = "사용자가 찜을 한 폼들을 조회합니다")
    public ResponseEntity<List<FormResponseDTO>> getFavoriteForm() {
        List<FormResponseDTO> response = memberProfileService.getFavoriteForm();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/posts")
    @Operation(summary = "내가 작성한 게시글 조회 - 토큰필요", description = "토큰을 통해 해당 사용자가 작성한 게시글을 조회합니다")
    public ResponseEntity<List<BoardSummaryResponseDTO>> getMyBoard() {
        List<BoardSummaryResponseDTO> response = memberProfileService.getMyBoard();
        return ResponseEntity.ok(response);
    }
}
