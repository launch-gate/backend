package com.launchgate.mentoring.repository;

import com.launchgate.mentoring.entity.*;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorAssignmentRepository extends JpaRepository<MentorAssignment, Long> {
    List<MentorAssignment> findAllByMentorId(Long mentorId);

    Optional<MentorAssignment> findByTeamIdAndMentorId(Long teamId, Long mentorId);

    boolean existsByTeamIdAndMentorId(Long teamId, Long mentorId);
}
