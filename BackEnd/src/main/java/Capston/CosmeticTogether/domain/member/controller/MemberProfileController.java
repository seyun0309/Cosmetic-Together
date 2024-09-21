package Capston.CosmeticTogether.domain.member.controller;


import Capston.CosmeticTogether.domain.member.dto.MemberProfileDTO;
import Capston.CosmeticTogether.domain.member.dto.PasswordCheckDTO;
import Capston.CosmeticTogether.domain.member.service.MemberProfileService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/mypage")
public class MemberProfileController {
    private final MemberProfileService memberProfileService;

    // 비밀번호 체크하는 거
    @PostMapping("/check/password")
    @Operation(summary = "정보 수정 전 비밀번호 체크", description = "마이페이지에서 자신의 정보를 수정하기 전에 먼저 비밀번호를 체크합니다  / 토큰 필요")
    public ResponseEntity<String> checkPassword(@RequestBody PasswordCheckDTO passwordCheckDTO) {
        boolean isChecked = memberProfileService.checkPassword(passwordCheckDTO);

        if(isChecked) {
            return ResponseEntity.ok("비밀번호 인증이 완료되었습니다");
        } else {
            return ResponseEntity.ok("비밀번호가 불일치합니다 다시 작성해주세요");
        }
    }

    // 사용자 정보 화면에 띄우는 거
    @GetMapping("/info")
    @Operation(summary = "사용자 정보 리턴", description = "사용자의 정보를 수정하는 화면에서 기존의 있던 사용자 정보를 화면단에 띄우는 데에 사용  / 토큰 필요")
    public ResponseEntity<MemberProfileDTO> getMemberProfile() {
        MemberProfileDTO response = memberProfileService.getMemberProfile();
        return ResponseEntity.ok(response);
    }

    // 사용자 정보 수정하는 거
    @GetMapping("/update")
    @Operation(summary = "사용자 정보 수정", description = "image1(프로필 사진), image2(배경사진), request(사용자 정보)로 서버에 데이터를 보내면 사용자 정보 수정이 완료됩니다  / 토큰 필요")
    public ResponseEntity<String> updateMemberProfile(@RequestPart(required = false, name = "image1") MultipartFile profileUrl,
                                                      @RequestPart(required = false, name = "image2") MultipartFile backgroundUrl,
                                                      @RequestPart(name = "request") @Valid MemberProfileDTO memberProfileDTO) {
        memberProfileService.updateMemberProfile(profileUrl, backgroundUrl, memberProfileDTO);
        return ResponseEntity.ok("회원 정보가 수정되었습니다");
    }

    //TODO 계정 삭제
}
