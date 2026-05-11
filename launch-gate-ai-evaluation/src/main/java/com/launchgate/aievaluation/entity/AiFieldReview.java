package com.launchgate.aievaluation.entity;

import com.launchgate.contest.entity.SubmissionField;
import com.launchgate.submission.entity.SubmissionValue;
import jakarta.persistence.CascadeType;
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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "ai_evaluation", name = "field_ai_reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiFieldReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    private AiSubmissionReview review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id", nullable = false)
    private SubmissionField field;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_value_id")
    private SubmissionValue submissionValue;

    @Column(name = "field_order", nullable = false)
    private int order;

    @Column(name = "field_title", nullable = false, length = 160)
    private String title;

    @Column(name = "field_type", nullable = false, length = 32)
    private String type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private AiFieldReviewStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "source_type", nullable = false, length = 32)
    private AiReviewSourceType sourceType;

    @Column(name = "message", columnDefinition = "text")
    private String message;

    @OneToMany(mappedBy = "fieldReview", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AiCriterionReview> criteria = new HashSet<>();

    public AiFieldReview(
            AiSubmissionReview review,
            SubmissionField field,
            SubmissionValue submissionValue,
            int order,
            String title,
            String type,
            AiFieldReviewStatus status,
            AiReviewSourceType sourceType,
            String message
    ) {
        this.review = review;
        this.field = field;
        this.submissionValue = submissionValue;
        this.order = order;
        this.title = title;
        this.type = type;
        this.status = status;
        this.sourceType = sourceType;
        this.message = message;
    }

    public void replaceCriteria(List<AiCriterionReview> updatedCriteria) {
        criteria.clear();
        criteria.addAll(updatedCriteria);
    }

    public Long getFieldId() {
        return field.getId();
    }

    public Long getSubmissionValueId() {
        return submissionValue == null ? null : submissionValue.getId();
    }
}
