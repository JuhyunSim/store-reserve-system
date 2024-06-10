package com.zerobase.domain.entity;

import com.zerobase.domain.requestForm.store.StoreForm;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

import java.time.LocalDateTime;

@Entity(name = "store")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Audited
@AuditOverride(forClass = BaseEntity.class)
@Table(uniqueConstraints =
        { @UniqueConstraint(columnNames = { "partnerId", "name" }) })
public class StoreEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long partnerId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    private boolean reservePossible;

    @OneToOne(mappedBy = "store", cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    private StoreDetailEntity storeDetail;

    public static StoreEntity of(Long partnerId, StoreForm storeForm) {
        return StoreEntity.builder()
                .partnerId(partnerId)
                .name(storeForm.getStoreName())
                .latitude(storeForm.getLatitude())
                .longitude(storeForm.getLongitude())
                .description(storeForm.getDescription())
                .reservePossible(false)
                .storeDetail(StoreDetailEntity.from(storeForm.getStoreDetailForm()))
                .build();
    }

    @Entity(name = "store_detail")
    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    @AuditOverride(forClass = BaseEntity.class)
    public static class StoreDetailEntity extends BaseEntity {
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        private String tel;
        private String address;
        private String description;
        private LocalDateTime openTime;
        private LocalDateTime closeTime;

        @OneToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "store_id", nullable = false)
        private StoreEntity store;

        public static StoreDetailEntity from(StoreForm.StoreDetailForm detailForm) {
            return StoreDetailEntity.builder()
                    .address(detailForm.getAddress())
                    .description(detailForm.getDescription())
                    .tel(detailForm.getTel())
                    .openTime(detailForm.getOpenTime())
                    .closeTime(detailForm.getCloseTime())
                    .build();
        }
    }
}
