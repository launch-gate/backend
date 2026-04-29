package com.launchgate.contest.entity;

import com.launchgate.contest.entity.order.BaseOrderEntity;
import com.launchgate.contest.entity.stage.ContestStage;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "contest", name = "submission_fields")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SubmissionField extends BaseOrderEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stage_id", nullable = false)
    private ContestStage stage;

    @Column(nullable = false, length = 160)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private FieldType type;

    @Column(nullable = false)
    private boolean required;

    @Column(name = "file_formats", length = 240)
    private String fileFormats;

    @Column(name = "max_file_size_mb")
    private Integer maxFileSizeMb;

    @Column(name = "options", columnDefinition = "text")
    private String options;

    @Column(name = "participant_hint", columnDefinition = "text")
    private String participantHint;

    @Column(name = "example_value", columnDefinition = "text")
    private String exampleValue;

    @Column(name = "expert_note", columnDefinition = "text")
    private String expertNote;

    @Column(name = "criteria_description", columnDefinition = "text")
    private String criteriaDescription;

    public SubmissionField(ContestStage stage, int order, String title, FieldType type, boolean required) {
        super(order);
        this.stage = stage;
        this.title = title;
        this.type = type;
        this.required = required;
    }

    public void enrich(String fileFormats, Integer maxFileSizeMb, String options, String participantHint,
                       String exampleValue, String expertNote) {
        this.fileFormats = fileFormats;
        this.maxFileSizeMb = maxFileSizeMb;
        this.options = options;
        this.participantHint = participantHint;
        this.exampleValue = exampleValue;
        this.expertNote = expertNote;
    }

    public void update(
            int order,
            String title,
            FieldType type,
            boolean required,
            String fileFormats,
            Integer maxFileSizeMb,
            String options,
            String participantHint,
            String exampleValue,
            String expertNote,
            String criteriaDescription
    ) {
        setOrder(order);
        this.title = title;
        this.type = type;
        this.required = required;
        this.fileFormats = fileFormats;
        this.maxFileSizeMb = maxFileSizeMb;
        this.options = options;
        this.participantHint = participantHint;
        this.exampleValue = exampleValue;
        this.expertNote = expertNote;
        this.criteriaDescription = criteriaDescription;
    }

    public Long getStageId() {
        return stage.getId();
    }

}
