package com.launchgate.aievaluation.entity;

import com.launchgate.identity.entity.UserAccount;
import com.launchgate.submission.entity.StageSubmission;
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
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "ai_evaluation", name = "submission_ai_reviews")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AiSubmissionReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", nullable = false, unique = true)
    private StageSubmission submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requested_by", nullable = false)
    private UserAccount requestedByUser;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 32)
    private AiReviewStatus status;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at", nullable = false)
    private Instant updatedAt;

    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<AiFieldReview> fieldReviews = new HashSet<>();

    public AiSubmissionReview(StageSubmission submission, UserAccount requestedByUser, Instant now) {
        this.submission = submission;
        this.requestedByUser = requestedByUser;
        this.status = AiReviewStatus.COMPLETED;
        this.createdAt = now;
        this.updatedAt = now;
    }

    public void refresh(UserAccount requestedByUser, List<AiFieldReview> updatedFieldReviews, Instant now) {
        this.requestedByUser = requestedByUser;
        this.updatedAt = now;
        fieldReviews.clear();
        fieldReviews.addAll(updatedFieldReviews);
        status = updatedFieldReviews.stream().allMatch(review -> review.getStatus() == AiFieldReviewStatus.COMPLETED)
                ? AiReviewStatus.COMPLETED
                : AiReviewStatus.COMPLETED_WITH_WARNINGS;
    }

    public Long getSubmissionId() {
        return submission.getId();
    }

    public Long getRequestedBy() {
        return requestedByUser.getId();
    }
}
