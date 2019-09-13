package de.adorsys.sts.tests.e2e.testcomponents;

import com.jayway.jsonpath.JsonPath;
import de.adorsys.sts.keymanagement.service.KeyManagementProperties;
import de.adorsys.sts.keyrotation.KeyRotationSchedule;
import de.adorsys.sts.tests.config.WithControllableClock;
import lombok.SneakyThrows;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Component
public class PopRotationValidator {

    private final MockMvc mvc;
    private final WithControllableClock.ClockTestable clock;
    private final KeyRotationSchedule rotationSchedule;
    private final KeyManagementProperties props;

    public PopRotationValidator(MockMvc mvc, @Lazy WithControllableClock.ClockTestable clock,
                                KeyRotationSchedule rotationSchedule, KeyManagementProperties props) {
        this.mvc = mvc;
        this.clock = clock;
        this.rotationSchedule = rotationSchedule;
        this.props = props;
    }

    @SneakyThrows
    public void testPopRotates() {
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
