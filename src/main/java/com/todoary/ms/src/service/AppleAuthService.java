package com.todoary.ms.src.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.todoary.ms.src.auth.jwt.JwtTokenProvider;
import com.todoary.ms.src.exception.common.TodoaryException;
import com.todoary.ms.src.web.dto.ClientSecretHeaderParam;
import com.todoary.ms.src.web.dto.ClientSecretPayloadParam;
import com.todoary.ms.util.BaseResponseStatus;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.text.ParseException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.todoary.ms.util.BaseResponseStatus.*;
import static com.todoary.ms.util.BaseResponseStatus.APPLE_AUTHENTICATION_CODE_VALIDATION_FAILURE;

@Slf4j
@RequiredArgsConstructor
@Service
public class AppleAuthService {
    @Value("${apple.iss}")
    private String AUD;

    @Value("${apple.client-id}")
    private String CLIENT_ID;

    @Value("${apple.team-id}")
    private String TEAM_ID;

    @Value("${apple.key-id}")
    private String KEY_ID;

    @Value("${apple.signature}")
    private String APPLE_CLIENT_SECRET_SIGNATURE;

    @Value("${apple.key-path}")
    private String KEY_PATH;

    @Value("${apple.token-url}")
    private String AUTH_TOKEN_URL;

    @Value("${apple.website-url}")
    private String APPLE_WEBSITE_URL;

    private final JwtTokenProvider jwtTokenProvider;

    public JSONObject getTokenResponseByCode(String authenticationCode) {
        return getTokenResponse(getTokenRequest(authenticationCode));
    }

    private String getClientSecret() {
        ClientSecretHeaderParam clientSecretHeaderParam = new ClientSecretHeaderParam(
                KEY_ID,
                APPLE_CLIENT_SECRET_SIGNATURE
        );

        ClientSecretPayloadParam clientSecretPayloadParam = new ClientSecretPayloadParam(
                TEAM_ID,
                Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant()),
                AUD,
                CLIENT_ID,
                getPrivateKey()
        );

        return jwtTokenProvider.createAppleClientSecret(clientSecretHeaderParam, clientSecretPayloadParam);

    }

    private Map getTokenRequest(String authenticationCode) {
        Map<String, String> tokenRequest = new HashMap<>();

        tokenRequest.put("client_id", CLIENT_ID);
        tokenRequest.put("client_secret", getClientSecret());
        tokenRequest.put("code", authenticationCode);
        tokenRequest.put("grant_type", "authorization_code");
        tokenRequest.put("redirect_uri", APPLE_WEBSITE_URL);

        return tokenRequest;
    }

    private JSONObject getTokenResponse(Map<String, String> tokenRequest) {
        Map response = doPostForTokens(tokenRequest);
        return new JSONObject(response);
    }

    private Map doPostForTokens(Map<String, String> tokenRequest) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        HttpEntity httpEntity = new HttpEntity(tokenRequest, httpHeaders);
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.postForEntity(AUTH_TOKEN_URL, httpEntity, Map.class);

        if (response.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
            throw new TodoaryException(APPLE_AUTHENTICATION_CODE_VALIDATION_FAILURE);
        }

        return response.getBody();
    }

    private PrivateKey getPrivateKey() {
        try {
            ClassPathResource resource = new ClassPathResource(KEY_PATH);

            InputStream is = resource.getInputStream();
            Reader pemReader = new InputStreamReader(is, StandardCharsets.UTF_8);
            PEMParser pemParser = new PEMParser(pemReader);

            JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
            PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
            return converter.getPrivateKey(object);
        } catch (IOException exception) {
            throw new TodoaryException(APPLE_Client_SECRET_ERROR);
        }
    }

    public String getProviderIdFrom(String idToken) {
        JSONObject jsonObject = decodeIdToken(idToken);
        return jsonObject.getAsString("sub");
    }

    public JSONObject decodeIdToken(String idToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(idToken);
            ReadOnlyJWTClaimsSet getPayload = signedJWT.getJWTClaimsSet();
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(getPayload.toJSONObject().toJSONString(), JSONObject.class);
        } catch (Exception exception) {
            throw new TodoaryException(INVALID_APPLE_AUTH);
        }
    }
}
