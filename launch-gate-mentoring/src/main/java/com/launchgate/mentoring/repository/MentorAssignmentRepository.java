package com.launchgate.mentoring.repository;

import com.launchgate.mentoring.entity.*;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorAssignmentRepository extends JpaRepository<MentorAssignment, Long> {
    List<MentorAssignment> findAllByMentor_Id(Long mentorId);

    Optional<MentorAssignment> findByTeam_IdAndMentor_Id(Long teamId, Long mentorId);

    boolean existsByTeam_IdAndMentor_Id(Long teamId, Long mentorId);
}
