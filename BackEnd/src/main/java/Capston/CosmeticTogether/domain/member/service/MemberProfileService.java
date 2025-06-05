package Capston.CosmeticTogether.domain.member.service;


import Capston.CosmeticTogether.domain.board.domain.Board;
import Capston.CosmeticTogether.domain.board.domain.BoardImage;
import Capston.CosmeticTogether.domain.board.dto.response.BoardSummaryResponseDTO;
import Capston.CosmeticTogether.domain.board.repository.BoardRepository;
import Capston.CosmeticTogether.domain.board.service.S3ImageService;
import Capston.CosmeticTogether.domain.comment.repository.CommentRepository;
import Capston.CosmeticTogether.domain.favorites.repository.FavoritesRepository;
import Capston.CosmeticTogether.domain.follow.domain.Follow;
import Capston.CosmeticTogether.domain.follow.repository.FollowRepository;
import Capston.CosmeticTogether.domain.form.domain.Form;
import Capston.CosmeticTogether.domain.form.dto.resonse.form.FormResponseDTO;
import Capston.CosmeticTogether.domain.likes.repository.LikesRepository;
import Capston.CosmeticTogether.domain.member.domain.Member;
import Capston.CosmeticTogether.domain.member.dto.response.GetFollowerListDTO;
import Capston.CosmeticTogether.domain.member.dto.response.GetFollowingListDTO;
import Capston.CosmeticTogether.domain.member.dto.response.MemberProfileResponseDTO;
import Capston.CosmeticTogether.domain.member.dto.PasswordCheckDTO;
import Capston.CosmeticTogether.domain.member.dto.response.MyPageOverviewResponseDTO;
import Capston.CosmeticTogether.domain.member.repository.MemberRepository;
import Capston.CosmeticTogether.global.auth.service.AuthUtil;
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
    private final S3ImageService s3ImageService;
    private final PasswordEncoder passwordEncoder;
    private final LikesRepository likesRepository;
    private final FavoritesRepository favoritesRepository;
    private final BoardRepository boardRepository;
    private final MemberRepository memberRepository;
    private final CommentRepository commentRepository;
    private final AuthUtil authUtil;
    private final FollowRepository followRepository;

    public MyPageOverviewResponseDTO getMyPageOverView() {
        // 1. 로그인 사용자 가져오기
        Member loginMember = authUtil.extractMemberAfterTokenValidation();

        // 2. 팔로잉, 팔로워 수 가져오기
        long followingCount = loginMember.getFollowingList().stream()
                .filter(Follow::isValid)
                .count();

        long followerCount = loginMember.getFollowerList().stream()
                .filter(Follow::isValid)
                .count();

        // 3. 매핑해서 리턴
        return MyPageOverviewResponseDTO.builder()
                .profileUrl(loginMember.getProfileUrl())
                .nickName(loginMember.getNickname())
                .followingCount(followingCount)
                .followerCount(followerCount)
                .build();
    }

    public List<GetFollowerListDTO> getFollowers() {
        // 1. 로그인 한 사용자 가져오기
        Member loginMember = authUtil.extractMemberAfterTokenValidation();

        // 2. 팔로워 리스트 가져오기
        return loginMember.getFollowerList().stream()
                .map(follow -> GetFollowerListDTO.builder()
                        .loginMemberName(loginMember.getNickname())
                        .followerMemberId(follow.getFollower().getId())
                        .nickname(follow.getFollower().getNickname())
                        .profileUrl(follow.getFollower().getProfileUrl())
                        .build())
                .collect(Collectors.toList());
    }

    public List<GetFollowingListDTO> getFollowings() {
        // 1. 로그인 한 사용자 가져오기
        Member loginMember = authUtil.extractMemberAfterTokenValidation();

        // 2. 팔로잉 리스트 가져오기
        return loginMember.getFollowingList().stream()
                .map(following -> {
                    boolean isFollowing = followRepository
                            .findByFollowerAndFollowingAndIsValidTrue(loginMember, following.getFollowing())
                            .isPresent();

                    return GetFollowingListDTO.builder()
                            .loginMemberName(loginMember.getNickname())
                            .followingMemberId(following.getFollowing().getId())
                            .nickname(following.getFollowing().getNickname())
                            .profileUrl(following.getFollowing().getProfileUrl())
                            .following(isFollowing)
                            .build();
                })
                .collect(Collectors.toList());
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
    public void updateMemberProfile(MultipartFile img) {
        // 1. 로그인 사용자 가져오기
        Member loginMember = authUtil.extractMemberAfterTokenValidation();

        // 2. 정보 수정

        // 2.1 기존 프로필 이미지 삭제
        s3ImageService.deleteImageFromS3(loginMember.getProfileUrl());

        // 2.2 새로운 프로필 이미지로 업데이트
        String imgUrl = s3ImageService.upload(img);
        loginMember.updateProfileUrl(imgUrl);

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
                    .formId(form.getId())
                    .thumbnail(form.getFormUrl())
                    .title(form.getTitle())
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
            return daysBetween + "일 전";
        } else if (hoursBetween >= 1) {
            return hoursBetween + "시간 전";
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

    public void updateUserNickname(String nickName) {
        // 1. 로그인 사용자 조회
        Member loginMember = authUtil.extractMemberAfterTokenValidation();

        // 2. 닉네임 변경
        loginMember.updateNickname(nickName);

        // 3. 다시 저장
        memberRepository.save(loginMember);
    }

    public void updateUserPassword(String password) {
        // 1. 로그인 사용자 조회
        Member loginMember = authUtil.extractMemberAfterTokenValidation();

        // 2. 닉네임 변경
        String hashedPassword = passwordEncoder.encode(password);
        loginMember.updatePassword(hashedPassword);

        // 3. 다시 저장
        memberRepository.save(loginMember);
    }
}
