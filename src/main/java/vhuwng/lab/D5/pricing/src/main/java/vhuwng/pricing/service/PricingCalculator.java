package vhuwng.pricing.service;

import java.util.List;

import org.springframework.stereotype.Service;

import vhuwng.pricing.dto.request.PricingQuoteRequest;
import vhuwng.pricing.dto.response.PricingLineResponse;
import vhuwng.pricing.dto.response.PricingQuoteResponse;

@Service
public class PricingCalculator {

    public PricingQuoteResponse calculate(PricingQuoteRequest request) {
        List<PricingLineResponse> lineTotals = request.lines().stream()
                .map(line -> new PricingLineResponse(
                        line.lineNo(),
                        Math.multiplyExact(line.quantity(), line.unitPrice())
                ))
                .toList();

        long subtotal = lineTotals.stream()
                .mapToLong(PricingLineResponse::lineTotal)
                .sum();

        long discountAmount = subtotal * request.discountPercent() / 100;
        long taxableAmount = subtotal - discountAmount;
        long taxAmount = taxableAmount * request.taxPercent() / 100;
        long total = taxableAmount + taxAmount;

        return new PricingQuoteResponse(
                subtotal,
                discountAmount,
                taxAmount,
                total,
                lineTotals
        );
    }
}
