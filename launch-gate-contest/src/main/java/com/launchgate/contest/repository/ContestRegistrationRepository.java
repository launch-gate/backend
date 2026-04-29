package com.launchgate.contest.repository;

import com.launchgate.contest.entity.*;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ContestRegistrationRepository extends JpaRepository<ContestRegistration, Long> {
    boolean existsByContest_IdAndParticipant_Id(Long contestId, Long participantId);

    Optional<ContestRegistration> findByContest_IdAndParticipant_Id(Long contestId, Long participantId);

    List<ContestRegistration> findAllByContest_IdOrderByRegisteredAtAsc(Long contestId);

    long countByContest_Id(Long contestId);

    default boolean existsByContestIdAndParticipantId(Long contestId, Long participantId) {
        return existsByContest_IdAndParticipant_Id(contestId, participantId);
    }

    default Optional<ContestRegistration> findByContestIdAndParticipantId(Long contestId, Long participantId) {
        return findByContest_IdAndParticipant_Id(contestId, participantId);
    }

    default List<ContestRegistration> findAllByContestIdOrderByRegisteredAtAsc(Long contestId) {
        return findAllByContest_IdOrderByRegisteredAtAsc(contestId);
    }

    default long countByContestId(Long contestId) {
        return countByContest_Id(contestId);
    }
}
