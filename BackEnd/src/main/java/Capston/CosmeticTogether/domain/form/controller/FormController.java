package Capston.CosmeticTogether.domain.form.controller;

import Capston.CosmeticTogether.ResponseMessage;
import Capston.CosmeticTogether.domain.board.service.S3ImageService;
import Capston.CosmeticTogether.domain.form.dto.request.CreateFormRequestDTO;
import Capston.CosmeticTogether.domain.form.dto.request.UpdateFormRequestDTO;
import Capston.CosmeticTogether.domain.form.dto.resonse.form.CreateFormResponseDTO;
import Capston.CosmeticTogether.domain.form.dto.resonse.form.DetailFormResponseDTO;
import Capston.CosmeticTogether.domain.form.dto.resonse.form.FormResponseDTO;
import Capston.CosmeticTogether.domain.form.dto.resonse.form.UpdateFormInfoResponseDTO;
import Capston.CosmeticTogether.domain.form.service.FormService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Tag(name = "폼", description = "등록, 세부 조회, 최신 조회, 팔로잉 조회, 키워드 조회, 수정, 삭제")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/form")
public class FormController {
    private final FormService formService;
    private final S3ImageService s3ImageService;

    // 작성자 폼 등록
    @PostMapping
    @Operation(summary = "폼 생성 - 토큰 필요", description = "thumnail(썸네일 사진), request(폼 내용), images(상품 사진들)을 보내면 폼이 생성됩니다")
    public ResponseEntity<ResponseMessage> createForm(@RequestPart(name = "thumbnail", required = false) MultipartFile thumbnail,
                                                            @RequestPart(name = "request") @Valid CreateFormRequestDTO createFormRequestDTO,
                                                            @RequestPart(name = "images") List<MultipartFile> images) {

        formService.createForm(thumbnail, createFormRequestDTO, images);
        return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), "폼 생성 완료"));
    }

    // 단일 폼 조회
    @GetMapping("/{formId}")
    @Operation(summary = "폼 세부 조회 - 토큰 필요", description = "URL의 formId를 통해서 폼의 세부 조회를 진행합니다")
    public ResponseEntity<DetailFormResponseDTO> getForm(@PathVariable("formId") Long formId) {
        DetailFormResponseDTO response = formService.getForm(formId);
        return ResponseEntity.ok(response);
    }

    // 최신 폼 조회
    @GetMapping("/recent")
    @Operation(summary = "최신 폼 조회", description = "폼을 최신순으로 조회합니다")
    public ResponseEntity<List<FormResponseDTO>> getRecentForms() {
        List<FormResponseDTO> responses = formService.getRecentForms();
        return ResponseEntity.ok(responses);
    }

    // 팔로잉 폼 조회
    @GetMapping("/following")
    @Operation(summary = "팔로잉 폼 조회 - 토큰 필요", description = "사용자가 팔로잉한 다른 사용자의 최신 폼들(List)을 리턴합니다")
    public ResponseEntity<List<FormResponseDTO>> getFollowingForms() {
        List<FormResponseDTO> responses = formService.getFollowingForms();
        return ResponseEntity.ok(responses);
    }

    // 키워드 검색
    @GetMapping()
    @Operation(summary = "[API] 키워드 통한 폼 검색", description = "사용자가 키워드를 검색창에 넣으면 키워드가 포함된 폼을 불러옵니다")
    public ResponseEntity<List<FormResponseDTO>> searchFormByKeyword(@RequestParam("keyword") String keyword) {
        List<FormResponseDTO> response;

        if(keyword == null) {
            response = formService.getRecentForms();
        } else {
            response = formService.searchFormByKeyword(keyword);
        }

        return ResponseEntity.ok(response);
    }

    // 수정할 때 정보 리턴
    @PostMapping("/info/{formId}")
    @Operation(summary = "[UI] 폼 정보 리턴 - 토큰필요", description = "폼 정보를 수정하는 화면에서 기존 폼 정보를 화면단에 띄우는 데에 사용합니다")
    public ResponseEntity<UpdateFormInfoResponseDTO> getFormInfo(@PathVariable("formId") Long formId) {
        UpdateFormInfoResponseDTO response = formService.getFormInfo(formId);
        return ResponseEntity.ok(response);
    }

    // 작성자 폼 수정
    @PostMapping("/{formId}")
    @Operation(summary = "폼 수정 - 토큰 필요", description = "thumnail(썸네일 사진), request(폼 내용), images(상품 사진들)을 보내면 폼이 생성됩니다")
    public ResponseEntity<ResponseMessage> updateForm(@PathVariable("formId") Long formId,
                                             @RequestPart(required = false, name = "thumbnail") MultipartFile thumbnail,
                                             @RequestPart(required = false, name = "request") @Valid UpdateFormRequestDTO updateFormRequestDTO) {
        String thumbnailURL = null;
        if (thumbnail != null && !thumbnail.isEmpty()) {
            thumbnailURL = s3ImageService.upload(thumbnail);
        }

        formService.updateForm(formId, thumbnailURL, updateFormRequestDTO);
        return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), "폼이 수정되었습니다"));
    }

    //작성자 폼 삭제
    @DeleteMapping("/{formId}")
    @Operation(summary = "폼 삭제 - 토큰 필요", description = "URL의 formId를 통해서 해당 폼을 삭제합니다")
    public ResponseEntity<ResponseMessage> deleteForm(@PathVariable("formId") Long formId) {
        formService.deleteForm(formId);
        return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), "폼이 삭제되었습니다"));
    }
}
