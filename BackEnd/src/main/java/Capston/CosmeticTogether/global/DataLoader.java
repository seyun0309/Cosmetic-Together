package Capston.CosmeticTogether.global;

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

@Component
public class DataLoader implements ApplicationRunner {
    private final MemberRepository memberRepository;
    private final JdbcTemplate jdbcTemplate;
    private final PasswordEncoder passwordEncoder;


    @Autowired
    public DataLoader(MemberRepository memberRepository, JdbcTemplate jdbcTemplate, PasswordEncoder passwordEncoder) {
        this.memberRepository = memberRepository;
        this.jdbcTemplate = jdbcTemplate;
        this.passwordEncoder = passwordEncoder;
    }


    @Override
    public void run(ApplicationArguments args) throws Exception {
        // 부서 초기 데이터 삽입
        memberRepository.deleteAll();

        // 시퀀스 초기화
        jdbcTemplate.execute("ALTER SEQUENCE member_id_seq RESTART WITH 1");

        String encodedPassword = passwordEncoder.encode("1234");

        Member member = Member.builder()
                .userName("사용자A")
                .email("asdf1234@naver.com")
                .password(encodedPassword)
                .phone("010-1111-2222")
                .nickname("사용자A")
                .address("서울특별시 !!!")
                .role(Role.USER)
                .authType(AuthType.REGULAR)
                .build();
        memberRepository.save(member);
    }
}
