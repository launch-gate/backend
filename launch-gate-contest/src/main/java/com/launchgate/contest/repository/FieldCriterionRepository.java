package com.launchgate.contest.repository;

import com.launchgate.contest.entity.FieldCriterion;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FieldCriterionRepository extends JpaRepository<FieldCriterion, Long> {
    List<FieldCriterion> findAllByField_IdOrderByOrderAsc(Long fieldId);
}
