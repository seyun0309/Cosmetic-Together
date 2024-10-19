package Capston.CosmeticTogether.domain.board.repository;

import Capston.CosmeticTogether.domain.board.domain.BoardImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BoardImageRepository extends JpaRepository<BoardImage, Long> {
}
