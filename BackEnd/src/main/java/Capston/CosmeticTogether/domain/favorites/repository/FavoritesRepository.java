package Capston.CosmeticTogether.domain.favorites.repository;

import Capston.CosmeticTogether.domain.favorites.domain.Favorites;
import Capston.CosmeticTogether.domain.form.domain.Form;
import Capston.CosmeticTogether.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoritesRepository extends JpaRepository<Favorites, Long> {

    @Query("SELECT COUNT(f) FROM Favorites f WHERE f.form.id = :formId AND f.isValid = true")
    long countFavoritesByFormId(@Param("formId") Long formId);

    Favorites findByMemberAndForm(Member loginMember, Form form);

    @Query("SELECT f.form FROM Favorites f WHERE f.member.id = :memberId AND f.isValid = true AND f.deletedAt IS NULL")
    List<Form> findFavoritesFormsByMemberId(@Param("memberId") Long memberId);

    @Query("SELECT f FROM Favorites f WHERE f.form.id = :formId AND f.member.id = :memberId AND f.isValid = true")
    Favorites findByFormIdAndMemberId(@Param("formId") Long formId, @Param("memberId")Long memberId);
}
