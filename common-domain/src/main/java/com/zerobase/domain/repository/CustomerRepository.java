package com.zerobase.domain.repository;

import com.zerobase.domain.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    Optional<CustomerEntity> findByEmail(String email);

    Optional<CustomerEntity> findByIdAndEmail(Long partnerId, String email);

    boolean existsById(Long customerId);

    Integer deleteByIdAndEmail(Long id, String email);
}
