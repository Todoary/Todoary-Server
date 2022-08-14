package com.todoary.ms.src.auth;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.JWSSigner;
import com.nimbusds.jose.crypto.ECDSASigner;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.ReadOnlyJWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.todoary.ms.util.BaseException;
import com.todoary.ms.util.BaseResponseStatus;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;
import net.minidev.json.JSONObject;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMParser;
import org.bouncycastle.openssl.jcajce.JcaPEMKeyConverter;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.InvalidKeyException;
import java.security.PrivateKey;
import java.security.interfaces.ECPrivateKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Slf4j
@Component
public class AppleUtil {

    @Value("${apple.publickey-url}")
    private String APPLE_PUBLIC_KEYS_URL;

    @Value("${apple.iss}")
    private String ISS;

    @Value("${apple.aud}")
    private String AUD;

    @Value("${apple.team-id}")
    private String TEAM_ID;

    @Value("${apple.key-id}")
    private String KEY_ID;

    @Value("${apple.key-path}")
    private String KEY_PATH;

    @Value("${apple.token-url}")
    private String AUTH_TOKEN_URL;

    @Value("${apple.website-url}")
    private String APPLE_WEBSITE_URL;

    private static ObjectMapper objectMapper = new ObjectMapper();

    public String createClientSecret() throws IOException {
        Date expirationDate = Date.from(LocalDateTime.now().plusDays(30).atZone(ZoneId.systemDefault()).toInstant());
        Map<String, Object> jwtHeader = new HashMap<>();
        jwtHeader.put("kid", KEY_ID);
        jwtHeader.put("alg", "ES256");

        return Jwts.builder()
                .setHeaderParams(jwtHeader)
                .setIssuer(TEAM_ID)
                .setIssuedAt(new Date(System.currentTimeMillis())) // 발행 시간 - UNIX 시간
                .setExpiration(expirationDate) // 만료 시간
                .setAudience(ISS)
                .setSubject(AUD)
                .signWith(SignatureAlgorithm.ES256, getPrivateKey())
                .compact();
    }

    private PrivateKey getPrivateKey() throws IOException {
        PEMParser pemParser = null;
        ClassPathResource resource = new ClassPathResource(KEY_PATH);
        try (FileReader keyReader = new FileReader(resource.getFile());
             PemReader pemReader = new PemReader(keyReader)) {
            {
                pemParser = new PEMParser(pemReader);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        JcaPEMKeyConverter converter = new JcaPEMKeyConverter();
        PrivateKeyInfo object = (PrivateKeyInfo) pemParser.readObject();
        return converter.getPrivateKey(object);
    }

    public String doPost(String url, Map<String, String> param) {
        String result = null;
        CloseableHttpClient httpclient = null;
        CloseableHttpResponse response = null;
        Integer statusCode = null;
        String reasonPhrase = null;
        try {
            httpclient = HttpClients.createDefault();
            HttpPost httpPost = new HttpPost(url);
            httpPost.addHeader("Content-Type", "application/x-www-form-urlencoded");
            List<NameValuePair> nvps = new ArrayList<>();
            Set<Map.Entry<String, String>> entrySet = param.entrySet();
            for (Map.Entry<String, String> entry : entrySet) {
                String fieldName = entry.getKey();
                String fieldValue = entry.getValue();
                nvps.add(new BasicNameValuePair(fieldName, fieldValue));
            }
            UrlEncodedFormEntity formEntity = new UrlEncodedFormEntity(nvps);
            httpPost.setEntity(formEntity);
            response = httpclient.execute(httpPost);
            statusCode = response.getStatusLine().getStatusCode();
            reasonPhrase = response.getStatusLine().getReasonPhrase();
            HttpEntity entity = response.getEntity();
            result = EntityUtils.toString(entity, "UTF-8");

            if (statusCode != 200) {
                log.error("[error] : " + result);
            }
            EntityUtils.consume(entity);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpclient != null) {
                    httpclient.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public JSONObject decodeFromIdToken(String id_token) {

        try {
            SignedJWT signedJWT = SignedJWT.parse(id_token);
            ReadOnlyJWTClaimsSet getPayload = signedJWT.getJWTClaimsSet();
            ObjectMapper objectMapper = new ObjectMapper();
            JSONObject payload = objectMapper.readValue(getPayload.toJSONObject().toJSONString(), JSONObject.class);
            if (payload != null) {
                return payload;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public JSONObject  validateAuthorizationGrantCode(String client_secret, String code) {

        Map<String, String> tokenRequest = new HashMap<>();

        tokenRequest.put("client_id", AUD);
        tokenRequest.put("client_secret", client_secret);
        tokenRequest.put("code", code);
        tokenRequest.put("grant_type", "authorization_code");
        tokenRequest.put("redirect_uri", APPLE_WEBSITE_URL);

        return getTokenResponse(tokenRequest);
    }

    private JSONObject  getTokenResponse(Map<String, String> tokenRequest) {

        try {
            String response = doPost(AUTH_TOKEN_URL, tokenRequest);
            ObjectMapper objectMapper = new ObjectMapper();
            JSONObject  tokenResponse = objectMapper.readValue(response, JSONObject .class);

            if (tokenRequest != null) {
                return tokenResponse;
            }
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }
}