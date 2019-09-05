package de.adorsys.sts.tests.e2e;

import de.adorsys.sts.keymanagement.model.StsKeyStore;
import de.adorsys.sts.keymanagement.persistence.KeyStoreRepository;
import de.adorsys.sts.persistence.jpa.config.EnableJpaPersistence;
import de.adorsys.sts.tests.BaseEndpointTest;
import de.adorsys.sts.tests.config.WithPopConfig;
import de.adorsys.sts.tests.config.WithoutWebSecurityConfig;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.isIn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@EnableJpaPersistence
@ContextConfiguration(classes = {WithPopConfig.class, WithoutWebSecurityConfig.class})
class PopControllerJpaTest extends BaseEndpointTest {

    @Autowired
    @Qualifier("cached")
    private KeyStoreRepository repository;

    @Test
    @SneakyThrows
    void testPopOk() {
        StsKeyStore keyStore = repository.load();

        mvc.perform(get("/pop"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.keys.length()").value(2))
                .andExpect(jsonPath("$.keys[*].use").value(containsInAnyOrder("sig", "enc")))
                .andExpect(jsonPath("$.keys[*].e").value(containsInAnyOrder("AQAB", "AQAB")))
                .andExpect(jsonPath("$.keys[0].kid").value(isIn(keyStore.getKeyEntries().keySet())))
                .andExpect(jsonPath("$.keys[1].kid").value(isIn(keyStore.getKeyEntries().keySet())));

    }
}
