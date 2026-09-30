package vhuwng.pricing.controller;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vhuwng.pricing.dto.request.PricingQuoteRequest;
import vhuwng.pricing.dto.response.PricingQuoteResponse;
import vhuwng.pricing.service.PricingCalculator;

@RestController
@RequestMapping("/api/pricing")
public class PricingController {

    private final PricingCalculator pricingCalculator;

    public PricingController(PricingCalculator pricingCalculator) {
        this.pricingCalculator = pricingCalculator;
    }

    @PostMapping("/quote")
    public PricingQuoteResponse quote(@Valid @RequestBody PricingQuoteRequest request) {
        return pricingCalculator.calculate(request);
    }
}
