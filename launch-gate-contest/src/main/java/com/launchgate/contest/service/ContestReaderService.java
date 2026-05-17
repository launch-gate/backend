package com.launchgate.contest.service;

import com.launchgate.contest.dto.ContestMetrics;
import com.launchgate.contest.entity.Contest;
import com.launchgate.contest.entity.ContestResource;
import com.launchgate.contest.entity.SubmissionField;
import com.launchgate.contest.entity.stage.ContestStage;
import com.launchgate.contest.repository.ContestRegistrationRepository;
import com.launchgate.contest.repository.ContestRepository;
import com.launchgate.contest.repository.ContestResourceRepository;
import com.launchgate.contest.repository.ContestStageRepository;
import com.launchgate.contest.repository.SubmissionFieldRepository;
import com.launchgate.contest.repository.TeamRepository;
import lombok.RequiredArgsConstructor;

import com.launchgate.common.NotFoundException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Вспомогательный сервис для работы с конкурсом.
 */
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class ContestReaderService {
    private final ContestRepository contestRepository;
    private final ContestStageRepository stageRepository;
    private final SubmissionFieldRepository fieldRepository;
    private final ContestResourceRepository resourceRepository;
    private final ContestRegistrationRepository registrationRepository;
    private final TeamRepository teamRepository;

    /**
     * Получить конкурс по идентификатору.
     * @param contestId идентификатор конкурса.
     * @return конкурс.
     */
    public Contest getContestById(Long contestId) {
        return contestRepository.findById(contestId)
                .orElseThrow(() -> new NotFoundException("Конкурс не найден"));
    }

    /**
     * Получить стадию конкурса по идентификатору.
     * @param stageId идентификатор стадии
     * @return стадия конкурса
     */
    public ContestStage getStageById(Long stageId) {
        return stageRepository.findById(stageId)
                .orElseThrow(() -> new NotFoundException("Стадия не найдена"));
    }

    /**
     * Получить все стадии конкурса.
     * @param contestId идентификатор конкурса.
     * @return список стадий конкурса.
     */
    public List<ContestStage> stages(Long contestId) {
        return stageRepository.findAllByContestIdOrderByOrderAsc(contestId);
    }

    /**
     * Получить все поля формы стадии.
     * @param stageId идентификатор стадии.
     * @return список полей формы стадии.
     */
    public List<SubmissionField> getSubmissionFields(Long stageId) {
        return fieldRepository.findAllByStageIdOrderByOrderAsc(stageId);
    }

    /**
     * Получить ресурс конкурса по идентификатору стадии.
     * @param stageId идентификатор стадии.
     * @return список ресурсов
     */
    public List<ContestResource> getStageResources(Long stageId) {
        return resourceRepository.findAllByStageIdOrderByOrderAsc(stageId);
    }

    /**
     * Получить метрки по конкурсу.
     * @param contestId идентификатор конкурса.
     * @return метрики.
     */
    public ContestMetrics metrics(Long contestId) {
        return new ContestMetrics(
                registrationRepository.countByContestId(contestId),
                teamRepository.countByContestId(contestId),
                stageRepository.countByContestId(contestId)
        );
    }
}
