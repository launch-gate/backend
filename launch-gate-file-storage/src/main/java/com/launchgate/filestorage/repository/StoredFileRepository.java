package com.launchgate.filestorage.repository;

import com.launchgate.filestorage.entity.*;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StoredFileRepository extends JpaRepository<StoredFile, Long> {
}
