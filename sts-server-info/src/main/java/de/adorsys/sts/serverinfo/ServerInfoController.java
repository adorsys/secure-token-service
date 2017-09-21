package de.adorsys.sts.serverinfo;


import de.adorsys.sts.pop.EnablePOP;
import de.adorsys.sts.token.tokenexchange.EnableTokenExchange;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.util.Map;

@RestController
@Api(value = "/", tags = {"Endpoint Metada"}, description = "Endpoint Metadata")
@RequestMapping(path = "/")
public class ServerInfoController {

    @Autowired
    private HttpServletRequest servletRequest;

    @Autowired
    private ApplicationContext applicationContext;

    @GetMapping(produces={MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Endpoint Metada", notes = "Provide meta information on this API Endpoint")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Ok", response = ServerInfoResponse.class)})
    public ResponseEntity<Object> info(){
        String urlBase = StringUtils.substringBeforeLast(servletRequest.getRequestURL().toString(), servletRequest.getRequestURI());
        ServerInfoResponse serverInfoResponse = new ServerInfoResponse();

        if(isConfigurationEnabled(EnablePOP.class)) {
            serverInfoResponse.setJwks_url(urlBase + "/pop");
        }

        serverInfoResponse.setAdmin_url(urlBase + "/admin");

        if(isConfigurationEnabled(EnableTokenExchange.class)) {
            serverInfoResponse.setToken_exchange(urlBase + "/token");
        }

        serverInfoResponse.setApi_docs_url(urlBase + "/api-docs/index.html");

        return ResponseEntity.ok(serverInfoResponse);
    }

    private boolean isConfigurationEnabled(Class<? extends Annotation> annotation) {
        return applicationContext.getBeansWithAnnotation(annotation).size() > 0;
    }
}
