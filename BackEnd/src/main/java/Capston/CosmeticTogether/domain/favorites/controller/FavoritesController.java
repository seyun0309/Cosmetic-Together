package Capston.CosmeticTogether.domain.favorites.controller;


import Capston.CosmeticTogether.domain.favorites.service.FavoritesService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/favorite")
public class FavoritesController {
    private final FavoritesService favoritesService;

    @PostMapping("/{formId}")
    @Operation(summary = "폼 찜 및 찜 취소 - 토큰필요", description = "폼을 사용자의 찜 목록에 추가 또는 삭제합니다")
    public ResponseEntity<String> likeOrUnlikeBoard(@PathVariable("formId") Long formId) {
        boolean isValid = favoritesService.favoriteOrUnFavoriteBoard(formId);
        if(isValid) {
            return ResponseEntity.ok("해당 폼이 내 찜 목록에 추가되었습니다");
        } else {
            return ResponseEntity.ok("해당 폼이 내 찜 목록에 삭제되었습니다");
        }
    }
}
