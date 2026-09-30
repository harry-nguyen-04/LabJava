package vhuwng.order.client;

import vhuwng.order.dto.request.PricingQuoteRequest;
import vhuwng.order.dto.response.PricingQuoteResponse;

public interface PricingClient {

    PricingQuoteResponse quote(PricingQuoteRequest request);
}
