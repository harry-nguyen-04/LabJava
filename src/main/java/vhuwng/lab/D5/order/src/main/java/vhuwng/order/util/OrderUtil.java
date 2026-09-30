package vhuwng.order.util;

import vhuwng.order.client.PricingClient;
import vhuwng.order.dto.request.PricingLineRequest;
import vhuwng.order.dto.request.PricingQuoteRequest;
import vhuwng.order.dto.response.OrderLineResponse;
import vhuwng.order.dto.response.OrderResponse;
import vhuwng.order.dto.response.PricingLineResponse;
import vhuwng.order.dto.response.PricingQuoteResponse;
import vhuwng.order.dto.response.PricingStatus;
import vhuwng.order.entity.OrderEntity;
import vhuwng.order.middleware.exception.OrderNotFoundException;
import vhuwng.order.repository.OrderRepository;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class OrderUtil {

    private OrderUtil() {
    }

    public static OrderEntity findOrder(OrderRepository orderRepository, Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new OrderNotFoundException(id));
    }

    public static OrderResponse toResponse(
            OrderEntity order,
            boolean requestPricing,
            PricingClient pricingClient
    ) {
        PricingQuoteRequest request = toPricingRequest(order);
        PricingQuoteResponse quote = requestPricing
                ? safeQuote(pricingClient, request)
                : PricingQuoteResponse.pending(request);
        Map<Integer, Long> lineTotals = quote.lines() == null
                ? Map.of()
                : quote.lines().stream()
                .filter(line -> line.lineTotal() != null)
                .collect(Collectors.toMap(
                        PricingLineResponse::lineNo,
                        PricingLineResponse::lineTotal,
                        (first, ignored) -> first
                ));

        PricingStatus pricingStatus = quote.total() == null
                ? PricingStatus.PENDING
                : PricingStatus.READY;

        List<OrderLineResponse> lines = order.getLines().stream()
                .map(line -> new OrderLineResponse(
                        line.getLineNo(),
                        line.getProductId(),
                        line.getSku(),
                        line.getProductName(),
                        line.getQuantity().longValue(),
                        line.getUnitPrice(),
                        pricingStatus == PricingStatus.READY ? lineTotals.get(line.getLineNo()) : null
                ))
                .toList();

        return new OrderResponse(
                order.getId(),
                order.getCode(),
                order.getCustomerId(),
                order.getCustomerName(),
                order.getCreatedBy(),
                order.getStatus(),
                lines,
                pricingStatus,
                quote.subtotal(),
                order.getDiscountPercent().intValue(),
                quote.discountAmount(),
                order.getTaxPercent().intValue(),
                quote.taxAmount(),
                quote.total(),
                order.getCurrency(),
                order.getCreatedAt(),
                order.getUpdatedAt(),
                order.getPaidAt(),
                order.getFulfilledAt(),
                order.getCancelledAt()
        );
    }

    public static OffsetDateTime now() {
        return OffsetDateTime.now(ZoneOffset.ofHours(7));
    }

    private static PricingQuoteResponse safeQuote(
            PricingClient pricingClient,
            PricingQuoteRequest request
    ) {
        try {
            PricingQuoteResponse quote = pricingClient.quote(request);
            return quote == null ? PricingQuoteResponse.pending(request) : quote;
        } catch (RuntimeException exception) {
            return PricingQuoteResponse.pending(request);
        }
    }

    private static PricingQuoteRequest toPricingRequest(OrderEntity order) {
        return new PricingQuoteRequest(
                order.getDiscountPercent().intValue(),
                order.getTaxPercent().intValue(),
                order.getLines().stream()
                        .map(line -> new PricingLineRequest(
                                line.getLineNo(),
                                line.getQuantity().longValue(),
                                line.getUnitPrice()
                        ))
                        .toList()
        );
    }
}
