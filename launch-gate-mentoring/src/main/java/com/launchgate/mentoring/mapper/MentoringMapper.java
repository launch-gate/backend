package com.launchgate.mentoring.mapper;

import com.launchgate.mentoring.dto.MentorAssignmentResponse;
import com.launchgate.mentoring.entity.MentorAssignment;
import com.launchgate.mentoring.dto.MentorCallResponse;
import com.launchgate.mentoring.entity.MentorCall;
import lombok.experimental.UtilityClass;

@UtilityClass
public class MentoringMapper {

    public MentorAssignmentResponse toResponse(MentorAssignment assignment) {
        return new MentorAssignmentResponse(
                assignment.getId(),
                assignment.getContest().getId(),
                assignment.getTeam().getId(),
                assignment.getMentor().getId()
        );
    }

    public MentorCallResponse toResponse(MentorCall call) {
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
