package Capston.CosmeticTogether.domain.member.repository;

import Capston.CosmeticTogether.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {
    @Modifying
    @Query("UPDATE Member m SET m.refreshToken = :refreshToken WHERE m.id = :id")
    void updateRefreshToken(@Param("id") Long id, @Param("refreshToken") String refreshToken);

    boolean existsByNickname(String nickName);

    String findRoleById(Long id);

    Optional<Member> findByEmail(String email);
}
