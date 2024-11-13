package Capston.CosmeticTogether.global;

import Capston.CosmeticTogether.domain.board.domain.Board;
import Capston.CosmeticTogether.domain.board.domain.BoardImage;
import Capston.CosmeticTogether.domain.board.repository.BoardImageRepository;
import Capston.CosmeticTogether.domain.board.repository.BoardRepository;
import Capston.CosmeticTogether.domain.board.service.S3ImageService;
import Capston.CosmeticTogether.domain.comment.repository.CommentRepository;
import Capston.CosmeticTogether.domain.form.repository.FormRepository;
import Capston.CosmeticTogether.domain.member.domain.Member;
import Capston.CosmeticTogether.domain.member.repository.MemberRepository;
import Capston.CosmeticTogether.global.enums.AuthType;
import Capston.CosmeticTogether.global.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader implements ApplicationRunner {
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final BoardImageRepository boardImageRepository;
    private final FormRepository formRepository;
    private final CommentRepository commentRepository;
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;
    private final S3ImageService s3ImageService;


    @Autowired
    public DataLoader(MemberRepository memberRepository, BoardRepository boardRepository, BoardImageRepository boardImageRepository, FormRepository formRepository, CommentRepository commentRepository, JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder, S3ImageService s3ImageService) {
        this.memberRepository = memberRepository;
        this.boardRepository = boardRepository;
        this.boardImageRepository = boardImageRepository;
        this.formRepository = formRepository;
        this.commentRepository = commentRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
        this.s3ImageService = s3ImageService;
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        memberRepository.deleteAll();
        boardRepository.deleteAll();
        formRepository.deleteAll();
        commentRepository.deleteAll();
        boardImageRepository.deleteAll();

        // 시퀀스 초기화
        jdbcTemplate.execute("ALTER SEQUENCE member_id_seq RESTART WITH 1");
        jdbcTemplate.execute("ALTER SEQUENCE board_id_seq RESTART WITH 1");
        jdbcTemplate.execute("ALTER SEQUENCE form_id_seq RESTART WITH 1");
        jdbcTemplate.execute("ALTER SEQUENCE comment_id_seq RESTART WITH 1");
        jdbcTemplate.execute("ALTER SEQUENCE board_img_id_seq RESTART WITH 1");

        String encodedPassword = passwordEncoder.encode("1234");

        List<Member> members = Arrays.asList(
                new Member("김지상", "wltkd1097@naver.com", encodedPassword, "010-3192-7313", "김지상", "충청북도 충주시 연수동 1379-3", Role.USER, AuthType.REGULAR, "https://cosmetic-together-bucket.s3.ap-northeast-2.amazonaws.com/380d0c00-0default_profile.png"),
                new Member("강인서", "ilusixn26@naver.com", encodedPassword, "010-8372-0162", "강인서", "경기도 기흥구 구갈동", Role.USER, AuthType.REGULAR, "https://cosmetic-together-bucket.s3.ap-northeast-2.amazonaws.com/380d0c00-0default_profile.png"),
                new Member("이윤지", "nkje2001@naver.com", encodedPassword, "010-6123-9182", "이윤지", "서울특별시 성동구 성수동", Role.USER, AuthType.REGULAR, "https://cosmetic-together-bucket.s3.ap-northeast-2.amazonaws.com/380d0c00-0default_profile.png"),
                new Member("이하나", "alske0192@naver.com", encodedPassword, "010-1726-6412", "이하나", "부산광역시 해운대구 우동 1418-5 ", Role.USER, AuthType.REGULAR, "https://cosmetic-together-bucket.s3.ap-northeast-2.amazonaws.com/380d0c00-0default_profile.png"),
                new Member("김민정", "hiand_09@naver.com", encodedPassword, "010-0129-2812", "김민정", "대구광역시 중구 동성로2가 162", Role.USER, AuthType.REGULAR, "https://cosmetic-together-bucket.s3.ap-northeast-2.amazonaws.com/380d0c00-0default_profile.png")
        );
        memberRepository.saveAll(members);

        List<Board> boards = new ArrayList<>();

//        // 게시글과 이미지를 생성할 때 Board 객체를 먼저 생성하고 이를 BoardImage에 설정
//        Board boardA = new Board("A 게시글 테스트", new ArrayList<>(), members.get(0));
//        BoardImage boardImageA = new BoardImage("/img/SampleIMG1", boardA);  // BoardImage 생성 시 Board 설정
//        boardA.getBoardImages().add(boardImageA);  // Board에 BoardImage 추가
//        boards.add(boardA);
//
//        Board boardB = new Board("B 게시글 테스트", new ArrayList<>(), members.get(1));
//        BoardImage boardImageB = new BoardImage("/img/SampleIMG2", boardB);
//        boardB.getBoardImages().add(boardImageB);
//        boards.add(boardB);
//
//        // 나머지 게시글과 이미지를 비슷하게 추가
//        Board boardC = new Board("C 게시글 테스트", new ArrayList<>(), members.get(2));
//        BoardImage boardImageC = new BoardImage("/img/SampleIMG3", boardC);
//        boardC.getBoardImages().add(boardImageC);
//        boards.add(boardC);
//
//        Board boardD = new Board("D 게시글 테스트", new ArrayList<>(), members.get(3));
//        BoardImage boardImageD = new BoardImage("/img/SampleIMG4", boardD);
//        boardD.getBoardImages().add(boardImageD);
//        boards.add(boardD);
//
//        Board boardE = new Board("E 게시글 테스트", new ArrayList<>(), members.get(4));
//        BoardImage boardImageE = new BoardImage("/img/SampleIMG5", boardE);
//        boardE.getBoardImages().add(boardImageE);
//        boards.add(boardE);
//
//        Board boardF = new Board("F 게시글 테스트", new ArrayList<>(), members.get(0));
//        BoardImage boardImageF = new BoardImage("/img/SampleIMG2", boardF);
//        boardF.getBoardImages().add(boardImageF);
//        boards.add(boardF);
//
//        Board boardG = new Board("G 게시글 테스트", new ArrayList<>(), members.get(1));
//        BoardImage boardImageG = new BoardImage("/img/SampleIMG3", boardG);
//        boardG.getBoardImages().add(boardImageG);
//        boards.add(boardG);
//
//        Board boardH = new Board("H 게시글 테스트", new ArrayList<>(), members.get(2));
//        BoardImage boardImageH = new BoardImage("/img/SampleIMG1", boardH);
//        boardH.getBoardImages().add(boardImageH);
//        boards.add(boardH);
//
//        Board boardI = new Board("I 게시글 테스트", new ArrayList<>(), members.get(3));
//        BoardImage boardImageI = new BoardImage("/img/SampleIMG5", boardI);
//        boardI.getBoardImages().add(boardImageI);
//        boards.add(boardI);
//
//        Board boardJ = new Board("J 게시글 테스트", new ArrayList<>(), members.get(4));
//        BoardImage boardImageJ = new BoardImage("/img/SampleIMG4", boardJ);
//        boardJ.getBoardImages().add(boardImageJ);
//        boards.add(boardJ);
//
//        // 최종적으로 Board와 BoardImage 저장
//        boardRepository.saveAll(boards);

    }
}
