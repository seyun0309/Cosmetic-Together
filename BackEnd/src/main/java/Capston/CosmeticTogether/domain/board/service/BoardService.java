package Capston.CosmeticTogether.domain.board.service;

import Capston.CosmeticTogether.domain.board.domain.Board;
import Capston.CosmeticTogether.domain.board.dto.request.CreateBoardRequestDTO;
import Capston.CosmeticTogether.domain.board.dto.response.BoardResponseDTO;
import Capston.CosmeticTogether.domain.board.repository.BoardRepository;
import Capston.CosmeticTogether.domain.follow.domain.Follow;
import Capston.CosmeticTogether.domain.member.domain.Member;
import Capston.CosmeticTogether.domain.member.service.MemberService;
import Capston.CosmeticTogether.global.auth.dto.security.SecurityMemberDTO;
import Capston.CosmeticTogether.global.enums.ErrorCode;
import Capston.CosmeticTogether.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberService memberService;

    @Transactional
    public void uploadBoard(String imgUrl, CreateBoardRequestDTO createBoardRequestDTO) {
        // 1. 로그인 한 사용자 가져오기
        Member loginMember = memberService.getMemberFromSecurityDTO((SecurityMemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // 2. 게시글 저장
        Board board = Board.builder()
                .description(createBoardRequestDTO.getDescription())
                .board_img_url(imgUrl)
                .member(loginMember)
                .build();

        boardRepository.save(board);
    }

    //TODO 사용자 프로필 사진도 같이 넘겨야 됨(마이페이지 만들어야 함)
    public BoardResponseDTO getBoard(Long boardId) {
        // 1. boardId로 board 가져오기
        Board board = boardRepository.findDeleteAtIsNullById(boardId).orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

        // 2. 작성자도 가져오기
        Member writer = board.getMember();

        // 3. 매핑해서 리턴
        return BoardResponseDTO.builder()
                .description(board.getDescription())
                .imgUrl(board.getBoard_img_url())
                .writerNickName(writer.getNickname())
                .build();
    }

    public List<BoardResponseDTO> getRecentBoard() {
        // 1. 모든 게시글 불러오기
        List<Board> all = boardRepository.findDeleteAtIsNullAll();
        List<BoardResponseDTO> response = new ArrayList<>();

        // 2. 매핑해서 리턴
        for(Board board : all) {
            BoardResponseDTO dto = BoardResponseDTO.builder()
                    .description(board.getDescription())
                    .imgUrl(board.getBoard_img_url())
                    .writerNickName(board.getMember().getNickname())
                    .build();
            response.add(dto);
        }
        return response;
    }

    public List<BoardResponseDTO> getFollowingMemberBoard() {
        // 1. 로그인한 사용자 정보 가져오기
        Member loginMember = memberService.getMemberFromSecurityDTO((SecurityMemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // 2. 로그인한 사용자가 팔로우한 사람들의 목록 가져오기
        List<Member> followingMembers = new ArrayList<>();
        for (Follow follow : loginMember.getFollowingList()) {
            followingMembers.add(follow.getFollowing()); // Follow 엔티티에서 following 필드를 가져옴
        }

        // 3. 팔로우한 사람들의 게시글 조회
        List<Board> followingBoards = boardRepository.findByFollowingMembers(followingMembers);

        // 4. 매핑해서 리턴
        List<BoardResponseDTO> response = new ArrayList<>();

        for(Board board : followingBoards) {
            BoardResponseDTO dto = BoardResponseDTO.builder()
                    .description(board.getDescription())
                    .imgUrl(board.getBoard_img_url())
                    .writerNickName(board.getMember().getNickname())
                    .build();
            response.add(dto);
        }
        return response;
    }

    @Transactional
    public void updateBoard(Long boardId, String imgUrl, CreateBoardRequestDTO createBoardRequestDTO) {
        // 1. 유효한 boardId인지 확인
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

        // 2. 로그인한 사용자랑 게시글의 작성자가 같은지 확인
        Member loginMember = memberService.getMemberFromSecurityDTO((SecurityMemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        if(loginMember.equals(board.getMember())) {
            // 3. 수정
            board.update(createBoardRequestDTO.getDescription(), imgUrl);
            boardRepository.save(board);
        } else {
            throw new BusinessException(ErrorCode.NOT_WRITER_OF_POST);
        }
    }

    @Transactional
    public void deleteBoard(Long boardId) {
        // 1. 유효한 boardId인지 확인
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new BusinessException(ErrorCode.BOARD_NOT_FOUND));

        // 2. 로그인한 사용자랑 게시글 작성자가 같은지 확인
        Member loginMember = memberService.getMemberFromSecurityDTO((SecurityMemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        if(loginMember.equals(board.getMember())) {
            // 3. 삭제 처리(논리적 삭제로 진행)
            board.setDeletedAt(LocalDateTime.now());
            boardRepository.save(board);
        } else {
            throw new BusinessException(ErrorCode.NOT_WRITER_OF_POST);
        }
    }
}
