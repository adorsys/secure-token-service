package de.adorsys.sts.resourceserver;

import de.adorsys.sts.resourceserver.model.ResourceServer;
import de.adorsys.sts.resourceserver.service.ResourceServerManagementProperties;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Component
@ConfigurationProperties(prefix = "sts.resource-server-management")
@Validated
public class ResourceServerManagementConfigurationProperties implements ResourceServerManagementProperties {

    @NotNull
    @Size(min = 1)
    private List<ResourceServer> resourceServers;

    @Valid
    @NotNull
    private ResourceRetrieverConfigurationProperties resourceRetriever;

    @Override
    public List<ResourceServer> getResourceServers() {
        return resourceServers;
    }

    @Override
    public ResourceRetrieverProperties getResourceRetriever() {
        return resourceRetriever;
    }

    public void setResourceServers(List<ResourceServer> resourceServers) {
        this.resourceServers = resourceServers;
    }

    public void setResourceRetriever(ResourceRetrieverConfigurationProperties resourceRetriever) {
        this.resourceRetriever = resourceRetriever;
    }

    public static class ResourceRetrieverConfigurationProperties implements ResourceRetrieverProperties {

        @Min(0)
        @Max(Integer.MAX_VALUE)
        private Integer httpConnectTimeout;

        @Min(0)
        @Max(Integer.MAX_VALUE)
        private Integer httpReadTimeout;

        @Min(0)
        @Max(Integer.MAX_VALUE)
        private Integer httpSizeLimit;

        @Override
        public Integer getHttpConnectTimeout() {
            return httpConnectTimeout;
        }

        @Override
        public Integer getHttpReadTimeout() {
            return httpReadTimeout;
        }

        @Override
        public Integer getHttpSizeLimit() {
            return httpSizeLimit;
        }

        public void setHttpConnectTimeout(Integer httpConnectTimeout) {
            this.httpConnectTimeout = httpConnectTimeout;
        }

        public void setHttpReadTimeout(Integer httpReadTimeout) {
            this.httpReadTimeout = httpReadTimeout;
        }

        public void setHttpSizeLimit(Integer httpSizeLimit) {
            this.httpSizeLimit = httpSizeLimit;
        }
    }
}
