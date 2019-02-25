package de.adorsys.sts;

import de.adorsys.sts.cryptoutils.envutils.EnvProperties;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseUtils {
	public static final String ERROR_FIELD = "error";
	public static final String ERROR_DESCRIPTION_FIELD = "error_description";
	public static final String ERROR_INVALID_REQUEST_VALUE = "invalid_request";

	public static String getIssuer(HttpServletRequest servletRequest) {
		String issuerUrl = EnvProperties.getEnvOrSysProp("AUTH_SERVER_ISS_URL", true);
		if(StringUtils.isNotBlank(issuerUrl)) return issuerUrl;
		return StringUtils.substringBeforeLast(servletRequest.getRequestURL().toString(), servletRequest.getRequestURI());
	}

	public static ResponseEntity<Object> missingParam(String paramName){
		Map<String, List<String>> resultMap= new HashMap<>();
		resultMap.put(ERROR_FIELD, Collections.singletonList(ERROR_INVALID_REQUEST_VALUE));
		resultMap.put(ERROR_DESCRIPTION_FIELD, Collections.singletonList("Request parameter "+ paramName +" is missing. See https://tools.ietf.org/html/draft-ietf-oauth-token-exchange-08#section-2.1"));
		return ResponseEntity.badRequest().body(CollectionUtils.toMultiValueMap(resultMap));
	}

	public static ResponseEntity<Object> invalidParam(String message){
		Map<String, List<String>> resultMap= new HashMap<>();
		resultMap.put(ERROR_FIELD, Collections.singletonList(ERROR_INVALID_REQUEST_VALUE));
		resultMap.put(ERROR_DESCRIPTION_FIELD, Collections.singletonList(message));
		return ResponseEntity.badRequest().body(CollectionUtils.toMultiValueMap(resultMap));
	}
}
