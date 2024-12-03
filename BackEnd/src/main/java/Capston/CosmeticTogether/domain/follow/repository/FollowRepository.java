package Capston.CosmeticTogether.domain.follow.repository;

import Capston.CosmeticTogether.domain.follow.domain.Follow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FollowRepository extends JpaRepository<Follow, Long> {
    Follow findByFollowerIdAndFollowingId(Long followingId, Long id);
}
