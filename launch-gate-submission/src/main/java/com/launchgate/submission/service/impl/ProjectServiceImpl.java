package com.launchgate.submission.service.impl;

import com.launchgate.common.ForbiddenException;
import com.launchgate.common.LaunchGateException;
import com.launchgate.common.NotFoundException;
import com.launchgate.contest.entity.Contest;
import com.launchgate.contest.entity.ContestStatus;
import com.launchgate.contest.entity.ParticipationMode;
import com.launchgate.contest.entity.team.Team;
import com.launchgate.contest.service.ContestReaderService;
import com.launchgate.contest.service.TeamService;
import com.launchgate.identity.dto.AuthenticatedUser;
import com.launchgate.identity.entity.UserAccount;
import com.launchgate.identity.service.UserService;
import com.launchgate.submission.dto.MyProjectsResponse;
import com.launchgate.submission.dto.ProjectRequest;
import com.launchgate.submission.dto.ProjectResponse;
import com.launchgate.submission.entity.Project;
import com.launchgate.submission.repository.ProjectRepository;
import com.launchgate.submission.service.ProjectResponseAssembler;
import com.launchgate.submission.service.ProjectService;
import com.launchgate.submission.service.ProjectValidationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Clock;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

/**
 * Реализация сервиса для управления рабочими пространствами проектов участников и команд.
 */
@Service
@RequiredArgsConstructor
public class ProjectServiceImpl implements ProjectService {

    private final ProjectRepository projectRepository;
    private final ProjectResponseAssembler projectResponseAssembler;
    private final ContestReaderService contestReaderService;
    private final TeamService teamService;
    private final UserService userService;
    private final ProjectValidationService projectValidationService;
    private final Clock clock;

    @Override
    @Transactional
    public ProjectResponse createProject(AuthenticatedUser user, ProjectRequest request) {
        Contest contest = contestReaderService.getContestById(request.contestId());

        if (ParticipationMode.TEAM.equals(contest.getParticipationMode())) {

            if (Objects.isNull(request.teamId())) {
                throw new LaunchGateException("Командный проект конкурса требует идентификатор команды (teamId)");
            }

            if (!teamService.isMember(request.teamId(), user.id())) {
                throw new ForbiddenException("Только участник команды может создать командный проект");
            }

            Team team = teamService.getTeam(request.teamId());
            Project project = projectRepository.findByContest_IdAndTeam_Id(request.contestId(), request.teamId())
                    .orElseGet(() -> projectRepository.save(new Project(
                            contest,
                            team,
                            null,
                            Instant.now(clock)
                    )));

            return projectResponseAssembler.createProjectResponse(project);
        }

        UserAccount owner = userService.getUserById(user.id());
        Project project = projectRepository.findByContest_IdAndOwnerParticipant_Id(request.contestId(), user.id())
                .orElseGet(() -> projectRepository.save(new Project(
                        contest,
                        null,
                        owner,
                        Instant.now(clock)
                )));
        return projectResponseAssembler.createProjectResponse(project);
    }

    @Override
    @Transactional(readOnly = true)
    public ProjectResponse getProject(AuthenticatedUser user, Long projectId) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new NotFoundException("Проект не найден"));

        if (!projectValidationService.canReadProject(user, project)) {
            throw new ForbiddenException("Нет доступа к проекту");
        }

        return projectResponseAssembler.createProjectResponse(project);
    }

    @Override
    @Transactional(readOnly = true)
    public MyProjectsResponse myProjects(AuthenticatedUser user) {
        List<Project> individualProjects = projectRepository.findAllByOwnerParticipant_IdOrderByCreatedAtDesc(user.id());

        List<Long> teamIds = teamService.getTeamIdsByParticipant(user.id());

        List<Project> teamProjects = teamIds.isEmpty()
                ? List.<Project>of()
                : projectRepository.findAllByTeam_IdInOrderByCreatedAtDesc(teamIds);

        List<Project> projects = java.util.stream.Stream.concat(individualProjects.stream(), teamProjects.stream())
                .sorted(java.util.Comparator.comparing(Project::getCreatedAt).reversed())
                .toList();

        List<ProjectResponse> activeProjects = projects.stream()
                .filter(project -> project.getContest().getStatus() != ContestStatus.FINISHED)
                .map(projectResponseAssembler::createProjectResponse)
                .toList();

        List<ProjectResponse> archivedProjects = projects.stream()
                .filter(project -> project.getContest().getStatus() == ContestStatus.FINISHED)
                .map(projectResponseAssembler::createProjectResponse)
                .toList();

        return new MyProjectsResponse(activeProjects, archivedProjects);
    }
}
