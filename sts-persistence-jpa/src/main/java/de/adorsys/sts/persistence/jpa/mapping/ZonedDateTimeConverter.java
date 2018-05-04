package de.adorsys.sts.persistence.jpa.mapping;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Converter(autoApply = true)
public class ZonedDateTimeConverter implements AttributeConverter<ZonedDateTime, Timestamp> {

    @Override
    public java.sql.Timestamp convertToDatabaseColumn(ZonedDateTime entityValue) {
        return convert(entityValue);
    }

    public static Timestamp convert(ZonedDateTime entityValue) {
        if(entityValue == null) {
            return null;
        }

        return Timestamp.from(entityValue.toInstant());
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(java.sql.Timestamp databaseValue) {
        return convert(databaseValue);
    }

    public static ZonedDateTime convert(Timestamp databaseValue) {
        if(databaseValue == null) {
            return null;
        }

        Instant instant = databaseValue.toInstant();
        return instant.atZone(ZoneOffset.UTC);
    }
}
