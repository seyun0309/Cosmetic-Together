package Capston.CosmeticTogether.domain.comment.service;

import Capston.CosmeticTogether.domain.board.domain.Board;
import Capston.CosmeticTogether.domain.board.dto.request.CreateBoardRequestDTO;
import Capston.CosmeticTogether.domain.board.repository.BoardRepository;
import Capston.CosmeticTogether.domain.comment.domain.Comment;
import Capston.CosmeticTogether.domain.comment.dto.request.CreateCommentRequestDTO;
import Capston.CosmeticTogether.domain.comment.repository.CommentRepository;
import Capston.CosmeticTogether.domain.member.domain.Member;
import Capston.CosmeticTogether.domain.member.service.MemberService;
import Capston.CosmeticTogether.global.auth.dto.security.SecurityMemberDTO;
import Capston.CosmeticTogether.global.enums.ErrorCode;
import Capston.CosmeticTogether.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {
    private final CommentRepository commentRepository;
    private final BoardRepository boardRepository;
    private final MemberService memberService;

    public void createComment(CreateCommentRequestDTO createCommentRequestDTO) {
        // 1. 작성자 가져오기
        Member loginMember = memberService.getMemberFromSecurityDTO((SecurityMemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // 2. 게시글 유효성 체크
        Board board = boardRepository.findById(createCommentRequestDTO.getBoardId()).orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

        // 3. 게시글 등록
        Comment comment = Comment.builder()
                .content(createCommentRequestDTO.getComment())
                .commenter(loginMember)
                .board(board)
                .build();

        commentRepository.save(comment);
    }

    public void deleteComment(Long commentId) {
        // 1. 유효한 commentId인지 확인
        Comment comment = commentRepository.findById(commentId).orElseThrow(() -> new BusinessException(ErrorCode.COMMENT_NOT_FOUND));

        // 2. 작성자 로그인 유저 비교
        Member loginMember = memberService.getMemberFromSecurityDTO((SecurityMemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        if(loginMember.equals(comment.getCommenter())) {
            // 3. 삭제 처리
            comment.setDeletedAt(LocalDateTime.now());
            commentRepository.save(comment);
        } else {
            throw new BusinessException(ErrorCode.NOT_COMMENTER_OF_BOARD);
        }
    }
}
