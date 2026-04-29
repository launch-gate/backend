package com.launchgate.export.repository;

import com.launchgate.export.entity.*;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ExportJobRepository extends JpaRepository<ExportJob, Long> {
}
