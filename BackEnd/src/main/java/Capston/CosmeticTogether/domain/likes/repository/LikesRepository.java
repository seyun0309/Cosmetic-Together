package Capston.CosmeticTogether.domain.likes.repository;

import Capston.CosmeticTogether.domain.board.domain.Board;
import Capston.CosmeticTogether.domain.likes.domain.Likes;
import Capston.CosmeticTogether.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LikesRepository extends JpaRepository<Likes, Long> {

    Likes findByMemberAndBoard(Member loginMember, Board board);

    // 게시글의 좋아요 카운트하는 쿼리
    @Query("SELECT COUNT(l) FROM Likes l WHERE l.board.id = :boardId AND l.isValid = true")
    long countLikesByBoardId(@Param("boardId") Long boardId);

    // 특정 사용자가 좋아요한 게시물 리스트를 가져오는 쿼리
    @Query("SELECT l.board FROM Likes l WHERE l.member.id = :memberId AND l.isValid = true AND l.board.deletedAt IS NULL")
    List<Board> findLikedBoardsByMemberId(@Param("memberId") Long memberId);
}
