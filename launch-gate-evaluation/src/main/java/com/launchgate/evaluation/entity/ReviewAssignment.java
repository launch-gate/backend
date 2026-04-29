package com.launchgate.evaluation.entity;

import com.launchgate.identity.entity.UserAccount;
import com.launchgate.submission.entity.StageSubmission;
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
import jakarta.persistence.Table;
import java.math.BigDecimal;
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "evaluation", name = "review_assignments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ReviewAssignment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "stage_id", nullable = false)
    private Long stageId;

    @Column(name = "submission_id", nullable = false)
    private Long submissionId;

    @Column(name = "expert_id", nullable = false)
    private Long expertId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ReviewStatus status = ReviewStatus.NEW;

    @Column(name = "assigned_at", nullable = false)
    private Instant assignedAt;

    @Column(name = "score", precision = 8, scale = 2)
    private BigDecimal score;

    @Column(name = "comment", columnDefinition = "text")
    private String comment;

    @Column(name = "finalized_at")
    private Instant finalizedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", insertable = false, updatable = false)
    private StageSubmission submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expert_id", insertable = false, updatable = false)
    private UserAccount expert;

    public ReviewAssignment(Long stageId, Long submissionId, Long expertId, Instant assignedAt) {
        this.stageId = stageId;
        this.submissionId = submissionId;
        this.expertId = expertId;
        this.assignedAt = assignedAt;
    }

    public void markDraft() {
        if (status == ReviewStatus.NEW) {
            status = ReviewStatus.DRAFT;
        }
    }

    public void complete() {
        status = ReviewStatus.COMPLETED;
    }

    public void update(BigDecimal score, String comment) {
        this.score = score;
        this.comment = comment;
    }

    public void finalizeAt(Instant now) {
        finalizedAt = now;
    }
}
