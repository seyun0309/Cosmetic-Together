package Capston.CosmeticTogether.global.auth.controller;

import Capston.CosmeticTogether.global.auth.dto.request.DuplicateDTO;
import Capston.CosmeticTogether.global.auth.dto.request.LoginRequestDTO;
import Capston.CosmeticTogether.global.auth.dto.request.SignUpRequestDTO;
import Capston.CosmeticTogether.global.auth.dto.response.SignUpResponseDTO;
import Capston.CosmeticTogether.global.auth.dto.token.GeneratedTokenDTO;
import Capston.CosmeticTogether.global.auth.dto.token.TokenModifyDTO;
import Capston.CosmeticTogether.global.auth.service.AuthService;
import Capston.CosmeticTogether.global.auth.service.JwtProvider;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;
    private final JwtProvider jwtProvider;

    @GetMapping("/kakao/callback")
    @Operation(summary = "카카오 로그인", description = "카카오 로그인")
    public ResponseEntity<GeneratedTokenDTO> kakaoCallback(@RequestParam(value = "code") String code) {
        GeneratedTokenDTO generatedTokenDTO = authService.kakaoLogin(code);
        return ResponseEntity.ok(generatedTokenDTO);
    }

    @PostMapping("/signup")
    @Operation(summary = "회원가입 로직", description = "사용자 이름, 이메일, 비밀번호를 입력하면 검증 후 회원가입을 진행합니다.")
    public ResponseEntity<SignUpResponseDTO> signUp(@RequestBody @Valid SignUpRequestDTO signUpRequestDTO) {
        SignUpResponseDTO responseDTO = authService.signUp(signUpRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }
    //이메일 유니크 속성 고려해봐야 함
//    @PostMapping("/duplicate/email")
//    @Operation(summary = "이메일 중복 검사 로직", description = "이메일 중복 검사를 진행합니다.")
//    public ResponseEntity<String> checkLoginIdDuplicate(@RequestBody @Valid DuplicateDTO.Email duplicateLoginIdDTO ) {
//        boolean isDuplicate = authService.checkLoginIdDuplicate(duplicateLoginIdDTO.getEmail());
//
//        if (isDuplicate) {
//            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 존재하는 이메일입니다");
//        } else {
//            return ResponseEntity.ok("사용 가능한 이메일입니다");
//        }
//    }

    @PostMapping("/duplicate/nickname")
    @Operation(summary = "닉네임 중복 검사 로직", description = "닉네임 중복 검사를 진행합니다.")
    public ResponseEntity<String> checkNickNameDuplicate(@RequestBody @Valid DuplicateDTO.NickName duplicateNickNameDTO ) {
        boolean isDuplicate = authService.checkNickNameDuplicate(duplicateNickNameDTO.getNickName());

        if (isDuplicate) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body("이미 존재하는 닉네임입니다");
        } else {
            return ResponseEntity.ok("사용 가능한 닉네임입니다");
        }
    }

    @PostMapping("/login")
    @Operation(summary = "로그인 로직", description = "이메일, 비밀번호를 입력하면 검증 후 로그인을 진행하고 성공하면 Access Token과 Refresh Token을 발급합니다.")
    public ResponseEntity<GeneratedTokenDTO> login(@RequestBody @Valid LoginRequestDTO loginRequestDTO) {
        GeneratedTokenDTO generatedTokenDTO = authService.login(loginRequestDTO);
        return ResponseEntity.ok(generatedTokenDTO);
    }

    @PatchMapping("/logout")
    @Operation(summary = "로그아웃 로직", description = "사용자의 Refresh Token을 무효화합니다.")
    public ResponseEntity<String> logout() {
        authService.logout();
        return ResponseEntity.ok("로그아웃이 정상적으로 되었습니다");
    }

    @PatchMapping("/tokens")
    @Operation(summary = "토큰 재발급", description = "Access Token과 남은 기간에 따라 Refresh Token을 재발급 합니다.")
    public GeneratedTokenDTO tokenModify(@Valid @RequestBody TokenModifyDTO tokenModifyRequest) {
        return jwtProvider.reissueToken(tokenModifyRequest.getRefreshToken());
    }
}
