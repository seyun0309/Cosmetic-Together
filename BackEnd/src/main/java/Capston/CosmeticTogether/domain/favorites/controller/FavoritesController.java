package Capston.CosmeticTogether.domain.favorites.controller;


import Capston.CosmeticTogether.ResponseMessage;
import Capston.CosmeticTogether.domain.favorites.service.FavoritesService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "찜 [폼]", description = "찜 팔로우, 찜 팔로우 취소")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/favorite")
public class FavoritesController {
    private final FavoritesService favoritesService;

    @PostMapping("/{formId}")
    @Operation(summary = "폼 찜 및 찜 취소 - 토큰필요", description = "폼을 사용자의 찜 목록에 추가 또는 삭제합니다")
    public ResponseEntity<ResponseMessage> likeOrUnlikeBoard(@PathVariable("formId") Long formId) {
        boolean isValid = favoritesService.favoriteOrUnFavoriteBoard(formId);
        if(isValid) {
            return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), "폼을 찜 목록에 추가하였습니다"));
        } else {
            return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), "폼을 찜 목록에 삭제하였습니다"));
        }
    }
}
