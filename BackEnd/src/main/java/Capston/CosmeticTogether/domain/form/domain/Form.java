package Capston.CosmeticTogether.domain.form.domain;

import Capston.CosmeticTogether.domain.favorites.domain.Favorites;
import Capston.CosmeticTogether.domain.member.domain.Member;
import Capston.CosmeticTogether.global.common.BaseEntity;
import Capston.CosmeticTogether.global.enums.FormStatus;
import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;
import java.util.List;

@SuperBuilder
@Getter
@Entity
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "form")
public class Form extends BaseEntity {

    @JoinColumn(nullable = false)
    @ManyToOne
    private Member organizer;

    @Column(length = 100, nullable = false)
    private String title;

    @Column
    private String formDescription;

    @Column
    private String formUrl;

    @OneToMany(mappedBy = "form")
    @OrderBy("id ASC")
    private List<Product> product;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormStatus formStatus;

    @Column
    private LocalDateTime startDate;

    @Column
    private LocalDateTime endDate;

    @OneToMany(mappedBy = "form")
    private List<Delivery> deliveries;

    @Column(nullable = false)
    private String deliveryInstructions;

    @OneToMany(mappedBy = "form")
    private List<Favorites> favorites;

    // 업데이트 메서드
    public void update(String title, String form_description, String formUrl, LocalDateTime startDate, LocalDateTime endDate) {
        if (title != null) {
            this.title = title;
        }
        if (form_description != null) {
            this.formDescription = form_description;
        }
        if (formUrl != null) {
            this.formUrl = formUrl;
        }
        if (startDate != null) {
            this.startDate = startDate;
        }
        if (endDate != null) {
            this.endDate = endDate;
        }
    }
}
