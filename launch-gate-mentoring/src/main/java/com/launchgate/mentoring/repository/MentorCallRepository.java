package com.launchgate.mentoring.repository;

import com.launchgate.mentoring.entity.*;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MentorCallRepository extends JpaRepository<MentorCall, Long> {
    List<MentorCall> findAllByTeam_IdOrderByStartsAtAsc(Long teamId);

    List<MentorCall> findAllByMentor_IdOrderByStartsAtAsc(Long mentorId);
}
