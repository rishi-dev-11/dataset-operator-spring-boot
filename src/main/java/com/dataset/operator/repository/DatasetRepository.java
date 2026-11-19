package com.dataset.operator.repository;

import com.dataset.operator.entity.DatasetEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DatasetRepository extends JpaRepository<DatasetEntity, Long> {

    Optional<DatasetEntity> findByName(String name);

    boolean existsByName(String name);
}
