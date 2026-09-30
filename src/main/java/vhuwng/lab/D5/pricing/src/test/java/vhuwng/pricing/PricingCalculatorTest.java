package vhuwng.pricing;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import vhuwng.pricing.dto.request.PricingLineRequest;
import vhuwng.pricing.dto.request.PricingQuoteRequest;
import vhuwng.pricing.dto.response.PricingQuoteResponse;
import vhuwng.pricing.service.PricingCalculator;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PricingCalculatorTest {

    private PricingCalculator calculator;

    @BeforeEach
    void setUp() {
        calculator = new PricingCalculator();
    }

    @Test
    void calculatesLineTotal() {
        PricingQuoteResponse result = calculator.calculate(request(0, 0,
                new PricingLineRequest(1, 2L, 10_000L)));

        assertThat(result.lines().getFirst().lineTotal()).isEqualTo(20_000L);
        assertThat(result.subtotal()).isEqualTo(20_000L);
    }

    @Test
    void sumsMultipleLineTotalsIntoSubtotal() {
        PricingQuoteResponse result = calculator.calculate(request(0, 0,
                new PricingLineRequest(1, 2L, 10_000L),
                new PricingLineRequest(2, 3L, 5_000L)));

        assertThat(result.lines()).extracting(line -> line.lineTotal())
                .containsExactly(20_000L, 15_000L);
        assertThat(result.subtotal()).isEqualTo(35_000L);
    }

    @Test
    void calculatesDiscountFromSubtotal() {
        PricingQuoteResponse result = calculator.calculate(request(10, 0,
                new PricingLineRequest(1, 2L, 10_000L)));

        assertThat(result.discountAmount()).isEqualTo(2_000L);
        assertThat(result.total()).isEqualTo(18_000L);
    }

    @Test
    void calculatesTaxAfterDiscount() {
        PricingQuoteResponse result = calculator.calculate(request(10, 8,
                new PricingLineRequest(1, 2L, 10_000L)));

        assertThat(result.taxAmount()).isEqualTo(1_440L);
        assertThat(result.total()).isEqualTo(19_440L);
    }

    @Test
    void emptyLinesProduceZeroAmounts() {
        PricingQuoteResponse result = calculator.calculate(new PricingQuoteRequest(10, 8, List.of()));

        assertThat(result.subtotal()).isZero();
        assertThat(result.discountAmount()).isZero();
        assertThat(result.taxAmount()).isZero();
        assertThat(result.total()).isZero();
        assertThat(result.lines()).isEmpty();
    }

    private PricingQuoteRequest request(int discountPercent, int taxPercent, PricingLineRequest... lines) {
        return new PricingQuoteRequest(discountPercent, taxPercent, List.of(lines));
    }
}
