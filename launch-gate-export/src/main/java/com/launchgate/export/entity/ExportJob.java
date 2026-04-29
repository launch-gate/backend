package com.launchgate.export.entity;

import com.launchgate.contest.entity.Contest;
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
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.time.Instant;

@Entity
@Table(schema = "exporting", name = "export_jobs")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExportJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "contest_id", nullable = false)
    private Long contestId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contest_id", insertable = false, updatable = false)
    private Contest contest;

    @Column(name = "created_by", nullable = false)
    private Long createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", insertable = false, updatable = false)
    private UserAccount creator;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ExportFormat format;

    @Column(name = "custom_prompt", columnDefinition = "text")
    private String customPrompt;

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    public ExportJob(Long contestId, Long createdBy, ExportFormat format, String customPrompt, Instant createdAt) {
        this.contestId = contestId;
        this.createdBy = createdBy;
        this.format = format;
        this.customPrompt = customPrompt;
        this.createdAt = createdAt;
    }

}
