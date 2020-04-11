package com.dream.configs;

import com.google.common.io.ByteStreams;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.nio.charset.Charset;

@Slf4j
public class RequestLoggingInterceptor implements ClientHttpRequestInterceptor {

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        long startTime = System.currentTimeMillis();
        log.info("\n Request method: {}, \n Request URI: {}, \n Request headers: {}, \n Request body: {}",
                request.getMethod(),
                request.getURI(),
                request.getHeaders(),
                new String(body, Charset.forName("UTF-8"))
        );
        ClientHttpResponse response = execution.execute(request, body);
        long duration = System.currentTimeMillis() - startTime;
        log.info("\n Request method: {}, \n Request URI: {}, \n Duration {}ms, \n Response status code: {}, \n Response body: {}",
                request.getMethod(),
                request.getURI(),
                duration,
                response.getStatusCode(),
                new String(ByteStreams.toByteArray(response.getBody()), Charset.forName("UTF-8"))
        );
        return response;
    }
}
