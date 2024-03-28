package de.adorsys.sts.admin;


import de.adorsys.sts.common.config.AdminResource;
import de.adorsys.sts.resourceserver.model.ResourceServer;
import de.adorsys.sts.resourceserver.model.ResourceServers;
import de.adorsys.sts.resourceserver.service.ResourceServerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Admin Endpoint", description = "Admin Endpoint")
@RequestMapping("/admin")
@AdminResource
public class AdminController {

    private final ResourceServerService resourceServerService;

    @Autowired
    public AdminController(ResourceServerService resourceServerService) {
        this.resourceServerService = resourceServerService;
    }

    @GetMapping(path = "/resourceServer", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(
            summary = "Returns the list of resource servers",
            responses = {@ApiResponse(responseCode = "200", description = "Ok")},
            description = "Fetches and returns resource server descriptions."
    )
    public ResponseEntity<ResourceServers> loadResourceServers() {
        ResourceServers servers = resourceServerService.getAll();
        return ResponseEntity.ok(servers);
    }

    @PostMapping(path = "/resourceServer", produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(summary = "Adds a resource server", responses = {
            @ApiResponse(responseCode = "201", description = "No content")
    })
    public ResponseEntity addResourceServers(@RequestBody ResourceServer resourceServer) {
        resourceServerService.create(resourceServer);
        return ResponseEntity.noContent().build();
    }
}
