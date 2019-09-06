package de.adorsys.sts.tests.e2e;

import com.jayway.jsonpath.JsonPath;
import de.adorsys.sts.keymanagement.service.KeyManagementProperties;
import de.adorsys.sts.keyrotation.KeyRotationSchedule;
import de.adorsys.sts.persistence.jpa.config.EnableJpaPersistence;
import de.adorsys.sts.tests.BaseEndpointTest;
import de.adorsys.sts.tests.config.WithControllableClock;
import de.adorsys.sts.tests.config.WithPopConfig;
import de.adorsys.sts.tests.config.WithRotation;
import de.adorsys.sts.tests.config.WithoutWebSecurityConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableJpaPersistence
@ContextConfiguration(classes = {
        WithPopConfig.class,
        WithoutWebSecurityConfig.class,
        WithControllableClock.class,
        WithRotation.class
})
class PopControllerRotationJpaTest extends BaseEndpointTest {

    @Autowired
    private WithControllableClock.ClockTestable clock;

    @Autowired
    private KeyRotationSchedule rotationSchedule;

    @Autowired
    private KeyManagementProperties props;

    @Test
    @SneakyThrows
    void testPopRotates() {
        MvcResult oldResult = mvc.perform(get("/pop"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        Collection<String> oldKeyIds = JsonPath.parse(oldResult.getResponse().getContentAsString())
                .read("$.keys[*].kid");

        // Expire keys using validity interval
        clock.setInstant(clock.instant().plusMillis(
                props.getKeystore().getKeys().getEncKeyPairs().getLegacyInterval() + 10)
        );
        rotationSchedule.scheduledRotation();

        MvcResult newResult = mvc.perform(get("/pop"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn();
        Collection<String> newIds = JsonPath.parse(newResult.getResponse().getContentAsString())
                .read("$.keys[*].kid");

        assertThat(newIds).doesNotContainAnyElementsOf(oldKeyIds);
    }
}
