package de.adorsys.sts.keymanagement.util;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class DateTimeUtils {

    public static ZonedDateTime addMillis(ZonedDateTime now, Long millis) {
        return now.toInstant().plusMillis(millis).atZone(ZoneOffset.UTC);
    }

    private DateTimeUtils() {
        throw new IllegalStateException("Not supported");
    }
}
