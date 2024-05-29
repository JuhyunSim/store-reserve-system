package com.zerobase.partner.domain.repository;

import com.zerobase.partner.domain.model.PartnerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PartnerRepository extends JpaRepository<PartnerEntity, Long> {
    Optional<PartnerEntity> findByEmail(String email);
}
