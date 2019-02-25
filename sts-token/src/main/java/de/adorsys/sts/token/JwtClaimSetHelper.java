package de.adorsys.sts.token;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.adorsys.sts.cryptoutils.ObjectMapperSPI;
import org.apache.commons.lang3.StringUtils;

import com.nimbusds.jwt.JWTClaimsSet;

import de.adorsys.sts.resourceserver.model.ResourceServerAndSecret;

public class JwtClaimSetHelper {

    public static JWTClaimsSet.Builder handleResources(
            JWTClaimsSet.Builder claimSetBuilder,
            List<ResourceServerAndSecret> processedResources,
            ObjectMapperSPI mapper
    ) {

        if (processedResources.isEmpty()) return claimSetBuilder;
        Map<String, String> map = new HashMap<>();
        for (ResourceServerAndSecret rs : processedResources) {
            String userSecretClaimName = rs.getResourceServer().getUserSecretClaimName();
            if (StringUtils.isBlank(userSecretClaimName)) userSecretClaimName = rs.getResourceServer().getAudience();
            if (StringUtils.isNotBlank(userSecretClaimName))
                map.put(userSecretClaimName, rs.getEncryptedSecret());
        }

        try {
            return claimSetBuilder.claim("user-secret", mapper.writeValueAsString(map));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
