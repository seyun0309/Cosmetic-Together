package Capston.CosmeticTogether.domain.form.service;

import Capston.CosmeticTogether.domain.form.domain.Form;
import Capston.CosmeticTogether.domain.form.domain.Order;
import Capston.CosmeticTogether.domain.form.domain.OrderProduct;
import Capston.CosmeticTogether.domain.form.domain.Product;
import Capston.CosmeticTogether.domain.form.dto.request.OrderRequestDTO;
import Capston.CosmeticTogether.domain.form.repository.FormRepository;
import Capston.CosmeticTogether.domain.form.repository.OrderRepository;
import Capston.CosmeticTogether.domain.form.repository.ProductRepository;
import Capston.CosmeticTogether.domain.member.domain.Member;
import Capston.CosmeticTogether.domain.member.service.MemberService;
import Capston.CosmeticTogether.global.auth.dto.security.SecurityMemberDTO;
import Capston.CosmeticTogether.global.enums.ErrorCode;
import Capston.CosmeticTogether.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final MemberService memberService;
    private final FormRepository formRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    //TODO max 넘으면 안됨
    @Transactional
    public void createOrder(Long formId, OrderRequestDTO orderRequestDTO) {
        // 1. 사용자 정보 가져오기
        Member loginMember = memberService.getMemberFromSecurityDTO((SecurityMemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // 2. formId 유효성 체크
        Form form = formRepository.findById(formId).orElseThrow(() -> new BusinessException("존재하는 폼이 아닙니다", ErrorCode.FORM_NOT_FOUND));

        // 3. 폼 유효성 체크(시작날짜, 종료 날짜)
        if (form.getStartDate().isAfter(LocalDateTime.now()) || form.getEndDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException("판매기간이 지난 폼입니다", ErrorCode.FORM_NOT_ACTIVE);
        }

        //4. 주문 생성
        Order order = Order.builder()
                .buyer(loginMember)
                .recipientName(orderRequestDTO.getRecipientName())
                .recipientPhone(orderRequestDTO.getRecipientPhone())
                .recipientAddress(orderRequestDTO.getRecipientAddress())
                .totalPrice(orderRequestDTO.getTotalPrice())
                .build();

        // 5. 제품 리스트와 수량 처리
        List<OrderProduct> orderProductList = new ArrayList<>();
        for (int i = 0; i < orderRequestDTO.getProductsId().size(); i++) {
            // 제품 ID와 수량 가져오기
            Long productId = orderRequestDTO.getProductsId().get(i);
            Integer quantity = orderRequestDTO.getOrderQuantity().get(i);

            // 제품 조회
            Product product = productRepository.findById(productId)
                    .orElseThrow(() -> new BusinessException("해당 제품을 찾을 수 없습니다", ErrorCode.PRODUCT_NOT_FOUND));

            // 재고 확인 및 감소 처리
            if (product.getStock() < quantity) {
                throw new BusinessException(product.getProductName() + " 제품의 재고가 부족합니다", ErrorCode.PRODUCT_OUT_OF_STOCK);
            }
            product.decreaseStock(quantity);
            productRepository.save(product);

            // OrderProduct 생성
            OrderProduct orderProduct = OrderProduct.builder()
                    .order(order)
                    .product(product)
                    .quantity(quantity)
                    .build();

            orderProductList.add(orderProduct);
        }
        order.saveProducts(orderProductList);
        orderRepository.save(order);
    }

    @Transactional
    public void deleteOrder(Long orderId) {
        // 1. 유효한 orderId인지 확인
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException("존재하는 주문이 아닙니다", ErrorCode.ORDER_NOT_FOUND));

        // 2. 로그인 사용자와 주문자 정보 비교
        Member loginMember = memberService.getMemberFromSecurityDTO((SecurityMemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // 3. 논리적 삭제 진행(재고 변환)
        if(loginMember.equals(order.getBuyer())) {

            order.setDeletedAt(LocalDateTime.now());
            orderRepository.save(order);
        } else {
            throw new BusinessException("해당 주문의 구매자가 아닙니다", ErrorCode.NOT_BUYER_OF_ORDER);
        }
    }
}
