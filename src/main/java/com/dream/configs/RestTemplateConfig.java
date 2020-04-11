package com.dream.configs;

import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import javax.net.ssl.SSLContext;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;

@Configuration
public class RestTemplateConfig {

    static int TIMEOUT = 60000;

    @Bean
    public RestTemplate platformRestTemplate(RestTemplateBuilder restTemplateBuilder) throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
        RequestLoggingInterceptor interceptor = new RequestLoggingInterceptor();

        TrustStrategy acceptingTrustStrategy = (X509Certificate[] chain, String authType) -> true;

        SSLContext sslContext = SSLContexts.custom()
                .loadTrustMaterial(null, acceptingTrustStrategy)
                .build();

        SSLConnectionSocketFactory csf = new SSLConnectionSocketFactory(sslContext);

        CloseableHttpClient httpClient = HttpClients.custom()
                .setSSLSocketFactory(csf)
                .build();

        HttpComponentsClientHttpRequestFactory clientHttpRequestFactory =
                new HttpComponentsClientHttpRequestFactory();

        clientHttpRequestFactory.setConnectTimeout(TIMEOUT);
        clientHttpRequestFactory.setReadTimeout(TIMEOUT);
        clientHttpRequestFactory.setConnectionRequestTimeout(TIMEOUT);

        clientHttpRequestFactory.setHttpClient(httpClient);

        BufferingClientHttpRequestFactory bufferingClientHttpRequestFactory = new BufferingClientHttpRequestFactory(clientHttpRequestFactory);
        return restTemplateBuilder.requestFactory(() -> bufferingClientHttpRequestFactory)
                .interceptors(interceptor)
                .build();
    }
}
