package Capston.CosmeticTogether.domain.follow.repository;

import Capston.CosmeticTogether.domain.follow.domain.Follow;
import Capston.CosmeticTogether.domain.member.domain.Member;
import io.swagger.v3.oas.annotations.Parameters;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Follow findByFollowerIdAndFollowingId(Long followingId, Long id);

    @Query("SELECT f FROM Follow f WHERE f.follower = :loginMember AND f.following = :writer AND f.isValid = true")
    Optional<Follow> findByFollowerAndFollowingAndIsValidTrue(@Param("loginMember") Member loginMember, @Param("writer") Member writer);
}
