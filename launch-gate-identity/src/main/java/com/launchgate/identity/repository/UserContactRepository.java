package com.launchgate.identity.repository;

import com.launchgate.identity.entity.*;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserContactRepository extends JpaRepository<UserContact, Long> {
    List<UserContact> findAllByUserId(Long userId);

    void deleteAllByUserId(Long userId);
}
