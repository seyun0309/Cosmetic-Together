package Capston.CosmeticTogether.domain.member.service;


import Capston.CosmeticTogether.domain.board.domain.Board;
import Capston.CosmeticTogether.domain.board.domain.BoardImage;
import Capston.CosmeticTogether.domain.board.dto.response.BoardSummaryResponseDTO;
import Capston.CosmeticTogether.domain.board.repository.BoardRepository;
import Capston.CosmeticTogether.domain.board.service.S3ImageService;
import Capston.CosmeticTogether.domain.comment.repository.CommentRepository;
import Capston.CosmeticTogether.domain.favorites.repository.FavoritesRepository;
import Capston.CosmeticTogether.domain.form.domain.Form;
import Capston.CosmeticTogether.domain.form.dto.resonse.form.FormResponseDTO;
import Capston.CosmeticTogether.domain.likes.repository.LikesRepository;
import Capston.CosmeticTogether.domain.member.domain.Member;
import Capston.CosmeticTogether.domain.member.dto.request.MemberUpdateRequestDTO;
import Capston.CosmeticTogether.domain.member.dto.response.MemberProfileResponseDTO;
import Capston.CosmeticTogether.domain.member.dto.PasswordCheckDTO;
import Capston.CosmeticTogether.domain.member.dto.response.MyPageOverviewResponseDTO;
import Capston.CosmeticTogether.domain.member.repository.MemberRepository;
import Capston.CosmeticTogether.global.auth.service.AuthUtil;
import Capston.CosmeticTogether.global.enums.Role;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
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
public class MemberProfileService {
    private final MemberService memberService;
    private final S3ImageService s3ImageService;
    private final PasswordEncoder passwordEncoder;
    private final LikesRepository likesRepository;
    private final FavoritesRepository favoritesRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final AuthUtil authUtil;

    public MyPageOverviewResponseDTO getMyPageOverView() {
        // 1. 로그인 사용자 가져오기
        Member loginMember = authUtil.extractMemberAfterTokenValidation();

        // 2. 매핑해서 리턴
        return MyPageOverviewResponseDTO.builder()
                .profileUrl(loginMember.getProfileUrl())
                .nickName(loginMember.getNickname())
                .build();
    }

    public boolean checkPassword(PasswordCheckDTO passwordCheckDTO) {
        // 1. 로그인 사용자 가져오기
        Member loginMember = authUtil.extractMemberAfterTokenValidation();

        // 2. 로그인 사용자와 passwordCheckDTO 비교해서 boolean 값 리턴
        return passwordEncoder.matches(passwordCheckDTO.getPassword(), loginMember.getPassword());
    }

    public MemberProfileResponseDTO getMemberProfile() {
        // 1. 로그인 사용자 가져오기
        Member loginMember = authUtil.extractMemberAfterTokenValidation();

        // 2. 사용자 정보 매핑해서 리턴
        return MemberProfileResponseDTO.builder()
                .nickname(loginMember.getNickname())
                .email(loginMember.getEmail())
                .phone(loginMember.getPhone())
                .address(loginMember.getAddress())
                .build();
    }

    @Transactional
    public void updateMemberProfile(MultipartFile profileUrl, MultipartFile backgroundUrl, MemberUpdateRequestDTO memberUpdateRequestDTO) {
        // 1. 로그인 사용자 가져오기
        Member loginMember = authUtil.extractMemberAfterTokenValidation();

        // 2. 정보 수정

        //2.1 이미지가 원래 없었다면 그냥 넣기
        if(loginMember.getProfileUrl().isEmpty()) {
            s3ImageService.upload(profileUrl);
            loginMember.updateMemberInfo(memberUpdateRequestDTO, Role.USER);
        } else {
            // 2.2 이미지가 원래 있었다면 기존 이미지 삭제하고 진행
            s3ImageService.deleteImageFromS3(loginMember.getProfileUrl());
            loginMember.updateMemberInfo(memberUpdateRequestDTO, Role.USER);
        }
        if(loginMember.getBackgroundUrl().isEmpty()) {
            s3ImageService.upload(backgroundUrl);
            loginMember.updateMemberInfo(memberUpdateRequestDTO, Role.USER);
        } else {
            s3ImageService.deleteImageFromS3(loginMember.getBackgroundUrl());
            loginMember.updateMemberInfo(memberUpdateRequestDTO, Role.USER);
        }

        memberRepository.save(loginMember);
    }

    public List<BoardSummaryResponseDTO> getLikedBoard() {
        // 1. 로그인 사용자 가져오기
        Member loginMember = authUtil.extractMemberAfterTokenValidation();

        // 2. 좋아요한 게시물 리스트 가져오기
        List<Board> boardList = likesRepository.findLikedBoardsByMemberId(loginMember.getId());

        // 3. 매핑해서 리턴
        List<BoardSummaryResponseDTO> response = new ArrayList<>();

        for(Board board : boardList) {
            long likeCount = likesRepository.countLikesByBoardId(board.getId());
            long commentCount = commentRepository.countByBoard(board);

            List<String> imageUrls = board.getBoardImages().stream()
                    .map(BoardImage::getBoardUrl)
                    .collect(Collectors.toList());

            String postTime = formatTime(board.getCreatedAt());

            BoardSummaryResponseDTO boardSummaryResponseDTO = BoardSummaryResponseDTO.builder()
                    .boardId(board.getId())
                    .writerNickName(board.getMember().getNickname())
                    .profileUrl(board.getMember().getProfileUrl())
                    .description(board.getDescription())
                    .boardUrl(imageUrls)
                    .likeCount(likeCount)
                    .postTime(postTime)
                    .commentCount(commentCount)
                    .build();
            response.add(boardSummaryResponseDTO);
        }
        return response;
    }

    public List<FormResponseDTO> getFavoriteForm() {
        // 1. 로그인 사용자 가져오기
        Member loginMember = authUtil.extractMemberAfterTokenValidation();

        // 2. 찜한 폼 리스트 가져오기
        List<Form> formList = favoritesRepository.findFavoritesFormsByMemberId(loginMember.getId());

        // 3. 매핑해서 리턴
        List<FormResponseDTO> response = new ArrayList<>();
        for(Form form : formList) {
            long favoritesCount = favoritesRepository.countFavoritesByFormId(form.getId());

            FormResponseDTO formResponseDTO = FormResponseDTO.builder()
                    .thumbnail(form.getFormUrl())
                    .organizerName(form.getOrganizer().getNickname())
                    .organizer_url(form.getOrganizer().getProfileUrl())
                    .formStatus(form.getFormStatus().getDescription())
                    .build();

            response.add(formResponseDTO);
        }
        return response;
    }

    public List<BoardSummaryResponseDTO> getMyBoard() {
        // 1. 로그인 사용자 가져오기
        Member loginMember = authUtil.extractMemberAfterTokenValidation();

        // 2. 사용자가 작성한 게시글 리스트 가져오기
        List<Board> boardList = boardRepository.findByMemberId(loginMember.getId());

        // 3. 매핑해서 리턴
        List<BoardSummaryResponseDTO> response = new ArrayList<>();
        for(Board board : boardList) {
            long likeCount = likesRepository.countLikesByBoardId(board.getId());
            long commentCount = commentRepository.countByBoard(board);

            List<String> imageUrls = board.getBoardImages().stream()
                    .map(BoardImage::getBoardUrl)
                    .collect(Collectors.toList());

            // 4. 작성시간 포맷팅
            String postTime = formatTime(board.getCreatedAt());

            BoardSummaryResponseDTO dto = BoardSummaryResponseDTO.builder()
                    .boardId(board.getId())
                    .writerNickName(board.getMember().getNickname())
                    .profileUrl(board.getMember().getProfileUrl())
                    .description(board.getDescription())
                    .boardUrl(imageUrls)
                    .likeCount(likeCount)
                    .postTime(postTime)
                    .commentCount(commentCount)
                    .build();

            response.add(dto);
        }
        return response;
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

    public void updateUserAddress(String address) {
        // 1. 로그인 사용자 조회
        Member loginMember = authUtil.extractMemberAfterTokenValidation();

        // 2. 주소 변경
        loginMember.updateAddress(address);

        // 3. 다시 저장
        memberRepository.save(loginMember);

    }
}
