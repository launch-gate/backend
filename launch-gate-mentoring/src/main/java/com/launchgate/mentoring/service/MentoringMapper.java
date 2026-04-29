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
                assignment.getContestId(),
                assignment.getTeamId(),
                assignment.getMentorId()
        );
    }

    public static MentorCallResponse toResponse(MentorCall call) {
        return new MentorCallResponse(
                call.getId(),
                call.getTeamId(),
                call.getMentorId(),
                call.getStartsAt(),
                call.getEndsAt(),
                call.getLink(),
                call.getNotes()
        );
    }
}
