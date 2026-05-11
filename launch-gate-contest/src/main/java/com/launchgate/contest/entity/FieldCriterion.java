package com.launchgate.contest.entity;

import com.launchgate.contest.entity.order.BaseOrderEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(schema = "contest", name = "field_criteria")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FieldCriterion extends BaseOrderEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "field_id", nullable = false)
    private SubmissionField field;

    @Column(name = "description", nullable = false, columnDefinition = "text")
    private String description;

    public FieldCriterion(SubmissionField field, int order, String description) {
        super(order);
        this.field = field;
        this.description = description;
    }

    public Long getFieldId() {
        return field.getId();
    }

}
