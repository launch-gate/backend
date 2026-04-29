package com.launchgate.contest.entity;

import com.launchgate.contest.entity.stage.ContestStage;
import com.launchgate.contest.entity.order.BaseOrderEntity;
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
@Table(schema = "contest", name = "contest_resources")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ContestResource extends BaseOrderEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stage_id", nullable = false)
    private ContestStage stage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 32)
    private ResourceType type;

    @Column(nullable = false, length = 180)
    private String title;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "link_url", length = 1000)
    private String linkUrl;

    @Column(name = "file_id")
    private Long fileId;

    public ContestResource(ContestStage stage, int order) {
        super(order);
        this.stage = stage;
    }

    public void update(int order, ResourceType type, String title, String description, String linkUrl, Long fileId) {
        setOrder(order);
        this.type = type;
        this.title = title;
        this.description = description;
        this.linkUrl = linkUrl;
        this.fileId = fileId;
    }

    public Long getStageId() {
        return stage.getId();
    }
}
