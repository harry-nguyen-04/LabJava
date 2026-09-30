package vhuwng.order.client;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.retry.Retry;
import vhuwng.order.dto.request.PricingQuoteRequest;
import vhuwng.order.dto.response.PricingQuoteResponse;

public class ResilientPricingClient implements PricingClient {

    private final PricingClient delegate;
    private final Retry retry;
    private final CircuitBreaker circuitBreaker;

    public ResilientPricingClient(
            PricingClient delegate,
            Retry retry,
            CircuitBreaker circuitBreaker
    ) {
        this.delegate = delegate;
        this.retry = retry;
        this.circuitBreaker = circuitBreaker;
    }

    @Override
    public PricingQuoteResponse quote(PricingQuoteRequest request) {
        try {
            return retry.executeSupplier(() ->
                    circuitBreaker.executeSupplier(() -> delegate.quote(request))
            );
        } catch (RuntimeException exception) {
            return fallback(request, exception);
        }
    }

    PricingQuoteResponse fallback(PricingQuoteRequest request, RuntimeException exception) {
        return PricingQuoteResponse.pending(request);
    }
}
