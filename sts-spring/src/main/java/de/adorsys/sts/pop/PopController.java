package de.adorsys.sts.pop;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.JWKSet;
import de.adorsys.sts.common.config.TokenResource;
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

import java.util.Map;

@RestController
@Api(value = "/pop", tags = {"Proof of Possession RFC7800"}, description = "Public key distribution endpoint")
@RequestMapping("/pop")
@TokenResource
public class PopController {

    private final PopService popService;

    @Autowired
    public PopController(PopService popService) {
        this.popService = popService;
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @ApiOperation(
            value = "Read server public keys",
            response = JWKSet.class,
            notes = "Fetches public keys of the target server. Keys are used to encrypt data sent to the server and " +
                    "also send a response encryption key to the server. See RFC7800")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "Ok")})
    public ResponseEntity<String> getPublicKeys() {
        JWKSet publicKeys = popService.getPublicKeys();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> jsonObject = publicKeys.toJSONObject();
        JsonNode jsonNode = mapper.convertValue(jsonObject, JsonNode.class);

        return ResponseEntity.ok(jsonNode.toString());
    }
}
