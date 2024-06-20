package de.adorsys.sts.pop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.jwk.JWKSet;
import de.adorsys.sts.common.config.TokenResource;
import de.adorsys.sts.token.api.ProofOfPossessionRfc7800Api;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@TokenResource
@RequiredArgsConstructor
public class PopController implements ProofOfPossessionRfc7800Api {

    private final PopService popService;

    @Override
    public ResponseEntity<String> getPublicKeys() {
        JWKSet publicKeys = popService.getPublicKeys();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> jsonObject = publicKeys.toJSONObject();
        JsonNode jsonNode = mapper.convertValue(jsonObject, JsonNode.class);

        return ResponseEntity.ok(jsonNode.toString());
    }
}
