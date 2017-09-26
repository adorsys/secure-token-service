package de.adorsys.sts.admin;


import de.adorsys.sts.common.config.AdminResource;
import de.adorsys.sts.resourceserver.ResourceServerManager;
import de.adorsys.sts.resourceserver.model.ResourceServers;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Api(value = "/admin", tags={"Admin Endpoint"}, description = "Admin Endpoint")
@RequestMapping("/admin")
@AdminResource
public class AdminController {

    @Autowired
    private ResourceServerManager resourceServerManager;

    @GetMapping(path="/resourceServer", produces={MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(value = "Returns the list of resource servers", response=ResourceServers.class, notes = "Fetches and returns resource server descriptions.")
    @ApiResponses(value = { @ApiResponse(code = 200, message = "Ok")})
    public ResponseEntity<ResourceServers> loadResourceServers(){
        ResourceServers servers = resourceServerManager.loadResourceServers();
        return ResponseEntity.ok(servers);
    }
}
