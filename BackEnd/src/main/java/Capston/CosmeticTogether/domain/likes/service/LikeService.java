package Capston.CosmeticTogether.domain.likes.service;

import Capston.CosmeticTogether.domain.board.domain.Board;
import Capston.CosmeticTogether.domain.board.repository.BoardRepository;
import Capston.CosmeticTogether.domain.likes.domain.Likes;
import Capston.CosmeticTogether.domain.likes.repository.LikesRepository;
import Capston.CosmeticTogether.domain.member.domain.Member;
import Capston.CosmeticTogether.domain.member.service.MemberService;
import Capston.CosmeticTogether.global.auth.dto.security.SecurityMemberDTO;
import Capston.CosmeticTogether.global.enums.ErrorCode;
import Capston.CosmeticTogether.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LikeService {
    private final MemberService memberService;
    private final BoardRepository boardRepository;
    private final LikesRepository likesRepository;

    public boolean likeOrUnlikeBoard(Long boardId) {
        // 1. 로그인 사용자 정보 가져오기
        Member loginMember = memberService.getMemberFromSecurityDTO((SecurityMemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // 2. boardId 유효성 체크
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new BusinessException("존재하는 게시글이 아닙니다", ErrorCode.BOARD_NOT_FOUND));

        // 3. 좋아요 로직 진행: 이미 좋아요 했는지 확인
        Likes existingLike = likesRepository.findByMemberAndBoard(loginMember, board);

        if (existingLike != null) {
            // 이미 좋아요를 한 경우 -> 좋아요 취소
            if (existingLike.isValid()) {
                existingLike.setValid(false);
                return false;
            } else {
                // 좋아요가 취소된 상태면 다시 좋아요
                existingLike.setValid(true);
                return true;
            }
        } else {
            // 좋아요 기록이 없으면 새로운 좋아요 객체 생성
            Likes newLike = Likes.builder()
                    .member(loginMember)
                    .board(board)
                    .isValid(true) // 좋아요 상태로 설정
                    .build();
            likesRepository.save(newLike);
            return true;
        }
    }
}
