package com.launchgate.contest.entity;

import com.launchgate.contest.entity.order.BaseOrderEntity;
import com.launchgate.contest.entity.stage.ContestStage;
import com.launchgate.contest.enums.FieldType;
import com.launchgate.contest.enums.SubmissionFieldFileFormat;
import jakarta.persistence.Column;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import jakarta.persistence.CascadeType;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            schema = "contest",
            name = "submission_field_file_formats",
            joinColumns = @JoinColumn(name = "field_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "file_format", nullable = false, length = 32)
    @OrderColumn(name = "format_order")
    private List<SubmissionFieldFileFormat> fileFormats = new ArrayList<>();

    @Column(name = "max_file_size_mb")
    private Integer maxFileSizeMb;

    @Column(name = "participant_hint", columnDefinition = "text")
    private String participantHint;

    @Column(name = "example_value", columnDefinition = "text")
    private String exampleValue;

    @Column(name = "expert_note", columnDefinition = "text")
    private String expertNote;

    @OneToMany(mappedBy = "field", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<FieldCriterion> criteria = new HashSet<>();

    public SubmissionField(ContestStage stage, int order, String title, FieldType type, boolean required) {
        super(order);
        this.stage = stage;
        this.title = title;
        this.type = type;
        this.required = required;
    }

    public void enrich(
            List<SubmissionFieldFileFormat> fileFormats,
            Integer maxFileSizeMb,
            String participantHint,
            String exampleValue,
            String expertNote
    ) {
        this.fileFormats.clear();
        this.fileFormats.addAll(fileFormats == null ? List.of() : fileFormats);
        this.maxFileSizeMb = maxFileSizeMb;
        this.participantHint = participantHint;
        this.exampleValue = exampleValue;
        this.expertNote = expertNote;
    }

    public void update(
            int order,
            String title,
            FieldType type,
            boolean required,
            List<SubmissionFieldFileFormat> fileFormats,
            Integer maxFileSizeMb,
            String participantHint,
            String exampleValue,
            String expertNote
    ) {
        setOrder(order);
        this.title = title;
        this.type = type;
        this.required = required;
        this.fileFormats.clear();
        this.fileFormats.addAll(fileFormats == null ? List.of() : fileFormats);
        this.maxFileSizeMb = maxFileSizeMb;
        this.participantHint = participantHint;
        this.exampleValue = exampleValue;
        this.expertNote = expertNote;
    }

    public void replaceCriteria(List<FieldCriterion> updatedCriteria) {
        criteria.clear();
        criteria.addAll(updatedCriteria);
    }

    public Long getStageId() {
        return stage.getId();
    }

}
