package Capston.CosmeticTogether.domain.form.controller;


import Capston.CosmeticTogether.ResponseMessage;
import Capston.CosmeticTogether.domain.form.dto.request.OrderRequestDTO;
import Capston.CosmeticTogether.domain.form.dto.request.UpdateOrderStatusRequestDTO;
import Capston.CosmeticTogether.domain.form.dto.resonse.form.MyFormResponseDTO;
import Capston.CosmeticTogether.domain.form.dto.resonse.order.*;
import Capston.CosmeticTogether.domain.form.service.OrderService;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "주문", description = "주문 등록, 주문 삭제")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService orderService;

    // 주문 생성
    @PostMapping("/{formId}")
    @Operation(summary = "주문 등록 - 토큰 필요", description = "URL의 formId와 주문 정보를 보내면 주문 등록이 됩니다")
    public ResponseEntity<CreateOrderResponseDTO> createOrder(@PathVariable("formId") Long formId, @RequestBody OrderRequestDTO orderRequestDTO) {
        CreateOrderResponseDTO response = orderService.createOrder(formId, orderRequestDTO);
        return ResponseEntity.ok(response);
    }

    // 주문 후 판매자 계좌 내용 출력
    @GetMapping("/account/{orderId}")
    @Operation(summary = "주문 후 판매자의 계좌 내용 출력", description = "판매자 계좌 내용 출력")
    public ResponseEntity<AccountResponseDTO> getAccount(@PathVariable("orderId") Long orderId) {
        AccountResponseDTO response = orderService.getAccount(orderId);
        return ResponseEntity.ok(response);
    }

    // 주문 상태 변경
    @PostMapping("/status/{orderId}")
    @Operation(summary = "주문 상태 변경", description = "판매자 해당 주문의 상태를 변경합니다")
    public ResponseEntity<ResponseMessage> updateOrderStatus(@PathVariable("orderId") Long orderId, @RequestBody UpdateOrderStatusRequestDTO updateOrderStatusRequestDTO) {
        orderService.updateOrderStatus(orderId, updateOrderStatusRequestDTO);
        return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), "주문 상태가 변경되었습니다"));
    }


    // 주문 삭제
    @DeleteMapping("/{orderId}")
    @Operation(summary = "주문 삭제 - 토큰 필요", description = "URL의 orderId를 통해서 주문 삭제를 삭제합니다")
    public ResponseEntity<ResponseMessage> deleteOrder(@PathVariable("orderId") Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok(new ResponseMessage(HttpStatus.OK.value(), "주문이 삭제되었습니다"));
    }

    // 주문한 폼 조회
    @GetMapping("/my-order")
    @Operation(summary = "주문 작성한 폼 조회 - 토큰 필요", description = "사용자가 주문한 폼을 조회합니다")
    public ResponseEntity<List<OrderResponseDTO>> getOrderForm() {
        List<OrderResponseDTO> response = orderService.getOrderForm();
        return ResponseEntity.ok(response);
    }

    // 주문서 조회
    @GetMapping("/{orderId}")
    @Operation(summary = "주문서 조회 - 토큰 필요", description = "사용자가 주문한 폼을 조회합니다")
    public ResponseEntity<OrderDetailResponseDTO> getOrder(@PathVariable("orderId") Long orderId) {
        OrderDetailResponseDTO response = orderService.getOrder(orderId);
        return ResponseEntity.ok(response);
    }

    // 작성 폼 조회
    @GetMapping("/my-form")
    @Operation(summary = "내가 작성한 폼 조회 - 토큰필요", description = "토큰을 통해 해당 사용자가 작성한 폼을 조회합니다")
    public ResponseEntity<List<MyFormResponseDTO>> getMyForm() {
        List<MyFormResponseDTO> response = orderService.getMyForm();
        return ResponseEntity.ok(response);
    }

    // 판매내역 - 주문서 리스트 조회
    @GetMapping("/my-form/{formId}")
    @Operation(summary = "판매자 기준 주문서 리스트 조회 - 토큰 필요", description = "판매자 기준 해당 폼에 들어온 주문 리스트를 조회합니다")
    public ResponseEntity<SellerOrderResponseDTO> getSalesOrders(@PathVariable("formId") Long formId) {
        SellerOrderResponseDTO response = orderService.getSalesOrders(formId);
        return ResponseEntity.ok(response);
    }
}
