package com.sixbell.sigas;

import io.opentracing.Tracer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/sigas")
public class SigasController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final RestTemplate restTemplate;

    @Value("${preferences.api.url:http://rios:8080}")
    private String remoteURL;

    @Autowired
    private Tracer tracer;

    public SigasController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @RequestMapping(value = "/establish", method = RequestMethod.PUT, consumes = "application/json")
    public ResponseEntity<String> receiveCall(@RequestBody String body) {
        try {
            return restTemplate.postForEntity(remoteURL+"/rios/establish", body, String.class);
        } catch (HttpStatusCodeException ex) {
            logger.warn("Exception trying to post to rios service.", ex);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(String.format("%d %s", ex.getRawStatusCode(), createHttpErrorResponseString(ex)));
        } catch (RestClientException ex) {
            logger.warn("Exception trying to post to rios service.", ex);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                    .body(ex.getMessage());
        }
    }

    @RequestMapping(value = "/registerFlow", method = RequestMethod.PUT, consumes = "application/json")
    public ResponseEntity<String> registerFlow(@RequestBody String body) {
        try {
            return restTemplate.postForEntity(remoteURL+"/rios/registerFlow", body, String.class);
        } catch (HttpStatusCodeException ex) {
            logger.warn("Exception trying to post to rios service.", ex);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(String.format("%d %s", ex.getRawStatusCode(), createHttpErrorResponseString(ex)));
        } catch (RestClientException ex) {
            logger.warn("Exception trying to post to rios service.", ex);
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(ex.getMessage());
        }
    }

    private String createHttpErrorResponseString(HttpStatusCodeException ex) {
        String responseBody = ex.getResponseBodyAsString().trim();
        if (responseBody.startsWith("null")) {
            return ex.getStatusCode().getReasonPhrase();
        }
        return responseBody;
    }

}
