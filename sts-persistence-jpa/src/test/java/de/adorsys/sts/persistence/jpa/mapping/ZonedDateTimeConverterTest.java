package de.adorsys.sts.persistence.jpa.mapping;

import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

public class ZonedDateTimeConverterTest {

    private ZonedDateTimeConverter converter;

    @Before
    public void setup() throws Exception {
      converter = new ZonedDateTimeConverter();
    }

    @Test
    public void should() throws Exception {

        ZonedDateTime zonedDateTime = ZonedDateTime.of(2017, 2, 7, 11, 16, 22, 333, ZoneOffset.UTC);
        Timestamp timestamp = converter.convertToDatabaseColumn(zonedDateTime);

        long timeMillis = timestamp.getTime();
        long zonedDateTimeMillis = zonedDateTime.toInstant().toEpochMilli();

        assertThat(timeMillis, is(equalTo(zonedDateTimeMillis)));

        ZonedDateTime backConvertedZonedDateTime = converter.convertToEntityAttribute(timestamp);
        assertThat(backConvertedZonedDateTime.toInstant().toEpochMilli(), is(equalTo(zonedDateTimeMillis)));
    }

}