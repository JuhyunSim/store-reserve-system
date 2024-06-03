package com.zerobase.partner.domain.repository;

import com.zerobase.partner.domain.model.StoreEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StoreRepository extends JpaRepository<StoreEntity, Long> {
    List<StoreEntity> findAllByPartnerIdOrderByNameAsc(Long partnerId);

     boolean existsByPartnerIdAndName(Long partnerId, String name);

}
