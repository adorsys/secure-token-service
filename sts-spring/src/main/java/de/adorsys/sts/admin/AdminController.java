package de.adorsys.sts.admin;


import de.adorsys.sts.common.config.AdminResource;
import de.adorsys.sts.resourceserver.model.ResourceServer;
import de.adorsys.sts.resourceserver.model.ResourceServers;
import de.adorsys.sts.resourceserver.service.ResourceServerService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Api(value = "/admin", tags = {"Admin Endpoint"}, description = "Admin Endpoint")
@RequestMapping("/admin")
@AdminResource
public class AdminController {

    private final ResourceServerService resourceServerService;

    @Autowired
    public AdminController(ResourceServerService resourceServerService) {
        this.resourceServerService = resourceServerService;
    }

    @GetMapping(path = "/resourceServer", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Returns the list of resource servers", response = ResourceServers.class, notes = "Fetches and returns resource server descriptions.")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Ok")})
    public ResponseEntity<ResourceServers> loadResourceServers() {
        ResourceServers servers = resourceServerService.getAll();
        return ResponseEntity.ok(servers);
    }

    @PostMapping(path = "/resourceServer", produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Adds a resource server", response = ResourceServers.class)
    @ApiResponses(value = {@ApiResponse(code = 201, message = "No content")})
    public ResponseEntity addResourceServers(@RequestBody ResourceServer resourceServer) {
        resourceServerService.create(resourceServer);
        return ResponseEntity.noContent().build();
    }
}
