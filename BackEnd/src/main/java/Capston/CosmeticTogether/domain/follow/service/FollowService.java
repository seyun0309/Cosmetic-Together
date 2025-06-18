package Capston.CosmeticTogether.domain.follow.service;


import Capston.CosmeticTogether.domain.board.domain.Board;
import Capston.CosmeticTogether.domain.board.repository.BoardRepository;
import Capston.CosmeticTogether.domain.follow.domain.Follow;
import Capston.CosmeticTogether.domain.follow.dto.response.FollowResponseDTO;
import Capston.CosmeticTogether.domain.follow.repository.FollowRepository;
import Capston.CosmeticTogether.domain.form.domain.Form;
import Capston.CosmeticTogether.domain.form.repository.FormRepository;
import Capston.CosmeticTogether.domain.member.domain.Member;
import Capston.CosmeticTogether.domain.member.repository.MemberRepository;
import Capston.CosmeticTogether.global.auth.service.AuthUtil;
import Capston.CosmeticTogether.global.enums.ErrorCode;
import Capston.CosmeticTogether.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Slf4j
public class FollowService {
    private final FollowRepository followRepository;
    private final MemberRepository memberRepository;
    private final AuthUtil authUtil;
    private final BoardRepository boardRepository;
    private final FormRepository formRepository;

    @Transactional
    public FollowResponseDTO followOrUnFollowByBoardId(Long boardId) {

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

    @Transactional
    public FollowResponseDTO followOrUnFollowByFormId(Long formId) {

        Member loginMember = authUtil.extractMemberAfterTokenValidation();

        Form form = formRepository.findById(formId).orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

        Long followingId = form.getOrganizer().getId();

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

    @Transactional
    public FollowResponseDTO followOrUnFollowByMemberId(Long memberId) {
        Member loginMember = authUtil.extractMemberAfterTokenValidation();

        Long followingId = memberId;

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
}
