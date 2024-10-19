package Capston.CosmeticTogether.domain.board.repository;

import Capston.CosmeticTogether.domain.board.domain.Board;
import Capston.CosmeticTogether.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BoardRepository extends JpaRepository<Board, Long> {

    // 팔로우한 사용자들의 게시글을 가져오는 쿼리
    @Query("SELECT b FROM Board b WHERE b.member IN :followingMembers AND b.deletedAt IS NULL ORDER BY b.createdAt DESC")
    List<Board> findByFollowingMembers(@Param("followingMembers") List<Member> followingMembers);

    @Query("SELECT b FROM Board b WHERE b.deletedAt IS NULL AND b.id = :boardId")
    Optional<Board> findDeleteAtIsNullById(@Param("boardId") Long boardId);

    @Query("SELECT b FROM Board b WHERE b.deletedAt IS NULL ORDER BY b.createdAt DESC")
    List<Board> findDeleteAtIsNullAll();

    @Query("SELECT b FROM Board b where b.deletedAt IS NULL AND b.member.id = :memberId")
    List<Board> findByMemberId(@Param("memberId")Long memberId);
}
