package com.jumia.phonenumbersapi.IT;

import com.jumia.phonenumbersapi.model.PhoneNumbersResponseModel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.util.Assert;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.text.MessageFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static com.jumia.phonenumbersapi.utils.TestUtil.isEven;
@Slf4j
public class TestRestTemplate {

    private static final String NAME= "admin";
    private static final String PASSWORD = "admin";
    private static final String CONTEXT = "phone-numbers-api";
    private static final String HOST = "localhost";
    private static final String PORT = "8080";
    private static final String API_ROOT = "api/v1/rest";
    private static final String HEALTH_CHECK_URL = "http://localhost:8080/phone-numbers-api/actuator/health";
    private static final String DATA_CHECK_URL = "http://localhost:8080/phone-numbers-api/api/v1/rest/customerPhonesNumbers?page=0&size=10";
    private static final String URI_VARIABLE_REGEX = "\\{[a-zA-Z]*\\}";

    private static final RestOperations restTemplate = new RestTemplate();

    public static Boolean healthCheckCall() {
        HealthCheck healthCheck;
        try {
            healthCheck = restTemplate.getForEntity(HEALTH_CHECK_URL, HealthCheck.class).getBody();
            assert healthCheck != null;
        } catch (Exception e) {
            return false;
        }
        return healthCheck.getStatus().equals("UP");
    }
    public static Boolean testCall() {
        PhoneNumbersResponseModel response;
        try {
            response =  restTemplate.exchange(DATA_CHECK_URL,HttpMethod.GET,new HttpEntity<>(createHttpHeaders(true)), PhoneNumbersResponseModel.class).getBody();
            assert response != null;
        } catch (Exception e) {
            return false;
        }
        return true;
    }
    public static <T> ResponseEntity<?> restCall(final String uri, final HttpMethod method, final Map<String,String> queryParameters,
                                                 final Class<?> responseType, final T requestBody, final Boolean withAuth, String... uriVariables) {
        switch (method) {
            case GET:
                return doGet(uri, queryParameters, responseType, withAuth, uriVariables);
            case POST:
                return doPost(uri, queryParameters, requestBody, responseType, withAuth, uriVariables);
            case PUT:
                return doPut(uri, queryParameters, requestBody, responseType, withAuth, uriVariables);
            default:
                return doDelete(uri, queryParameters, requestBody, responseType, withAuth, uriVariables);
        }
    }

    private static <T> ResponseEntity<?> doGet(final String uri, final Map<String,String> queryParameters,
                                               final Class<?> responseType, final Boolean withAuth, String... uriVariables) {
        final HttpEntity<T> headers = new HttpEntity<>(createHttpHeaders(withAuth));
        return restTemplate.exchange(uriBuilder(uri,queryParameters, uriVariables),HttpMethod.GET,headers,responseType);
    }

    private static <T> ResponseEntity<?> doPost(final String uri, final Map<String,String> queryParameters,
                                                final T requestBody, final Class<?> responseType, final Boolean withAuth, String... uriVariables) {
        final HttpEntity<T> requestEntity = new HttpEntity<>(requestBody, createHttpHeaders(withAuth));
        return restTemplate.exchange(uriBuilder(uri,queryParameters, uriVariables),HttpMethod.POST,requestEntity,responseType);
    }

    private static <T> ResponseEntity<?> doPut(final String uri, final Map<String,String> queryParameters,
                                               final T requestBody, final Class<?> responseType, final Boolean withAuth, String... uriVariables) {
        final HttpEntity<T> requestEntity = new HttpEntity<>(requestBody, createHttpHeaders(withAuth));
        return restTemplate.exchange(uriBuilder(uri,queryParameters, uriVariables),HttpMethod.PUT,requestEntity,responseType);
    }

    private static <T> ResponseEntity<?> doDelete(final String uri, final Map<String,String> queryParameters,
                                                  final T requestBody, final Class<?> responseType, final Boolean withAuth, String... uriVariables) {
        final HttpEntity<T> requestEntity = new HttpEntity<>(requestBody, createHttpHeaders(withAuth));
        return restTemplate.exchange(uriBuilder(uri,queryParameters, uriVariables),HttpMethod.DELETE,requestEntity,responseType);
    }

    private static URI uriBuilder(final String URI, final Map<String,String> queryParameters,  String... uriVariables) {
        final StringBuilder baseOfURI = new StringBuilder(10).append(uriBuilder(URI, uriVariables));
        String params = "";
        if (!queryParameters.isEmpty()) {
            baseOfURI.append("?");
            final Set<String> keys = queryParameters.keySet();
            params = keys.stream().map(key -> MessageFormat.format("{0}={1}", key, queryParameters.get(key)))
                    .collect(Collectors.joining("&"));
        }
        return UriComponentsBuilder.fromUriString(baseOfURI.append(params).toString()).build().encode().toUri();
    }

    private static String uriBuilder(final String URI, String... uriVariables) {
        final String baseOfURI = MessageFormat.format("{0}:{1}/{2}/{3}", HOST, PORT, CONTEXT, API_ROOT);
        return MessageFormat.format("http://{0}{1}",baseOfURI, applyUriVariables(URI, uriVariables));
    }

    private static String applyUriVariables(final String URI, String... uriVariables) {
        StringJoiner uriJoiner = new StringJoiner("/");
        int uriVariablePointer = 0;
        for(String path: URI.split("/")) {
            if(isUriVariable(path)){
                uriJoiner.add(uriVariables[uriVariablePointer]);
                uriVariablePointer++;
            } else {
                uriJoiner.add(path);
            }
        }
        return uriJoiner.toString();
    }

    private static HttpHeaders createHttpHeaders(Boolean withAuth) {
        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        if(withAuth) {
            final String notEncoded = NAME + ":" + PASSWORD;
            final String encodedAuth = "Basic " + Base64.getEncoder().encodeToString(notEncoded.getBytes());
            headers.add("Authorization", encodedAuth);
        }
        return headers;
    }
    public static Map<String, String> prepareParams(String... keyValuePairs) {
        ArrayList<String> keyValuePairList = new ArrayList<>(Arrays.asList(keyValuePairs));
        Assert.isTrue(isEven(keyValuePairList.size()), "[Assertion failed] - keyValuePairs length must be even");
        Map<String, String> queryParams = new HashMap<>();
        String key,value;
        while(!keyValuePairList.isEmpty()) {
            if(isEven(keyValuePairList.size())) {
                value = keyValuePairList.remove(keyValuePairList.size() - 1);
                key = keyValuePairList.remove(keyValuePairList.size() - 1);
                queryParams.put(key, value);
            }
        }
        return queryParams;
    }

    public static Boolean isUriVariable (String path) {
        Pattern pattern = Pattern.compile(URI_VARIABLE_REGEX);
        Matcher matcher = pattern.matcher(path);
        return matcher.matches();
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    private static class HealthCheck {
        private static final long serialVersionUID = 1L;
        private String status;
    }
}
