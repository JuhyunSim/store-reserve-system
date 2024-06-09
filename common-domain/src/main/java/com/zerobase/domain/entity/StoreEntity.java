package com.zerobase.domain.entity;

import com.zerobase.domain.requestForm.StoreForm;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.Audited;

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

    @Column(name = "partner_id")
    private Long partnerId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "latitude", nullable = false)
    private Double latitude;

    @Column(name = "longitude", nullable = false)
    private Double longitude;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    public static StoreEntity of(Long partnerId, StoreForm storeForm) {
        return StoreEntity.builder()
                .partnerId(partnerId)
                .name(storeForm.getStoreName())
                .latitude(storeForm.getLatitude())
                .longitude(storeForm.getLongitude())
                .description(storeForm.getDescription())
                .build();
    }
}
