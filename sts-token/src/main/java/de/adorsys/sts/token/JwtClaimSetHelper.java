package de.adorsys.sts.token;

import com.nimbusds.jwt.JWTClaimsSet;
import de.adorsys.sts.resourceserver.model.ResourceServerAndSecret;

import java.util.ArrayList;
import java.util.List;

public class JwtClaimSetHelper {

    public static JWTClaimsSet.Builder handleResources(JWTClaimsSet.Builder claimSetBuilder, List<ResourceServerAndSecret> processedResources) {
        if (processedResources.isEmpty()) return claimSetBuilder;
        if (processedResources.size() == 1) {
            claimSetBuilder = claimSetBuilder.audience(processedResources.get(0).getResourceServer().getAudience());
        } else {
            List<String> processedResourcesStr = new ArrayList<>();
            for (ResourceServerAndSecret resourceServerAndSecret : processedResources) {
                processedResourcesStr.add(resourceServerAndSecret.getResourceServer().getAudience());
            }
            claimSetBuilder = claimSetBuilder.audience(processedResourcesStr);
        }
        return claimSetBuilder;
    }
}
