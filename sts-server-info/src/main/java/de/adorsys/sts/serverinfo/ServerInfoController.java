package de.adorsys.sts.serverinfo;


import de.adorsys.sts.admin.EnableAdmin;
import de.adorsys.sts.pop.EnablePOP;
import de.adorsys.sts.token.tokenexchange.server.EnableTokenExchangeServer;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.lang.annotation.Annotation;

@RestController
@Tag(name = "Endpoint Metadata", description = "Endpoint Metadata")
@RequestMapping(path = "/")
public class ServerInfoController {

    @Autowired
    private HttpServletRequest servletRequest;

    @Autowired
    private ApplicationContext applicationContext;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Endpoint Metadata", description = "Provide meta information on this API Endpoint", responses = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Ok",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = ServerInfoResponse.class))
            )
    })
    public ResponseEntity<Object> info() {
        String urlBase = StringUtils.substringBeforeLast(servletRequest.getRequestURL().toString(), servletRequest.getRequestURI());
        ServerInfoResponse serverInfoResponse = new ServerInfoResponse();

        if (isConfigurationEnabled(EnablePOP.class)) {
            serverInfoResponse.setJwks_url(urlBase + "/pop");
        }

        if (isConfigurationEnabled(EnableAdmin.class)) {
            serverInfoResponse.setAdmin_url(urlBase + "/admin");
        }

        if (isConfigurationEnabled(EnableTokenExchangeServer.class)) {
            serverInfoResponse.setToken_exchange(urlBase + "/token");
        }

        if (isConfigurationEnabled(Tag.class)) {
            serverInfoResponse.setApi_docs_url(urlBase + "/v3/api-docs");
        }

        return ResponseEntity.ok(serverInfoResponse);
    }

    private boolean isConfigurationEnabled(Class<? extends Annotation> annotation) {
        return applicationContext.getBeansWithAnnotation(annotation).size() > 0;
    }
}
