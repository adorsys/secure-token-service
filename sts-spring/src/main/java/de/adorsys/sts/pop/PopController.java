package de.adorsys.sts.pop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.JWKSet;
import de.adorsys.sts.common.config.TokenResource;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@Tag(name = "Proof of Possession RFC7800", description = "Public key distribution endpoint")
@RequestMapping("/pop")
@TokenResource
public class PopController {

    private final PopService popService;

    @Autowired
    public PopController(PopService popService) {
        this.popService = popService;
    }

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE})
    @Operation(
            summary = "Read server public keys",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Ok")
            },
            description = "Fetches public keys of the target server. Keys are used to encrypt data sent to the server and " +
                    "also send a response encryption key to the server. See RFC7800")
    public ResponseEntity<String> getPublicKeys() {
        JWKSet publicKeys = popService.getPublicKeys();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> jsonObject = publicKeys.toJSONObject();
        JsonNode jsonNode = mapper.convertValue(jsonObject, JsonNode.class);

        return ResponseEntity.ok(jsonNode.toString());
    }
}
