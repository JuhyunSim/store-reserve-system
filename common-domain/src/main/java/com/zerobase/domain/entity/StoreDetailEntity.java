package com.zerobase.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;

@Entity(name = "store_detail")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StoreDetailEntity extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String tel;
    private String address;
    private String description;
    private LocalDateTime openTime;
    private LocalDateTime closeTime;
}
