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
                new Member("김지상", "wltkd1097@naver.com", encodedPassword, "010-3192-7313", "김지상", "충청북도 충주시 연수동 1379-3", Role.USER, AuthType.REGULAR, "https://deptmanagement-s3-bucket.s3.ap-northeast-2.amazonaws.com/11820363.png"),
                new Member("강인서", "ilusixn26@naver.com", encodedPassword, "010-8372-0162", "강인서", "경기도 기흥구 구갈동", Role.USER, AuthType.REGULAR, "https://deptmanagement-s3-bucket.s3.ap-northeast-2.amazonaws.com/11820363.png"),
                new Member("이윤지", "nkje2001@naver.com", encodedPassword, "010-6123-9182", "이윤지", "서울특별시 성동구 성수동", Role.USER, AuthType.REGULAR, "https://deptmanagement-s3-bucket.s3.ap-northeast-2.amazonaws.com/11820363.png"),
                new Member("이하나", "alske0192@naver.com", encodedPassword, "010-1726-6412", "이하나", "부산광역시 해운대구 우동 1418-5 ", Role.USER, AuthType.REGULAR, "https://deptmanagement-s3-bucket.s3.ap-northeast-2.amazonaws.com/11820363.png"),
                new Member("김민정", "hiand_09@naver.com", encodedPassword, "010-0129-2812", "김민정", "대구광역시 중구 동성로2가 162", Role.USER, AuthType.REGULAR, "https://deptmanagement-s3-bucket.s3.ap-northeast-2.amazonaws.com/11820363.png")
        );
        memberRepository.saveAll(members);
    }
}
