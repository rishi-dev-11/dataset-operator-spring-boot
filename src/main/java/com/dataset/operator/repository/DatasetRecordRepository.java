package com.dataset.operator.repository;

import com.dataset.operator.entity.DatasetRecordEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DatasetRecordRepository extends JpaRepository<DatasetRecordEntity, Long> {

    List<DatasetRecordEntity> findByDatasetIdOrderByIdAsc(Long datasetId);
}
