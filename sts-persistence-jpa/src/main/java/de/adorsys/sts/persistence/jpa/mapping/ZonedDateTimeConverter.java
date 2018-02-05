package de.adorsys.sts.persistence.jpa.mapping;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Converter(autoApply = true)
public class ZonedDateTimeConverter implements AttributeConverter<ZonedDateTime, Timestamp> {

    @Override
    public java.sql.Timestamp convertToDatabaseColumn(ZonedDateTime entityValue) {
        if(entityValue == null) {
            return null;
        }

        return Timestamp.from(entityValue.toInstant());
    }

    @Override
    public ZonedDateTime convertToEntityAttribute(java.sql.Timestamp databaseValue) {
        if(databaseValue == null) {
            return null;
        }

        LocalDateTime localDateTime = databaseValue.toLocalDateTime();
        return localDateTime.atZone(ZoneOffset.UTC);
    }
}
