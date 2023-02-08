/*
 * Copyright (c) 2023 Picture Soluções em TI - All Rights Reserved
 */

package br.com.picture.demo365mail;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

class OAuth {
    static OAuthResponse getToken(String url, String clientId, String clientSecret, String scope, String tenant) {
        HttpResponse<String> response;

        Map<String, String> param = new HashMap<>();
        param.put("client_id", clientId);
        param.put("scope", scope);
        param.put("client_secret", clientSecret);
        param.put("grant_type", "client_credentials");

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI(String.format("%s/%s/oauth2/v2.0/token", url, tenant)))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .timeout(Duration.of(10, ChronoUnit.SECONDS))
                    .POST(HttpRequest.BodyPublishers.ofString(toFormData(param)))
                    .build();

            HttpClient client = HttpClient.newBuilder()
                    .followRedirects(HttpClient.Redirect.ALWAYS)
                    .connectTimeout(Duration.of(5, ChronoUnit.SECONDS))
                    .build();

            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new AuthenticationException(e);
        }

        OAuthResponse accessToken = null;

        if(response.statusCode() >= 200 && response.statusCode() < 400) {
            String body = response.body();
            ObjectMapper objectMapper = new ObjectMapper();
            try {
                accessToken = objectMapper.readValue(body, OAuthResponse.class);
            } catch (JsonProcessingException e) {
                throw new AuthenticationException(e);
            }
        } else {
            throw new AuthenticationException(String.format("HTTP STATUS %d: %s", response.statusCode(), response.body()));
        }

        return accessToken;
    }

    private static String toFormData(Map<String, String> data) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, String> e : data.entrySet()) {
            if (sb.length() > 0) {
                sb.append("&");
            }
            sb.append(URLEncoder.encode(e.getKey(), StandardCharsets.UTF_8));
            sb.append("=");
            sb.append(URLEncoder.encode(e.getValue(), StandardCharsets.UTF_8));
        }

        return sb.toString();
    }

}
