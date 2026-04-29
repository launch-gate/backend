package com.launchgate.submission.entity;

import com.launchgate.contest.entity.SubmissionField;
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

@Entity
@Table(schema = "submission", name = "submission_values")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubmissionValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "submission_id", nullable = false)
    private Long submissionId;

    @Column(name = "field_id", nullable = false)
    private Long fieldId;

    @Column(name = "value_text", columnDefinition = "text")
    private String valueText;

    @Column(name = "file_ids", columnDefinition = "text")
    private String fileIds;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "submission_id", insertable = false, updatable = false)
    private StageSubmission submission;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id", insertable = false, updatable = false)
    private SubmissionField field;

    public SubmissionValue(Long submissionId, Long fieldId) {
        this.submissionId = submissionId;
        this.fieldId = fieldId;
    }

    public void update(String valueText, String fileIds) {
        this.valueText = valueText;
        this.fileIds = fileIds;
    }
}
