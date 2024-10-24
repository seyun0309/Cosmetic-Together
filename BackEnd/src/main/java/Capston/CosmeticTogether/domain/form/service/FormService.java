package Capston.CosmeticTogether.domain.form.service;


import Capston.CosmeticTogether.domain.board.service.S3ImageService;
import Capston.CosmeticTogether.domain.favorites.repository.FavoritesRepository;
import Capston.CosmeticTogether.domain.follow.domain.Follow;
import Capston.CosmeticTogether.domain.form.domain.Delivery;
import Capston.CosmeticTogether.domain.form.domain.Form;
import Capston.CosmeticTogether.domain.form.domain.Product;
import Capston.CosmeticTogether.domain.form.dto.request.CreateFormRequestDTO;
import Capston.CosmeticTogether.domain.form.dto.request.UpdateFormRequestDTO;
import Capston.CosmeticTogether.domain.form.dto.resonse.DetailFormResponseDTO;
import Capston.CosmeticTogether.domain.form.dto.resonse.FormResponseDTO;
import Capston.CosmeticTogether.domain.form.dto.resonse.UpdateFormInfoResponseDTO;
import Capston.CosmeticTogether.domain.form.repository.DeliveryRepository;
import Capston.CosmeticTogether.domain.form.repository.FormRepository;
import Capston.CosmeticTogether.domain.form.repository.OrderRepository;
import Capston.CosmeticTogether.domain.form.repository.ProductRepository;
import Capston.CosmeticTogether.domain.member.domain.Member;
import Capston.CosmeticTogether.domain.member.service.MemberService;
import Capston.CosmeticTogether.global.auth.dto.security.SecurityMemberDTO;
import Capston.CosmeticTogether.global.enums.ErrorCode;
import Capston.CosmeticTogether.global.enums.FormStatus;
import Capston.CosmeticTogether.global.enums.ProductStatus;
import Capston.CosmeticTogether.global.enums.Role;
import Capston.CosmeticTogether.global.error.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FormService {
    private final FormRepository formRepository;
    private final ProductRepository productRepository;
    private final MemberService memberService;
    private final FavoritesRepository favoritesRepository;
    private final OrderRepository orderRepository;
    private final DeliveryRepository deliveryRepository;
    private final S3ImageService s3ImageService;

    @Transactional
    public void createForm(MultipartFile thumbnail, CreateFormRequestDTO createFormRequestDTO, List<MultipartFile> images) {

        // 1. 사용자 정보 가져오기
        Member loginMember = memberService.getMemberFromSecurityDTO((SecurityMemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // 2. 일반 사용자만 폼 등록 가능 / 카카오인 경우엔 추가 정보 기입 필요
        if(loginMember.getRole() == Role.GUEST) {
            throw new BusinessException(ErrorCode.USER_INFO_NOT_COMPLETED);
        }

        // 3. 폼 저장
        String thumbnailURL = s3ImageService.upload(thumbnail);


        Form form = Form.builder()
                .organizer(loginMember)
                .title(createFormRequestDTO.getTitle())
                .form_description(createFormRequestDTO.getForm_description())
                .formUrl(thumbnailURL)
                .formStatus(FormStatus.ACTIVE)
                .startDate(createFormRequestDTO.getStartDate())
                .endDate(createFormRequestDTO.getEndDate())
                .build();
        formRepository.save(form);

        List<Delivery> deliveryList = new ArrayList<>();
        for(int i=0; i<createFormRequestDTO.getDeliveryOption().size(); i++) {
            Delivery delivery = Delivery.builder()
                    .deliveryOption(createFormRequestDTO.getDeliveryOption().get(i))
                    .deliveryCost(Integer.parseInt(createFormRequestDTO.getDeliveryCost().get(i)))
                    .form(form)
                    .build();

            deliveryList.add(delivery);
        }

        deliveryRepository.saveAll(deliveryList);

        // 4. 제품 저장
        List<String> productNames = createFormRequestDTO.getProductName();
        List<Integer> prices = createFormRequestDTO.getPrice();
        List<Integer> stocks = createFormRequestDTO.getStock();
        List<Integer> maxPurchaseLimit = createFormRequestDTO.getMaxPurchaseLimit();


        for (int i = 0; i < productNames.size(); i++) {

            List<String> imageUrls = new ArrayList<>();

            if (!images.isEmpty()) {
                for (MultipartFile image : images) {
                    if (!image.isEmpty()) {
                        String imageUrl = s3ImageService.upload(image);
                        imageUrls.add(imageUrl);
                    }
                }
            }

            Product product = Product.builder()
                    .productName(productNames.get(i))
                    .price(prices.get(i))
                    .stock(stocks.get(i))
                    .product_url(imageUrls.get(i))
                    .maxPurchaseLimit(maxPurchaseLimit.get(i))
                    .productStatus(ProductStatus.INSTOCK)
                    .form(form)
                    .build();

            productRepository.save(product);
        }
    }

    public DetailFormResponseDTO getForm(Long formId) {
        // 1. formId 유효성 체크
        Form form = formRepository.findDeleteAtIsNullById(formId).orElseThrow(() -> new BusinessException(ErrorCode.FORM_NOT_FOUND));

        // 2. 매핑해서 리턴
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 3. 판매기간 포맷팅
        String formattedStartDate = form.getStartDate().format(formatter);
        String formattedEndDate = form.getEndDate().format(formatter);
        String salesPeriod = formattedStartDate + " ~ " + formattedEndDate;

        // 4. 제품 상태 확인 및 업데이트 로직
        boolean allOutOfStock = true;

        for (Product product : form.getProduct()) {
            // 4-1. 해당 제품의 주문 수량 계산
            Integer totalOrderedQuantity = orderRepository.sumQuantityByProductId(product.getId());

            if (totalOrderedQuantity == null) {
                totalOrderedQuantity = 0;
            }

            // 4-2. 주문 수량과 재고 비교
            if (totalOrderedQuantity >= product.getStock()) {
                // 4-3. 재고가 소진되었을 경우, productStatus를 OUTSTOCK으로 변경
                product.setProductStatus(ProductStatus.OUTSTOCK);
            } else {
                // 4-4. 재고가 남아있는 경우, 전체 품절 여부를 false로 설정
                allOutOfStock = false;
            }
        }

        // 5. 모든 제품 품절 및 판매기간 지난 경우 formStatus 마감으로 설정
        if (allOutOfStock || (form.getStartDate().isAfter(LocalDateTime.now()) || form.getEndDate().isBefore(LocalDateTime.now()))) {
            form.setFormStatus(FormStatus.CLOSED);
        }

        // 6. 찜 개수 카운트
        Long favoriteCount = favoritesRepository.countFavoritesByFormId(form.getId());

        // 7. 매핑해서 리턴
        return DetailFormResponseDTO.builder()
                .thumbnail(form.getFormUrl())
                .organizerName(form.getOrganizer().getNickname())
                .organizer_profileUrl(form.getOrganizer().getProfile_url())
                .title(form.getTitle())
                .form_description(form.getForm_description())
                .salesPeriod(salesPeriod)
                .favoriteCount(favoriteCount)
                .productName(form.getProduct().stream().map(Product::getProductName).collect(Collectors.toList()))
                .price(form.getProduct().stream().map(product -> product.getPrice() + "원").collect(Collectors.toList()))
                .product_url(form.getProduct().stream().map(Product::getProduct_url).collect(Collectors.toList()))
                .maxPurchaseLimit(form.getProduct().stream().map(product -> product.getMaxPurchaseLimit() + "개").collect(Collectors.toList()))
                .stock(form.getProduct().stream().map(product -> product.getStock() + "개").collect(Collectors.toList()))
                .productStatuses(form.getProduct().stream().map(product -> product.getProductStatus().getDescription()).collect(Collectors.toList()))
                .deliveryOption(form.getDeliveries().stream().map(Delivery::getDeliveryOption).collect(Collectors.toList()))
                .deliveryCost(form.getDeliveries().stream().map(delivery -> delivery.getDeliveryCost() + "원").collect(Collectors.toList()))
                .build();
    }

    public List<FormResponseDTO> getRecentForms() {
        // 1. 모든 폼 가져오기
        List<Form> all = formRepository.findDeleteAtIsNullAll();
        List<FormResponseDTO> response = new ArrayList<>();

        // 2. 매핑해서 리턴
        for(Form form : all) {

            // 3. 제품 상태 확인 및 업데이트 로직
            boolean allOutOfStock = true;

            for (Product product : form.getProduct()) {
                // 3-1. 해당 제품의 주문 수량 계산
                Integer totalOrderedQuantity = orderRepository.sumQuantityByProductId(product.getId());

                if(totalOrderedQuantity == null) {
                    totalOrderedQuantity = 0;
                }

                // 3-2. 주문 수량과 재고 비교
                if (totalOrderedQuantity >= product.getStock()) {
                    // 3-3. 재고가 소진되었을 경우, productStatus를 OUTSTOCK으로 변경
                    product.setProductStatus(ProductStatus.OUTSTOCK);
                } else {
                    // 3-4. 재고가 남아있는 경우, 전체 품절 여부를 false로 설정
                    allOutOfStock = false;
                }
            }

            // 4. 모든 제품 품절 및 판매기간 지난 경우 formStatus 마감으로 설정
            if (allOutOfStock || (form.getStartDate().isAfter(LocalDateTime.now()) || form.getEndDate().isBefore(LocalDateTime.now()))) {
                form.setFormStatus(FormStatus.CLOSED);
            }

            // 5. 매핑
            FormResponseDTO formResponseDTO = FormResponseDTO.builder()
                    .title(form.getTitle())
                    .thumbnail(form.getFormUrl())
                    .organizerName(form.getOrganizer().getNickname())
                    .organizer_url(form.getOrganizer().getProfile_url())
                    .formStatus(form.getFormStatus().getDescription())
                    .build();
        response.add(formResponseDTO);
        }
        return response;
    }

    public List<FormResponseDTO> getFollowingForms() {
        // 1. 사용자 정보 가져오기
        Member loginMember = memberService.getMemberFromSecurityDTO((SecurityMemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // 2. 사용자의 팔로잉 목록 가져오기
        List<Member> followingMembers = new ArrayList<>();
        for (Follow follow : loginMember.getFollowingList()) {
            followingMembers.add(follow.getFollower()); // Follow 엔티티에서 following 필드를 가져옴
        }

        // 2. 팔로잉 폼 가져오기(최신순으로)
        List<Form> followingForms = formRepository.findByFollowingMembers(followingMembers);

        // 3. 매핑해서 리턴
        List<FormResponseDTO> response = new ArrayList<>();

        for(Form form : followingForms) {

            // 4. 제품 상태 확인 및 업데이트 로직
            boolean allOutOfStock = true;

            for (Product product : form.getProduct()) {
                // 4-1. 해당 제품의 주문 수량 계산
                Integer totalOrderedQuantity = orderRepository.sumQuantityByProductId(product.getId());

                if(totalOrderedQuantity == null) {
                    totalOrderedQuantity = 0;
                }

                // 4-2. 주문 수량과 재고 비교
                if (totalOrderedQuantity >= product.getStock()) {
                    // 4-3. 재고가 소진되었을 경우, productStatus를 OUTSTOCK으로 변경
                    product.setProductStatus(ProductStatus.OUTSTOCK);
                } else {
                    // 4-4. 재고가 남아있는 경우, 전체 품절 여부를 false로 설정
                    allOutOfStock = false;
                }
            }

            // 5. 모든 제품 품절 및 판매기간 지난 경우 formStatus 마감으로 설정
            if (allOutOfStock || (form.getStartDate().isAfter(LocalDateTime.now()) || form.getEndDate().isBefore(LocalDateTime.now()))) {
                form.setFormStatus(FormStatus.CLOSED);
            }

            FormResponseDTO formResponseDTO = FormResponseDTO.builder()
                    .title(form.getTitle())
                    .thumbnail(form.getFormUrl())
                    .organizerName(form.getOrganizer().getNickname())
                    .organizer_url(form.getOrganizer().getProfile_url())
                    .formStatus(form.getFormStatus().getDescription())
                    .build();
            response.add(formResponseDTO);
        }
        return response;
    }

    public List<FormResponseDTO> searchFormByKeyword(String keyword) {
        // 1. 키워드가 포함된 폼 불러오기
        List<Form> formList = formRepository.findByKeywordInTitleOrDescriptionOrProductName(keyword);
        List<FormResponseDTO> response = new ArrayList<>();

        // 2. 매핑해서 리턴
        for(Form form : formList) {

            // 3. 제품 상태 확인 및 업데이트 로직
            boolean allOutOfStock = true;

            for (Product product : form.getProduct()) {
                // 3-1. 해당 제품의 주문 수량 계산
                Integer totalOrderedQuantity = orderRepository.sumQuantityByProductId(product.getId());

                if(totalOrderedQuantity == null) {
                    totalOrderedQuantity = 0;
                }

                // 3-2. 주문 수량과 재고 비교
                if (totalOrderedQuantity >= product.getStock()) {
                    // 3-3. 재고가 소진되었을 경우, productStatus를 OUTSTOCK으로 변경
                    product.setProductStatus(ProductStatus.OUTSTOCK);
                } else {
                    // 3-4. 재고가 남아있는 경우, 전체 품절 여부를 false로 설정
                    allOutOfStock = false;
                }
            }

            // 4. 모든 제품 품절 및 판매기간 지난 경우 formStatus 마감으로 설정
            if (allOutOfStock || (form.getStartDate().isAfter(LocalDateTime.now()) || form.getEndDate().isBefore(LocalDateTime.now()))) {
                form.setFormStatus(FormStatus.CLOSED);
            }

            // 5. 매핑
            FormResponseDTO formResponseDTO = FormResponseDTO.builder()
                    .title(form.getTitle())
                    .thumbnail(form.getFormUrl())
                    .organizerName(form.getOrganizer().getNickname())
                    .organizer_url(form.getOrganizer().getProfile_url())
                    .formStatus(form.getFormStatus().getDescription())
                    .build();
            response.add(formResponseDTO);
        }
        return response;
    }

    public UpdateFormInfoResponseDTO getFormInfo(Long formId) {
        // 1. 사용자 정보 가져오기
        Member loginMember = memberService.getMemberFromSecurityDTO((SecurityMemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // 2. formId 유효성 검사
        Form form = formRepository.findById(formId).orElseThrow(() -> new BusinessException("존재하지 않는 폼입니다", ErrorCode.FORM_NOT_FOUND));

        // 3. 로그인 사용자와 form 작성자 비교
        if(!(loginMember.equals(form.getOrganizer()))) {
            throw new BusinessException("해당 폼의 작성자가 아닙니다", ErrorCode.NOT_WRITER_OF_FORM);
        }

        // 4. 정보 매핑해서 리턴
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // 3. 판매기간 포맷팅
        String formattedStartDate = form.getStartDate().format(formatter);
        String formattedEndDate = form.getEndDate().format(formatter);

        return UpdateFormInfoResponseDTO.builder()
                .title(form.getTitle())
                .formUrl(form.getFormUrl())
                .startDate(formattedStartDate)
                .endDate(formattedEndDate)
                .build();
    }

    @Transactional
    public void updateForm(Long formId, String thumbnail, UpdateFormRequestDTO updateFormRequestDTO) {
        // 1. 사용자 정보 가져오기
        Member loginMember = memberService.getMemberFromSecurityDTO((SecurityMemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // 2. formId 유효성 검사
        Form form = formRepository.findById(formId).orElseThrow(() -> new BusinessException("존재하지 않는 폼입니다", ErrorCode.FORM_NOT_FOUND));

        // 3. 로그인 사용자와 form 작성자 비교
        if(!(loginMember.equals(form.getOrganizer()))) {
            throw new BusinessException("해당 폼의 작성자가 아닙니다", ErrorCode.NOT_WRITER_OF_FORM);
        }

        // 4. form 유효성 체크(주문이 있을 경우 폼 수정 금지)
        boolean hasOrders = orderRepository.existsByProduct_FormId(formId);

        if (hasOrders) {
            throw new BusinessException("이미 주문이 접수된 폼은 수정할 수 없습니다", ErrorCode.ORDER_EXISTS);
        }

        // 5. 폼 수정
        form.update(
                updateFormRequestDTO.getTitle(),
                updateFormRequestDTO.getForm_description(),
                thumbnail != null ? thumbnail : form.getFormUrl(),
                updateFormRequestDTO.getStartDate(),
                updateFormRequestDTO.getEndDate()
        );

        formRepository.save(form);
    }

    @Transactional
    public void deleteForm(Long formId) {
        // 1. 유효한 formId인지 확인
        Form form = formRepository.findById(formId).orElseThrow(() -> new BusinessException("존재하는 폼이 아닙니다", ErrorCode.FORM_NOT_FOUND));

        // 2. 로그인한 사용자랑 폼 작성자가 같은지 확인
        Member loginMember = memberService.getMemberFromSecurityDTO((SecurityMemberDTO) SecurityContextHolder.getContext().getAuthentication().getPrincipal());

        // 3. 논리적 삭제 진행
        if(loginMember.equals(form.getOrganizer())) {
            form.setDeletedAt(LocalDateTime.now());
            formRepository.save(form);
        } else {
            throw new BusinessException("해당 폼의 작성자가 아닙니다", ErrorCode.NOT_WRITER_OF_FORM);
        }
    }
}
