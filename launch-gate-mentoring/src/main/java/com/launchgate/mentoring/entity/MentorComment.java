package com.launchgate.mentoring.entity;

import com.launchgate.identity.entity.UserAccount;
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
import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "mentoring", name = "mentor_comments")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MentorComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stage_submission_id", nullable = false)
    private StageSubmission stageSubmission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mentor_id", nullable = false)
    private UserAccount mentor;

    @Column(nullable = false, columnDefinition = "text")
    private String text;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public MentorComment(StageSubmission stageSubmission, UserAccount mentor, String text, Instant createdAt) {
        this.stageSubmission = stageSubmission;
        this.mentor = mentor;
        this.text = text;
        this.createdAt = createdAt;
    }

    public Long getStageSubmissionId() {
        return stageSubmission.getId();
    }

    public Long getMentorId() {
        return mentor.getId();
    }
}
