package vhuwng.order.client;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import org.junit.jupiter.api.Test;
import vhuwng.order.dto.request.PricingLineRequest;
import vhuwng.order.dto.request.PricingQuoteRequest;
import vhuwng.order.dto.response.PricingQuoteResponse;

import java.time.Duration;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;

class ResilientPricingClientTest {

    @Test
    void retriesRemoteFailureThenReturnsPendingFallback() {
        PricingClient delegate = mock(PricingClient.class);
        PricingQuoteRequest request = new PricingQuoteRequest(
                10,
                8,
                List.of(new PricingLineRequest(1, 2L, 10_000L))
        );
        when(delegate.quote(request)).thenThrow(new RuntimeException("Pricing down"));

        Retry retry = Retry.of("test", RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ZERO)
                .retryExceptions(RuntimeException.class)
                .build());
        CircuitBreaker circuitBreaker = CircuitBreaker.of("test", CircuitBreakerConfig.custom()
                .minimumNumberOfCalls(10)
                .recordExceptions(RuntimeException.class)
                .build());

        PricingQuoteResponse result = new ResilientPricingClient(delegate, retry, circuitBreaker)
                .quote(request);

        assertThat(result.total()).isNull();
        assertThat(result.lines()).hasSize(1);
        verify(delegate, times(3)).quote(request);
    }
}
