package Capston.CosmeticTogether.domain.form.controller;


import Capston.CosmeticTogether.domain.form.dto.request.OrderRequestDTO;
import Capston.CosmeticTogether.domain.form.service.OrderService;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Tag(name = "주문", description = "주문 등록, 주문 삭제")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/v1/order")
public class OrderController {

    private final OrderService orderService;

    // 주문 생성
    // TODO 주문 고유번호 리턴
    @PostMapping("/{formId}")
    @Operation(summary = "주문 등록 - 토큰 필요", description = "URL의 formId와 주문 정보를 보내면 주문 등록이 됩니다")
    public ResponseEntity<String> createOrder(@PathVariable("formId") Long formId, @RequestBody OrderRequestDTO orderRequestDTO) {
        orderService.createOrder(formId, orderRequestDTO);

        return ResponseEntity.ok("주문이 완료되었습니다");
    }

    // 주문 삭제
    @DeleteMapping("/{orderId}")
    @Operation(summary = "주문 삭제 - 토큰 필요", description = "URL의 orderId를 통해서 주문 삭제를 삭제합니다")
    public ResponseEntity<String> deleteOrder(@PathVariable("orderId") Long orderId) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.ok("주문이 삭제되었습니다");
    }
}
