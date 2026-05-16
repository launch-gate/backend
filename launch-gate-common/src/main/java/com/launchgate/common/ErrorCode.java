package com.launchgate.common;

import lombok.experimental.UtilityClass;

@UtilityClass
public class ErrorCode {

    public String applicationErrorCode = "launch-gate-error";

    public String conflictErrorCode = "conflict";

    public String forbiddenErrorCode = "forbidden";

    public String validationErrorCode = "validation_failed";

    public String notFoundErrorCode = "not_found";
}
