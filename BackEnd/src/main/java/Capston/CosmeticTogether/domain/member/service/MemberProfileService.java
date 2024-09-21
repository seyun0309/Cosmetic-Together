package Capston.CosmeticTogether.domain.member.service;


import Capston.CosmeticTogether.domain.board.service.S3ImageService;
import Capston.CosmeticTogether.domain.member.domain.Member;
import Capston.CosmeticTogether.domain.member.dto.MemberProfileDTO;
import Capston.CosmeticTogether.domain.member.dto.PasswordCheckDTO;
import Capston.CosmeticTogether.global.auth.dto.security.SecurityMemberDTO;
import Capston.CosmeticTogether.global.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberProfileService {
    private final MemberService memberService;
    private final S3ImageService s3ImageService;
    private final PasswordEncoder passwordEncoder;
    public boolean checkPassword(PasswordCheckDTO passwordCheckDTO) {
        // 1. 로그인 사용자 가져오기
        Member loginMember = memberService.getMemberFromSecurityDTO((SecurityMemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // 2. 로그인 사용자와 passwordCheckDTO 비교해서 boolean 값 리턴
        return passwordEncoder.matches(passwordCheckDTO.getPassword(), loginMember.getPassword());
    }

    public MemberProfileDTO getMemberProfile() {
        // 1. 로그인 사용자 가져오기
        Member loginMember = memberService.getMemberFromSecurityDTO((SecurityMemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // 2. 사용자 정보 매핑해서 리턴
        return MemberProfileDTO.builder()
                .userName(loginMember.getUserName())
                .email(loginMember.getEmail())
                .phone(loginMember.getPhone())
                .address(loginMember.getAddress())
                .status_msg(loginMember.getStatus_msg())
                .profile_url(loginMember.getProfile_url())
                .background_url(loginMember.getBackground_url())
                .build();
    }

    public void updateMemberProfile(MultipartFile profileUrl, MultipartFile backgroundUrl, MemberProfileDTO memberProfileDTO) {
        // 1. 로그인 사용자 가져오기
        Member loginMember = memberService.getMemberFromSecurityDTO((SecurityMemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // 2. 정보 수정

        //TODO 코르 리팩토링 하기
        //2.1 이미지가 원래 없었다면 그냥 넣기
        if(loginMember.getProfile_url().isEmpty()) {
            s3ImageService.upload(profileUrl);
            loginMember.updateMemberInfo(memberProfileDTO, Role.USER);
        } else {
            // 2.2 이미지가 원래 있었다면 기존 이미지 삭제하고 진행
            s3ImageService.deleteImageFromS3(loginMember.getProfile_url());
            loginMember.updateMemberInfo(memberProfileDTO, Role.USER);
        }
        if(loginMember.getBackground_url().isEmpty()) {
            s3ImageService.upload(backgroundUrl);
            loginMember.updateMemberInfo(memberProfileDTO, Role.USER);
        } else {
            s3ImageService.deleteImageFromS3(loginMember.getBackground_url());
            loginMember.updateMemberInfo(memberProfileDTO, Role.USER);
        }
    }
}
