package com.launchgate.contest.entity.stage;

import com.launchgate.contest.entity.*;
import com.launchgate.contest.entity.order.BaseOrderEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(schema = "contest", name = "contest_stages")
@Getter
@NoArgsConstructor
public class ContestStage extends BaseOrderEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id", nullable = false)
    private Contest contest;

    @Column(nullable = false, length = 240)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "rules", columnDefinition = "text")
    private String rules;

    @Column(name = "extra_info", columnDefinition = "text")
    private String extraInfo;

    @Column(name = "deadline_at")
    private Instant deadlineAt;

    @Column(name = "eliminating", nullable = false)
    private boolean eliminating;

    @Enumerated(EnumType.STRING)
    @Column(name = "score_scale", nullable = false, length = 32)
    private ScoreScale scoreScale;

    @OneToMany(mappedBy = "stage")
    private Set<SubmissionField> fields = new HashSet<>();

    @OneToMany(mappedBy = "stage")
    private Set<ContestResource> resources = new HashSet<>();

    public ContestStage(Contest contest,
                        int order,
                        String title, String description, String rules, String extraInfo,
                        Instant deadlineAt, boolean eliminating, ScoreScale scoreScale) {
        super(order);
        this.contest = contest;
        this.title = title;
        this.description = description;
        this.rules = rules;
        this.extraInfo = extraInfo;
        this.deadlineAt = deadlineAt;
        this.eliminating = eliminating;
        this.scoreScale = scoreScale;
    }

    public void update(String title, String description, String rules, String extraInfo,
                       Instant deadlineAt, boolean eliminating, ScoreScale scoreScale) {
        this.title = title;
        this.description = description;
        this.rules = rules;
        this.extraInfo = extraInfo;
        this.deadlineAt = deadlineAt;
        this.eliminating = eliminating;
        this.scoreScale = scoreScale;
    }

    public Long getContestId() {
        return contest.getId();
    }

}
