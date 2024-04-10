package de.adorsys.sts.persistence.jpa.mapping;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;

class ZonedDateTimeConverterTest {

    private ZonedDateTimeConverter converter;

    @BeforeEach
    public void setup() {
      converter = new ZonedDateTimeConverter();
    }

    @Test
    void should() {

        ZonedDateTime zonedDateTime = ZonedDateTime.of(2017, 2, 7, 11, 16, 22, 333, ZoneOffset.UTC);
        Timestamp timestamp = converter.convertToDatabaseColumn(zonedDateTime);

        long timeMillis = timestamp.getTime();
        long zonedDateTimeMillis = zonedDateTime.toInstant().toEpochMilli();

        assertThat(timeMillis).isEqualTo(zonedDateTimeMillis);

        ZonedDateTime backConvertedZonedDateTime = converter.convertToEntityAttribute(timestamp);
        assertThat(backConvertedZonedDateTime.toInstant().toEpochMilli()).isEqualTo(zonedDateTimeMillis);
    }

}