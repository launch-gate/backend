package com.launchgate.mentoring.service;

import com.launchgate.mentoring.dto.MentorAssignmentResponse;
import com.launchgate.mentoring.entity.MentorAssignment;
import com.launchgate.mentoring.dto.MentorCallResponse;
import com.launchgate.mentoring.entity.MentorCall;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MentoringMapper {

    public static MentorAssignmentResponse toResponse(MentorAssignment assignment) {
        return new MentorAssignmentResponse(
                assignment.getId(),
                assignment.getContest().getId(),
                assignment.getTeam().getId(),
                assignment.getMentor().getId()
        );
    }

    public static MentorCallResponse toResponse(MentorCall call) {
        return new MentorCallResponse(
                call.getId(),
                call.getTeam().getId(),
                call.getMentor().getId(),
                call.getStartsAt(),
                call.getEndsAt(),
                call.getLink(),
                call.getNotes()
        );
    }
}
