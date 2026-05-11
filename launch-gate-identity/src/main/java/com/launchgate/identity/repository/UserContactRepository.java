package com.launchgate.identity.repository;

import com.launchgate.identity.entity.*;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserContactRepository extends JpaRepository<UserContact, Long> {
    List<UserContact> findAllByUser_Id(Long userId);

    void deleteAllByUser_Id(Long userId);
}
