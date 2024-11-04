package Capston.CosmeticTogether.global.auth.controller;

import Capston.CosmeticTogether.ResponseMessage;
import Capston.CosmeticTogether.global.auth.dto.EmailAuthResponseDTO;
import Capston.CosmeticTogether.global.auth.dto.MailAuthenticationDTO;
import Capston.CosmeticTogether.global.auth.dto.request.DuplicateDTO;
import Capston.CosmeticTogether.global.auth.dto.request.LoginRequestDTO;
import Capston.CosmeticTogether.global.auth.dto.request.SignUpRequestDTO;
import Capston.CosmeticTogether.global.auth.dto.response.SignUpResponseDTO;
import Capston.CosmeticTogether.global.auth.dto.token.GeneratedTokenDTO;
import Capston.CosmeticTogether.global.auth.dto.token.TokenModifyDTO;
import Capston.CosmeticTogether.global.auth.service.AuthService;
import Capston.CosmeticTogether.global.auth.service.JwtProvider;
import Capston.CosmeticTogether.global.auth.service.MailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;


@Tag(name = "인증/인가", description = "회원가입, 닉네임 중복 검사, 로그인, 이메일 중복 검사 및 인증코드 발송, 이메일 인증코드 검증, 로그아웃")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1")
public class AuthController {
    private final AuthService authService;
    private final JwtProvider jwtProvider;
    private final MailService mailService;


    @GetMapping("/kakao/callback")
    @Operation(summary = "카카오 로그인", description = "카카오 로그인")
    public ResponseEntity<GeneratedTokenDTO> kakaoCallback(@RequestParam(value = "code") String code) {
        GeneratedTokenDTO generatedTokenDTO = authService.kakaoLogin(code);
        return ResponseEntity.ok(generatedTokenDTO);
    }

    @PostMapping("/email/code")
    @Operation(summary = "이메일 중복 검사 및 인증번호 전송", description = "사용자가 입력한 이메일을 중복 검사 한 후 중복이 아니라면 인증코드 발송합니다")
    public ResponseEntity<ResponseMessage> sendAuthCode(@RequestBody DuplicateDTO.Email mailDTO) {
        boolean isDuplicate = authService.checkEmailDuplicate(mailDTO);

        if (isDuplicate) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseMessage(HttpStatus.CONFLICT.value(), "이미 존재하는 이메일입니다"));
        } else {
            mailService.sendEmail(mailDTO);
            return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), "인증코드가 발송되었습니다"));
        }
    }

    @PostMapping("/emailCheck")
    @Operation(summary = "이메일 인증번호 검증", description = "사용자가 입력한 인증번호가 올바른 인증번호인지 검사합니다")
    public EmailAuthResponseDTO checkAuthCode(@RequestBody MailAuthenticationDTO mailAuthenticationDTO) {
        return mailService.validateAuthCode(mailAuthenticationDTO.getEmail(), mailAuthenticationDTO.getAuthCode());
    }

    @PostMapping("/signup")
    @Operation(summary = "회원가입 로직", description = "사용자 이름, 이메일, 비밀번호를 입력하면 검증 후 회원가입을 진행합니다.")
    public ResponseEntity<SignUpResponseDTO> signUp(@RequestBody @Valid SignUpRequestDTO signUpRequestDTO) {
        SignUpResponseDTO responseDTO = authService.signUp(signUpRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @PostMapping("/nickname")
    @Operation(summary = "닉네임 중복 검사 로직", description = "닉네임 중복 검사를 진행합니다.")
    public ResponseEntity<ResponseMessage> checkNickNameDuplicate(@RequestBody @Valid DuplicateDTO.NickName duplicateNickNameDTO ) {
        boolean isDuplicate = authService.checkNickNameDuplicate(duplicateNickNameDTO.getNickName());

        if (isDuplicate) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new ResponseMessage(HttpStatus.CONFLICT.value(), "이미 존재하는 닉네임입니다"));
        } else {
            return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), "사용 가능한 닉네임입니다"));
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
    public ResponseEntity<ResponseMessage> logout() {
        authService.logout();
        return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), "로그아웃이 정상적으로 되었습니다"));
    }

    @PatchMapping("/tokens")
    @Operation(summary = "토큰 재발급", description = "Access Token과 남은 기간에 따라 Refresh Token을 재발급 합니다.")
    public GeneratedTokenDTO tokenModify(@Valid @RequestBody TokenModifyDTO tokenModifyRequest) {
        return jwtProvider.reissueToken(tokenModifyRequest.getRefreshToken());
    }
}
