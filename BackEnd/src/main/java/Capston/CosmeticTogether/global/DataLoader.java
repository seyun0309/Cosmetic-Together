package Capston.CosmeticTogether.global;

import Capston.CosmeticTogether.domain.board.repository.BoardRepository;
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

import java.util.Arrays;
import java.util.List;

@Component
public class DataLoader implements ApplicationRunner {
    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public DataLoader(MemberRepository memberRepository, BoardRepository boardRepository, JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.boardRepository = boardRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        memberRepository.deleteAll();
        boardRepository.deleteAll();

        // 시퀀스 초기화
        jdbcTemplate.execute("ALTER SEQUENCE member_id_seq RESTART WITH 1");
        jdbcTemplate.execute("ALTER SEQUENCE board_id_seq RESTART WITH 1");

        String encodedPassword = passwordEncoder.encode("1234");

        List<Member> members = Arrays.asList(
                new Member("사용자A", "test1@naver.com", encodedPassword, "010-1111-2222", "사용자A", "서울특별시", Role.USER, AuthType.REGULAR),
                new Member("사용자B", "test2@naver.com", encodedPassword, "010-1111-2222", "사용자B", "서울특별시", Role.USER, AuthType.REGULAR),
                new Member("사용자C", "test3@naver.com", encodedPassword, "010-1111-2222", "사용자C", "서울특별시", Role.USER, AuthType.REGULAR),
                new Member("사용자D", "test4@naver.com", encodedPassword, "010-1111-2222", "사용자D", "서울특별시", Role.USER, AuthType.REGULAR),
                new Member("사용자E", "test5@naver.com", encodedPassword, "010-1111-2222", "사용자E", "서울특별시", Role.USER, AuthType.REGULAR)
        );

        memberRepository.saveAll(members);
    }
}
