package Capston.CosmeticTogether.domain.form.repository;

import Capston.CosmeticTogether.domain.board.domain.Board;
import Capston.CosmeticTogether.domain.form.domain.Form;
import Capston.CosmeticTogether.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FormRepository extends JpaRepository<Form, Long> {

    @Query("SELECT f FROM Form f WHERE f.organizer IN :followingMembers AND f.deletedAt IS NULL ORDER BY f.createdAt DESC")
    List<Form> findByFollowingMembers(@Param("followingMembers") List<Member> followingMembers);

    @Query("SELECT f FROM Form f WHERE f.deletedAt IS NULL ORDER BY f.createdAt DESC")
    List<Form> findDeleteAtIsNullAll();

    @Query("SELECT f FROM Form f LEFT JOIN f.product p WHERE f.deletedAt IS NULL AND f.id = :formId")
    Optional<Form> findDeleteAtIsNullById(@Param("formId") Long formId);

    @Query("SELECT f FROM Form f WHERE f.deletedAt IS NULL AND f.organizer.id = :memberId")
    List<Form> findByMemberId(@Param("memberId")Long memberId);

    @Query( "SELECT DISTINCT f FROM Form f LEFT JOIN f.product p WHERE (f.title LIKE %:keyword% OR f.formDescription LIKE %:keyword% OR p.productName LIKE %:keyword%) AND f.deletedAt IS NULL ORDER BY f.createdAt DESC")
    List<Form> findByKeywordInTitleOrDescriptionOrProductName(@Param("keyword") String keyword);
}
