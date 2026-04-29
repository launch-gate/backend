package com.launchgate.contest.utils.organizer;

import com.launchgate.contest.dto.OrganizerResponse;
import com.launchgate.contest.entity.ContestOrganizer;
import lombok.experimental.UtilityClass;

@UtilityClass
public class OrganizerMapper {

    public static OrganizerResponse toOrganizerResponse(ContestOrganizer organizer) {
        return new OrganizerResponse(organizer.getId(), organizer.getUserId(), organizer.getRole());
    }

}
