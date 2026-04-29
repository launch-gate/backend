package com.launchgate.contest.utils.resource;

import com.launchgate.contest.dto.resources.ResourceResponse;
import com.launchgate.contest.entity.ContestResource;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ResourceMapper {

    public static ResourceResponse toResourceResponse(ContestResource resource) {
        return new ResourceResponse(
                resource.getId(),
                resource.getOrder(),
                resource.getType(),
                resource.getTitle(),
                resource.getDescription(),
                resource.getLinkUrl(),
                resource.getFileId()
        );
    }
}
