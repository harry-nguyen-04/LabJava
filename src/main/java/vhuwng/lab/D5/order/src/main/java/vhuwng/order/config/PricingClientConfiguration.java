package vhuwng.order.config;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import vhuwng.order.client.HttpPricingClient;
import vhuwng.order.client.PricingClient;
import vhuwng.order.client.ResilientPricingClient;

import java.time.Duration;

@Configuration
public class PricingClientConfiguration {

    @Bean
    SimpleClientHttpRequestFactory pricingRequestFactory(
            @Value("${pricing.timeout-ms:1000}") int timeoutMs
    ) {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(timeoutMs);
        requestFactory.setReadTimeout(timeoutMs);
        return requestFactory;
    }

    @Bean
    RestClient pricingRestClient(
            SimpleClientHttpRequestFactory requestFactory,
            @Value("${pricing.base-url:http://localhost:8081}") String baseUrl
    ) {
        return RestClient.builder()
                .baseUrl(baseUrl)
                .requestFactory(requestFactory)
                .build();
    }

    @Bean
    PricingClient pricingClient(
            RestClient pricingRestClient,
            @Value("${resilience4j.retry.instances.externalService.maxAttempts:3}") int maxAttempts,
            @Value("${resilience4j.retry.instances.externalService.waitDuration:100ms}") Duration retryWait,
            @Value("${resilience4j.circuitbreaker.instances.externalService.minimumNumberOfCalls:5}") int minimumNumberOfCalls,
            @Value("${resilience4j.circuitbreaker.instances.externalService.failureRateThreshold:50}") float failureRateThreshold,
            @Value("${resilience4j.circuitbreaker.instances.externalService.waitDurationInOpenState:10s}") Duration openStateWait
    ) {
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(maxAttempts)
                .waitDuration(retryWait)
                .retryExceptions(
                        ResourceAccessException.class,
                        HttpServerErrorException.class
                )
                .build();

        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .minimumNumberOfCalls(minimumNumberOfCalls)
                .failureRateThreshold(failureRateThreshold)
                .waitDurationInOpenState(openStateWait)
                .recordExceptions(
                        ResourceAccessException.class,
                        HttpServerErrorException.class
                )
                .build();

        return new ResilientPricingClient(
                new HttpPricingClient(pricingRestClient),
                Retry.of("externalService", retryConfig),
                CircuitBreaker.of("externalService", circuitBreakerConfig)
        );
    }
}
