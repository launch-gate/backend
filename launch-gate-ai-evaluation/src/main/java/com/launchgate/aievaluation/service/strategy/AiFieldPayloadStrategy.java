package com.launchgate.aievaluation.service.strategy;

import com.launchgate.aievaluation.service.model.PreparedFieldPayload;
import com.launchgate.contest.entity.SubmissionField;
import com.launchgate.submission.entity.SubmissionValue;

public interface AiFieldPayloadStrategy {
    boolean supports(SubmissionField field, SubmissionValue value);

    PreparedFieldPayload prepare(SubmissionField field, SubmissionValue value);
}
