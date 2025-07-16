package com.fitech.app.users.infrastructure.repository;

import com.fitech.app.users.domain.entities.MetricTypeUOM;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MetricTypeUOMRepository extends JpaRepository<MetricTypeUOM, Integer> {
    Page<MetricTypeUOM> findByMetricTypeId(Integer metricTypeId, Pageable pageable);
    boolean existsByMetricTypeIdAndUnitOfMeasureId(Integer metricTypeId, Integer unitOfMeasureId);
} 