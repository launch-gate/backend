package com.launchgate.submission.entity;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;



import com.launchgate.contest.entity.stage.ContestStage;
import com.launchgate.identity.entity.UserAccount;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(schema = "submission", name = "stage_submissions")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StageSubmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "project_id", nullable = false)
    private Long projectId;

    @Column(name = "stage_id", nullable = false)
    private Long stageId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private SubmissionStatus status = SubmissionStatus.DRAFT;

    @Column(name = "submitted_by")
    private Long submittedBy;

    @Column(name = "submitted_at")
    private Instant submittedAt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "project_id", insertable = false, updatable = false)
    private Project project;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stage_id", insertable = false, updatable = false)
    private ContestStage stage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submitted_by", insertable = false, updatable = false)
    private UserAccount submittedByUser;

    @OneToMany(mappedBy = "submission")
    private Set<SubmissionValue> values = new HashSet<>();

    public StageSubmission(Long projectId, Long stageId, Instant createdAt) {
        this.projectId = projectId;
        this.stageId = stageId;
        this.createdAt = createdAt;
    }

    public void submit(Long userId, Instant now) {
        status = SubmissionStatus.SUBMITTED;
        submittedBy = userId;
        submittedAt = now;
    }
}
