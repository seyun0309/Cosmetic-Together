package Capston.CosmeticTogether.domain.form.service;

import Capston.CosmeticTogether.domain.form.domain.*;
import Capston.CosmeticTogether.domain.form.dto.request.OrderRequestDTO;
import Capston.CosmeticTogether.domain.form.dto.request.UpdateOrderStatusRequestDTO;
import Capston.CosmeticTogether.domain.form.dto.resonse.form.FormResponseDTO;
import Capston.CosmeticTogether.domain.form.dto.resonse.form.MyFormResponseDTO;
import Capston.CosmeticTogether.domain.form.dto.resonse.order.*;
import Capston.CosmeticTogether.domain.form.repository.*;
import Capston.CosmeticTogether.domain.member.domain.Member;
import Capston.CosmeticTogether.domain.member.service.MemberService;
import Capston.CosmeticTogether.global.auth.dto.security.SecurityMemberDTO;
import Capston.CosmeticTogether.global.enums.ErrorCode;
import Capston.CosmeticTogether.global.enums.OrderStatus;
import Capston.CosmeticTogether.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

@Service
@RequiredArgsConstructor
@Slf4j
public class OrderService {
    private final MemberService memberService;
    private final FormRepository formRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final DeliveryRepository deliveryRepository;
    private final OrderProductRepository orderProductRepository;

    @Transactional
    public CreateOrderResponseDTO createOrder(Long formId, OrderRequestDTO orderRequestDTO) {
        // 1. 사용자 정보 가져오기
        Member loginMember = memberService.getMemberFromSecurityDTO((SecurityMemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // 2. formId 유효성 체크
        Form form = formRepository.findById(formId).orElseThrow(() -> new BusinessException("존재하는 폼이 아닙니다", ErrorCode.FORM_NOT_FOUND));

        // 3. 폼 유효성 체크(시작날짜, 종료 날짜)
        if (form.getStartDate().isAfter(LocalDateTime.now()) || form.getEndDate().isBefore(LocalDateTime.now())) {
            throw new BusinessException("판매기간이 지난 폼입니다", ErrorCode.FORM_NOT_ACTIVE);
        }

        Delivery delivery = deliveryRepository.findById(orderRequestDTO.getDeliveryId()).orElseThrow(() -> new BusinessException("존재하는 배송 방법이 아닙니다", ErrorCode.NOT_FOUND_DELIVERY));

        //4. 주문 생성
        Order order = Order.builder()
                .buyer(loginMember)
                .recipientName(orderRequestDTO.getRecipientName())
                .recipientPhone(orderRequestDTO.getRecipientPhone())
                .recipientAddress(orderRequestDTO.getRecipientAddress())
                .totalPrice(orderRequestDTO.getTotalPrice())
                .delivery(delivery)
                .form(form)
                .orderStatus(OrderStatus.COMPLETED)
                .build();

        orderRepository.save(order);

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

            orderProductRepository.save(orderProduct);

            orderProductList.add(orderProduct);
        }
        order.saveProducts(orderProductList);

        return CreateOrderResponseDTO.builder()
                .orderId(order.getId())
                .build();
    }

    public AccountResponseDTO getAccount(Long orderId) {
        // 1. 유효한 orderId인지 확인
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException("존재하는 주문이 아닙니다", ErrorCode.ORDER_NOT_FOUND));

        // 매핑해서 리턴
        return AccountResponseDTO.builder()
                .organizerName(order.getForm().getOrganizer().getUserName())
                .bankName(order.getForm().getBankName())
                .accountNumber(order.getForm().getAccountNumber())
                .build();

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

    public List<OrderResponseDTO> getOrderForm() {
        // 1. 로그인 사용자 정보 가져오기
        Member loginMember = memberService.getMemberFromSecurityDTO((SecurityMemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // 2. 해당 사용자가 주문한 폼 리스트 조회
        List<Order> orderList = orderRepository.findByBuyer(loginMember);

        List<OrderResponseDTO> response = new ArrayList<>();

        // 원화 포맷 설정
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.KOREA);

        // 3. 매핑해서 리턴
        for(Order order : orderList) {
            String orderDate = order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

            // 가격에 쉼표 포맷 적용
            String formattedTotalPrice = currencyFormat.format(order.getTotalPrice());

            OrderResponseDTO dto = OrderResponseDTO.builder()
                    .formId(order.getForm().getId())
                    .orderId(order.getId())
                    .orderStatus(order.getOrderStatus().getDescription())
                    .orderDate(orderDate)
                    .thumbnail(order.getForm().getFormUrl())
                    .title(order.getForm().getTitle())
                    .totalPrice(formattedTotalPrice)
                    .build();
            response.add(dto);
        }
        return response;
    }

    public OrderDetailResponseDTO getOrder(Long orderId) {
        // 1. 로그인 사용자 정보 가져오기
        Member loginMember = memberService.getMemberFromSecurityDTO((SecurityMemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // 2. 사용자가 작성한 주문서 조회
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        // 3. 매핑해서 리턴
        String orderDate = order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd"));

        // 원화 포맷 설정
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.KOREA);

        // 개별 가격 계산 및 포맷 적용
        String productPrice = currencyFormat.format(order.getTotalPrice() - order.getDelivery().getDeliveryCost());
        String shippingFee = currencyFormat.format(order.getDelivery().getDeliveryCost());
        String totalPayment = currencyFormat.format(order.getTotalPrice());
        String deliveryCost = currencyFormat.format(order.getDelivery().getDeliveryCost());


        List<OrderProductsResponseDTO> orderProducts = order.getOrderProducts().stream()
                .map(orderProduct -> OrderProductsResponseDTO.builder()
                        .productId(orderProduct.getProduct().getId())
                        .productName(orderProduct.getProduct().getProductName())
                        .price(String.valueOf(orderProduct.getProduct().getPrice()))
                        .product_url(orderProduct.getProduct().getProductUrl()) // 이미지 URL 필드명에 맞게 수정
                        .quantity(String.valueOf(orderProduct.getQuantity()))
                        .build())
                .toList();

        return OrderDetailResponseDTO.builder()
                .orderDate(orderDate)
                .productPrice(productPrice)
                .shippingFee(shippingFee)
                .totalPayment(totalPayment)
                .recipientName(order.getRecipientName())
                .recipientPhone(order.getRecipientPhone())
                .recipientAddress(order.getRecipientAddress())
                .orderProducts(orderProducts)
                .deliveryOption(order.getDelivery().getDeliveryOption())
                .deliveryCost(deliveryCost)
                .orderStatus(order.getOrderStatus().getDescription())
                .organizerName(order.getForm().getOrganizer().getUserName())
                .bankName(order.getForm().getBankName())
                .accountNumber(order.getForm().getAccountNumber())
                .build();
    }

    public List<MyFormResponseDTO> getMyForm() {
        // 1. 로그인 사용자 정보 가져오기
        Member loginMember = memberService.getMemberFromSecurityDTO((SecurityMemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // 2. 해당 사용자가 작성한 폼 리스트 가져오기
        List<Form> formList = formRepository.findByMemberId(loginMember.getId());

        // 3. 매핑해서 리턴
        List<MyFormResponseDTO> response = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        for(Form form : formList) {
            String formattedStartDate = form.getStartDate().format(formatter);
            String formattedEndDate = form.getEndDate().format(formatter);
            String salesPeriod = formattedStartDate + " ~ " + formattedEndDate;

            MyFormResponseDTO myFormResponseDTO = MyFormResponseDTO.builder()
                    .formId(form.getId())
                    .thumbnail(form.getFormUrl())
                    .title(form.getTitle())
                    .salesPeriod(salesPeriod)
                    .build();

            response.add(myFormResponseDTO);
        }

        return response;
    }

    public SellerOrderResponseDTO getSalesOrders(Long formId) {
        // 1. formId를 통해 해당 form에 등록된 주문 리스트 가져오기
        Form form = formRepository.findById(formId).orElseThrow(() -> new BusinessException(ErrorCode.FORM_NOT_FOUND));
        List<Order> orderList = orderRepository.findAllByForm(form);

        // 2. 매핑해서 리턴
        int totalOrders = 0;
        int totalSales = 0;

        // 원화 포맷 설정
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(Locale.KOREA);

        List<SellerOrderListDTO> orders = new ArrayList<>();
        for(Order order : orderList) {
            String orderDateTime = order.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss"));
            totalOrders += 1;
            totalSales += order.getTotalPrice();

            // 가격에 "," 추가
            String formattedPrice = currencyFormat.format(order.getTotalPrice());

            SellerOrderListDTO dto = SellerOrderListDTO.builder()
                    .formId(order.getForm().getId())
                    .orderId(order.getId())
                    .orderDate(orderDateTime)
                    .buyerName(order.getBuyer().getUserName())
                    .totalPrice(formattedPrice)
                    .build();
            orders.add(dto);
        }

        return SellerOrderResponseDTO.builder()
                .totalOrders(currencyFormat.format(totalOrders))
                .totalSales(currencyFormat.format(totalSales))
                .orders(orders)
                .build();
    }

    public void updateOrderStatus(Long orderId, UpdateOrderStatusRequestDTO updateOrderStatusRequestDTO) {
        // 1. 유효한 orderId인지 확인
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        // 2. 주문 상태 변경
        // 2. description으로 OrderStatus 찾기
        String statusDescription = updateOrderStatusRequestDTO.getOrderStatus();
        OrderStatus newStatus = Arrays.stream(OrderStatus.values())
                .filter(status -> status.getDescription().equals(statusDescription))
                .findFirst()
                .orElseThrow(() -> new BusinessException("주문 상태를 변경해주세요", ErrorCode.INVALID_INPUT_VALUE));

        // 상태 변경
        order.updateOrderStatus(newStatus);
        orderRepository.save(order);
    }
}
