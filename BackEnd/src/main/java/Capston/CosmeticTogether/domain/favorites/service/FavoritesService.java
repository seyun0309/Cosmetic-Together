package Capston.CosmeticTogether.domain.favorites.service;


import Capston.CosmeticTogether.domain.favorites.domain.Favorites;
import Capston.CosmeticTogether.domain.favorites.repository.FavoritesRepository;
import Capston.CosmeticTogether.domain.form.domain.Form;
import Capston.CosmeticTogether.domain.form.repository.FormRepository;
import Capston.CosmeticTogether.domain.member.domain.Member;
import Capston.CosmeticTogether.global.auth.service.AuthUtil;
import Capston.CosmeticTogether.global.enums.ErrorCode;
import Capston.CosmeticTogether.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoritesService {
    private final AuthUtil authUtil;
    private final FormRepository formRepository;
    private final FavoritesRepository favoritesRepository;

    @Transactional
    public boolean favoriteOrUnFavoriteBoard(Long formId) {
        // 1. 로그인 사용자 정보 가져오기
        Member loginMember = authUtil.extractMemberAfterTokenValidation();

        // 2. boardId 유효성 체크
        Form form = formRepository.findById(formId).orElseThrow(() -> new BusinessException("존재하는 폼이 아닙니다", ErrorCode.FORM_NOT_FOUND));

        // 3. 찜 로직 진행: 이미 찜 했는지 확인
        Favorites existingFavorite = favoritesRepository.findByMemberAndForm(loginMember, form);

        if (existingFavorite != null) {
            // 이미 찜을 한 경우 -> 찜 취소
            if (existingFavorite.isValid()) {
                existingFavorite.setValid(false);
                return false;
            } else {
                // 찜이 취소된 상태면 다시 찜
                existingFavorite.setValid(true);
                return true;
            }
        } else {
            // 찜 기록이 없으면 새로운 찜 객체 생성
            Favorites newFavorite = Favorites.builder()
                    .member(loginMember)
                    .form(form)
                    .isValid(true) // 찜 상태로 설정
                    .build();
            favoritesRepository.save(newFavorite);
            return true;
        }
    }
}
