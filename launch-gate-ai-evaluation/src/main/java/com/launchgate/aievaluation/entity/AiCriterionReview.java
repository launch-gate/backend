package com.launchgate.aievaluation.entity;

import com.launchgate.contest.entity.FieldCriterion;
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
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "ai_evaluation", name = "criterion_ai_reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiCriterionReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_review_id", nullable = false)
    private AiFieldReview fieldReview;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_criterion_id", nullable = false)
    private FieldCriterion criterion;

    @Column(name = "criterion_order", nullable = false)
    private int order;

    @Column(name = "criterion_description", nullable = false, columnDefinition = "text")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private AiCriterionReviewStatus status;

    @Column(name = "score")
    private Integer score;

    @Column(name = "verdict", columnDefinition = "text")
    private String verdict;

    @Column(name = "answer", columnDefinition = "text")
    private String answer;

    @Column(name = "evidence_json", columnDefinition = "text")
    private String evidenceJson;

    @Column(name = "confidence", precision = 8, scale = 4)
    private BigDecimal confidence;

    public AiCriterionReview(
            AiFieldReview fieldReview,
            FieldCriterion criterion,
            int order,
            String description,
            AiCriterionReviewStatus status,
            Integer score,
            String verdict,
            String answer,
            String evidenceJson,
            BigDecimal confidence
    ) {
        this.fieldReview = fieldReview;
        this.criterion = criterion;
        this.order = order;
        this.description = description;
        this.status = status;
        this.score = score;
        this.verdict = verdict;
        this.answer = answer;
        this.evidenceJson = evidenceJson;
        this.confidence = confidence;
    }

    public Long getCriterionId() {
        return criterion.getId();
    }
}
