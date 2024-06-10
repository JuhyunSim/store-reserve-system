package com.zerobase.domain.repository;

import com.zerobase.domain.entity.ReserveEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ReserveRepository extends JpaRepository<ReserveEntity, Long> {
    List<ReserveEntity> findAllByCustomerId(Long customerId);

    List<ReserveEntity> findAllByPartnerId(Long partnerId);

    List<ReserveEntity> findAllByStoreId(Long storeId);
}
