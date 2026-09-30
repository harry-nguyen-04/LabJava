package vhuwng.order.client;

import org.springframework.web.client.RestClient;
import org.slf4j.MDC;
import vhuwng.order.middleware.filter.CorrelationIdFilter;
import vhuwng.order.dto.request.PricingQuoteRequest;
import vhuwng.order.dto.response.PricingQuoteResponse;

public class HttpPricingClient implements PricingClient {

    private final RestClient restClient;

    public HttpPricingClient(RestClient restClient) {
        this.restClient = restClient;
    }

    @Override
    public PricingQuoteResponse quote(PricingQuoteRequest request) {
        var requestSpec = restClient.post()
                .uri("/api/pricing/quote")
                .body(request);

        String correlationId = MDC.get(CorrelationIdFilter.MDC_KEY);
        if (correlationId != null) {
            requestSpec.header(CorrelationIdFilter.HEADER_NAME, correlationId);
        }

        return requestSpec.retrieve()
                .body(PricingQuoteResponse.class);
    }
}
