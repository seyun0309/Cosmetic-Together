package Capston.CosmeticTogether.domain.board.service;

import Capston.CosmeticTogether.domain.board.domain.Board;
import Capston.CosmeticTogether.domain.board.domain.BoardImage;
import Capston.CosmeticTogether.domain.board.dto.request.CreateBoardRequestDTO;
import Capston.CosmeticTogether.domain.board.dto.response.GetBoardResponseDTO;
import Capston.CosmeticTogether.domain.board.dto.response.UpdateBoardResponseDTO;
import Capston.CosmeticTogether.domain.board.repository.BoardRepository;
import Capston.CosmeticTogether.domain.follow.domain.Follow;
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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class BoardService {
    private final BoardRepository boardRepository;
    private final MemberService memberService;
    private final LikesRepository likesRepository;
    private final S3ImageService s3ImageService;

    @Transactional
    public void uploadBoard(List<MultipartFile> images, CreateBoardRequestDTO createBoardRequestDTO) {
        // 1. 로그인 한 사용자 가져오기
        Member loginMember = memberService.getMemberFromSecurityDTO((SecurityMemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // 2. 게시글 저장
        Board board = Board.builder()
                .description(createBoardRequestDTO.getDescription())
                .member(loginMember)
                .build();

        // 3. 이미지 저장 처리
        if (images != null && !images.isEmpty()) {
            saveBoardImages(images, board);
        }

        boardRepository.save(board);
    }

    private void saveBoardImages(List<MultipartFile> images, Board board) {
        List<BoardImage> boardImages = new ArrayList<>();

        for (MultipartFile image : images) {
            if (!image.isEmpty()) {
                String imageUrl = s3ImageService.upload(image);
                BoardImage boardImage = new BoardImage(imageUrl, board);
                boardImages.add(boardImage);
            }
        }

        board.getBoardImages().addAll(boardImages);
    }

    public GetBoardResponseDTO getBoard(Long boardId) {
        // 1. boardId로 board 가져오기
        Board board = boardRepository.findDeleteAtIsNullById(boardId).orElseThrow(() -> new BusinessException("존재하는 게시글이 아닙니다", ErrorCode.BOARD_NOT_FOUND));
        long likeCount = likesRepository.countLikesByBoardId(board.getId());

        // 2. 작성자 추출
        Member writer = board.getMember();

        // 3. BoardImage 리스트에서 이미지 URL 추출
        List<String> imageUrls = board.getBoardImages().stream()
                .map(BoardImage::getImageUrl)
                .collect(Collectors.toList());

        // 4. 작성시간 포맷팅
        String postTime = formatTime(board.getCreatedAt());

        // 5. 매핑해서 리턴
        return GetBoardResponseDTO.builder()
                .writerNickName(writer.getNickname())
                .profileUrl(board.getMember().getProfile_url())
                .description(board.getDescription())
                .boardUrl(imageUrls)
                .likeCount(likeCount)
                .postTime(postTime)
                .build();
    }

    public List<GetBoardResponseDTO> getRecentBoard() {
        // 1. 모든 게시글 불러오기
        List<Board> all = boardRepository.findDeleteAtIsNullAll();
        List<GetBoardResponseDTO> response = new ArrayList<>();

        // 2. 매핑해서 리턴
        for(Board board : all) {
            long likeCount = likesRepository.countLikesByBoardId(board.getId());

            // 2-1. BoardImage 리스트에서 이미지 URL 추출
            List<String> imageUrls = board.getBoardImages().stream()
                    .map(BoardImage::getImageUrl)
                    .collect(Collectors.toList());

            // 2-2. 작성시간 포맷팅
            String postTime = formatTime(board.getCreatedAt());

            GetBoardResponseDTO getBoardResponseDTO = GetBoardResponseDTO.builder()
                    .writerNickName(board.getMember().getNickname())
                    .profileUrl(board.getMember().getProfile_url())
                    .description(board.getDescription())
                    .boardUrl(imageUrls)
                    .likeCount(likeCount)
                    .postTime(postTime)
                    .build();
            response.add(getBoardResponseDTO);
        }
        return response;
    }

    public List<GetBoardResponseDTO> getFollowingMemberBoard() {
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
        List<GetBoardResponseDTO> response = new ArrayList<>();

        for(Board board : followingBoards) {
            long likeCount = likesRepository.countLikesByBoardId(board.getId());

            // 2-1. BoardImage 리스트에서 이미지 URL 추출
            List<String> imageUrls = board.getBoardImages().stream()
                    .map(BoardImage::getImageUrl)
                    .collect(Collectors.toList());

            // 2-2. 작성시간 포맷팅
            String postTime = formatTime(board.getCreatedAt());

            GetBoardResponseDTO dto = GetBoardResponseDTO.builder()
                    .writerNickName(board.getMember().getNickname())
                    .profileUrl(board.getMember().getProfile_url())
                    .description(board.getDescription())
                    .boardUrl(imageUrls)
                    .likeCount(likeCount)
                    .postTime(postTime)
                    .build();
            response.add(dto);
        }
        return response;
    }

    public UpdateBoardResponseDTO getBoardUpdateInfo(Long boardId) {
        // 1. 유효한 boardId인지 확인
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new BusinessException("존재하는 게시글이 아닙니다", ErrorCode.BOARD_NOT_FOUND));

        // 2. 로그인한 사용자랑 게시글의 작성자가 같은지 확인
        Member loginMember = memberService.getMemberFromSecurityDTO((SecurityMemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // 3. 정보 매핑해서 리턴
        List<String> imageUrls = board.getBoardImages().stream()
                .map(BoardImage::getImageUrl)
                .collect(Collectors.toList());

        return UpdateBoardResponseDTO.builder()
                .description(board.getDescription())
                .boardUrl(imageUrls)
                .build();
    }

    @Transactional
    public void updateBoard(Long boardId, List<MultipartFile> images, CreateBoardRequestDTO createBoardRequestDTO) {
        // 1. 유효한 boardId인지 확인
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new BusinessException("존재하는 게시글이 아닙니다", ErrorCode.BOARD_NOT_FOUND));
        // 2. 로그인한 사용자랑 게시글의 작성자가 같은지 확인
        Member loginMember = memberService.getMemberFromSecurityDTO((SecurityMemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        if(loginMember.equals(board.getMember())) {
            // 3. 수정
            board.update(createBoardRequestDTO.getDescription());

            // 3-1. 기존 이미지 삭제
            board.getBoardImages().clear();

            // 3. 이미지 저장 처리
            if (images != null && !images.isEmpty()) {
                saveBoardImages(images, board);
            }

            boardRepository.save(board);
        } else {
            throw new BusinessException("해당 게시글의 작성자가 아닙니다", ErrorCode.NOT_WRITER_OF_POST);
        }
    }

    @Transactional
    public void deleteBoard(Long boardId) {
        // 1. 유효한 boardId인지 확인
        Board board = boardRepository.findById(boardId).orElseThrow(() -> new BusinessException("존재하는 게시글이 아닙니다", ErrorCode.BOARD_NOT_FOUND));

        // 2. 로그인한 사용자랑 게시글 작성자가 같은지 확인
        Member loginMember = memberService.getMemberFromSecurityDTO((SecurityMemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        if(loginMember.equals(board.getMember())) {
            // 3. 삭제 처리(논리적 삭제로 진행)
            board.setDeletedAt(LocalDateTime.now());
            boardRepository.save(board);
        } else {
            throw new BusinessException("해당 게시글의 작성자가 아닙니다", ErrorCode.NOT_WRITER_OF_POST);
        }
    }

    public static String formatTime(LocalDateTime createdAt) {
        LocalDateTime now = LocalDateTime.now();

        // 생성 시간과 현재 시간의 차이를 계산
        long daysBetween = ChronoUnit.DAYS.between(createdAt.toLocalDate(), now.toLocalDate());
        long hoursBetween = ChronoUnit.HOURS.between(createdAt, now);

        // 하루 전에 생성된 경우
        if (daysBetween >= 1) {
            return daysBetween + "일";
        } else if (hoursBetween >= 1) {
            return hoursBetween + "시";
        } else {
            return "방금";
        }
    }
}
