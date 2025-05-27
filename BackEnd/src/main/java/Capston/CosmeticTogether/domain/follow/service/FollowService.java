package Capston.CosmeticTogether.domain.follow.service;


import Capston.CosmeticTogether.domain.board.domain.Board;
import Capston.CosmeticTogether.domain.board.repository.BoardRepository;
import Capston.CosmeticTogether.domain.follow.domain.Follow;
import Capston.CosmeticTogether.domain.follow.dto.response.FollowResponseDTO;
import Capston.CosmeticTogether.domain.follow.dto.response.GetFollowAndFollowingMemberDTO;
import Capston.CosmeticTogether.domain.follow.repository.FollowRepository;
import Capston.CosmeticTogether.domain.member.domain.Member;
import Capston.CosmeticTogether.domain.member.repository.MemberRepository;
import Capston.CosmeticTogether.domain.member.service.MemberService;
import Capston.CosmeticTogether.global.auth.dto.security.SecurityMemberDTO;
import Capston.CosmeticTogether.global.auth.service.AuthUtil;
import Capston.CosmeticTogether.global.enums.ErrorCode;
import Capston.CosmeticTogether.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowService {
    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;
    private final MemberService memberService;
    private final AuthUtil authUtil;
    private final BoardRepository boardRepository;

    @Transactional
    public FollowResponseDTO likeOrUnlikeBoard(Long boardId) {

        Member loginMember = authUtil.extractMemberAfterTokenValidation();

        Board board = boardRepository.findById(boardId).orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

        Long followingId = board.getMember().getId();

        // 자신을 팔로우하려는 경우 예외 처리
        if (loginMember.getId().equals(followingId)) {
            throw new BusinessException("자기 자신을 팔로우할 수 없습니다", ErrorCode.SELF_FOLLOW);
        }

        // DB에 있는 id인지 검사
        Member followingMember = memberRepository.findById(followingId).orElseThrow(() -> new BusinessException("해당 사용자가 존재하지 않습니다", ErrorCode.MEMBER_NOT_FOUND));

        // 팔로우 여부 확인
        Follow checkFollow = followRepository.findByFollowerIdAndFollowingId(loginMember.getId(), followingId);

        if (checkFollow != null) {
            if(checkFollow.isValid()) {
                checkFollow.setValid(false);
                return new FollowResponseDTO(false, followingMember.getNickname());
            } else {
                checkFollow.setValid(true);
                return new FollowResponseDTO(false, followingMember.getNickname());
            }
        } else {
            // 팔로우 처리
            Follow follow = Follow.builder()
                    .follower(loginMember)
                    .following(followingMember)
                    .isValid(true)
                    .build();
            followRepository.save(follow);
            return new FollowResponseDTO(true, followingMember.getNickname());
        }
    }

    public List<GetFollowAndFollowingMemberDTO> getFollowers() {
        // 1. 로그인 한 사용자 가져오기
        Member loginMember = memberService.getMemberFromSecurityDTO((SecurityMemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // 2. 팔로워 리스트 가져오기
        List<GetFollowAndFollowingMemberDTO> followerMemberList = new ArrayList<>();

        for(Follow follow : loginMember.getFollowerList()) {
            GetFollowAndFollowingMemberDTO getFollowAndFollowingMemberDTO = GetFollowAndFollowingMemberDTO.builder()
                    .nickname(follow.getFollowing().getNickname())
                    .profileUrl(follow.getFollowing().getProfileUrl())
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

        for(Follow follow : loginMember.getFollowingList()) {
            GetFollowAndFollowingMemberDTO getFollowAndFollowingMemberDTO = GetFollowAndFollowingMemberDTO.builder()
                    .nickname(follow.getFollower().getNickname())
                    .profileUrl(follow.getFollower().getProfileUrl())
                    .build();
            followingMemberList.add(getFollowAndFollowingMemberDTO);
        }

        return followingMemberList;
    }
}
