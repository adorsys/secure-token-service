package de.adorsys.sts.persistence.jpa.mapping;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Converter
public class TimestampConverter implements AttributeConverter<ZonedDateTime, Timestamp> {
    @Override
    public Timestamp convertToDatabaseColumn(ZonedDateTime zonedTime) {
        if (zonedTime == null) {
            return null;
        }
        return new Timestamp(zonedTime.toInstant().toEpochMilli());
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(Timestamp localTime) {
        if (localTime == null) {
            return null;
        }
        return ZonedDateTime.ofInstant(Instant.ofEpochMilli(localTime.getTime()), ZoneOffset.UTC);
    }
}