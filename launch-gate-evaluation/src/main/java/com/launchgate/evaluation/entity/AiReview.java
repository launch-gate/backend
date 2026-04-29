package com.launchgate.evaluation.entity;

import com.launchgate.submission.entity.StageSubmission;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(schema = "evaluation", name = "ai_reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "submission_id", nullable = false)
    private Long submissionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", insertable = false, updatable = false)
    private StageSubmission submission;

    @Column(name = "summary", columnDefinition = "text")
    private String summary;

    @Column(name = "issues", columnDefinition = "text")
    private String issues;

    @Column(name = "suggested_score", precision = 8, scale = 2)
    private BigDecimal suggestedScore;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public AiReview(Long submissionId, String summary, String issues, BigDecimal suggestedScore, Instant createdAt) {
        this.submissionId = submissionId;
        this.summary = summary;
        this.issues = issues;
        this.suggestedScore = suggestedScore;
        this.createdAt = createdAt;
    }

}
