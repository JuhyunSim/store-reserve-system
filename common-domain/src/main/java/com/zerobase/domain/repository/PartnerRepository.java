package com.zerobase.domain.repository;

import com.zerobase.domain.entity.PartnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartnerRepository extends JpaRepository<PartnerEntity, Long> {
    Optional<PartnerEntity> findByEmail(String email);

    Optional<PartnerEntity> findByIdAndEmail(Long id, String email);

    void deleteByIdAndEmail(Long id, String email);
}
