package Capston.CosmeticTogether.domain.follow.service;


import Capston.CosmeticTogether.domain.follow.domain.Follow;
import Capston.CosmeticTogether.domain.follow.dto.response.GetFollowAndFollowingMemberDTO;
import Capston.CosmeticTogether.domain.follow.repository.FollowRepository;
import Capston.CosmeticTogether.domain.member.domain.Member;
import Capston.CosmeticTogether.domain.member.repository.MemberRepository;
import Capston.CosmeticTogether.domain.member.service.MemberService;
import Capston.CosmeticTogether.global.auth.dto.security.SecurityMemberDTO;
import Capston.CosmeticTogether.global.enums.ErrorCode;
import Capston.CosmeticTogether.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowService {
    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;

    @Transactional
    public String followMember(Long followingId) {
        Member loginMember = memberService.getMemberFromSecurityDTO((SecurityMemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // 자기자신을 팔로잉 하는지 검사
        if(followingId.equals(loginMember.getId())) {
            throw new BusinessException("자기 자신을 팔로우하는건 불가능합니다", ErrorCode.SELF_FOLLOW);
        }

        // DB에 있는 id인지 검사
        Member followingMember = memberRepository.findById(followingId).orElseThrow(() -> new BusinessException("해당 사용자가 존재하지 않습니다", ErrorCode.MEMBER_NOT_FOUND));

        // 기존에 팔로잉 된 거였는지 검사
        Optional<Follow> checkFollow = followRepository.findByFollowerIdAndFollowingId(followingId, loginMember.getId());
        if(checkFollow.isPresent()) {
            throw new BusinessException("이미 팔로우하였습니다", ErrorCode.ALREADY_FOLLOW);
        }

        // 정상적으로 팔로잉 진행
        Follow follow = new Follow(followingMember, loginMember);
        followRepository.save(follow);

        return followingMember.getNickname();
    }

    @Transactional
    public String unfollowMember(Long unfollowingId) {
        // 1. 로그인 한 사용자 가져오기 / 언팔로우 대상자 가져오기
        Member loginMember = memberService.getMemberFromSecurityDTO((SecurityMemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal());
        Member followingMember = memberRepository.findById(unfollowingId).orElseThrow(() -> new BusinessException("존재하는 사용자가 아닙니다", ErrorCode.MEMBER_NOT_FOUND));

        // 2. DB에 있는 id인지 검사
        Optional<Follow> checkFollow = followRepository.findByFollowerIdAndFollowingId(unfollowingId, loginMember.getId());
        if(checkFollow.isPresent()) {
            Follow follow = checkFollow.get();

            // 3. 언팔로잉 진행
            followRepository.delete(follow);
        } else {
            throw new BusinessException("팔로우 한 사용자가 아닙니다", ErrorCode.NOT_FOLLOWING);
        }
        return followingMember.getNickname();
    }

    public List<GetFollowAndFollowingMemberDTO> getFollowers() {
        // 1. 로그인 한 사용자 가져오기
        Member loginMember = memberService.getMemberFromSecurityDTO((SecurityMemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // 2. 팔로워 리스트 가져오기
        List<GetFollowAndFollowingMemberDTO> followerMemberList = new ArrayList<>();

        for(Follow follow : loginMember.getFollowerList()) {
            GetFollowAndFollowingMemberDTO getFollowAndFollowingMemberDTO = GetFollowAndFollowingMemberDTO.builder()
                    .nickname(follow.getFollower().getNickname())
                    .profileUrl(follow.getFollower().getProfile_url())
                    .build();
            followerMemberList.add(getFollowAndFollowingMemberDTO);
        }

        return followerMemberList;
    }

    public List<GetFollowAndFollowingMemberDTO> getFollowings() {
        // 1. 로그인 한 사용자 가져오기
        Member loginMember = memberService.getMemberFromSecurityDTO((SecurityMemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // 2. 팔로워 리스트 가져오기
        List<GetFollowAndFollowingMemberDTO> followingMemberList = new ArrayList<>();

        for(Follow follow : loginMember.getFollowerList()) {
            GetFollowAndFollowingMemberDTO getFollowAndFollowingMemberDTO = GetFollowAndFollowingMemberDTO.builder()
                    .nickname(follow.getFollower().getNickname())
                    .profileUrl(follow.getFollower().getProfile_url())
                    .build();
            followingMemberList.add(getFollowAndFollowingMemberDTO);
        }

        return followingMemberList;
    }
}
