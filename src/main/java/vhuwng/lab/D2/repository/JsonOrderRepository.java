package vhuwng.lab.D2.repository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import vhuwng.lab.D2.dto.LineDto;
import vhuwng.lab.D2.dto.OrderDetailDto;
import vhuwng.lab.D2.dto.SampleQueryFilterDto;
import vhuwng.lab.D2.service.OrderUtil;

@Repository
public class JsonOrderRepository implements IOrderRepository {

    private String path = "datasets/orders.json";

    private final OrderUtil orderUtil;
    public JsonOrderRepository(OrderUtil orderUtil) {
        this.orderUtil = orderUtil;
    }

    @Override
    public List<OrderDetailDto> getAllOrdersWithOptionalFilter(SampleQueryFilterDto filter){
        String content;
        try {
            content = Files.readString(Path.of(path));
        } catch (IOException e) {
            throw new RuntimeException("Failed to read orders.json", e);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode orders;
        try {
            orders = objectMapper.readTree(content);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse orders.json", e);
        }
        List<OrderDetailDto> orderDetailList = new ArrayList<>();

        for (JsonNode order : orders) {
            int id = order.get("id").asInt();
            String code = order.get("code").asText();
            int customerId = order.get("customerId").asInt();
            String customerName = order.get("customerName").asText();
            int createdBy = order.get("createdBy").asInt();
            String status = order.get("status").asText();

            List<LineDto> lines = new ArrayList<>();

            for (JsonNode line : order.get("lines")) {

                lines.add(new LineDto(
                        line.get("lineNo").asInt(),
                        line.get("productId").asInt(),
                        line.get("sku").asText(),
                        line.get("productName").asText(),
                        line.get("quantity").asInt(),
                        line.get("unitPrice").asLong(),
                        line.get("lineTotal").asLong()
                ));
            }

            long subtotal = order.get("subtotal").asLong();
            int discountPercent = order.get("discountPercent").asInt();
            long discountAmount = order.get("discountAmount").asLong();
            int taxPercent = order.get("taxPercent").asInt();
            long taxAmount = order.get("taxAmount").asLong();
            long total = order.get("total").asLong();
            String currency = order.get("currency").asText();
            String createdAt = order.get("createdAt").asText();
            String updatedAt = order.get("updatedAt").asText();
            String paidAt = order.path("paidAt").textValue();
            String fulfilledAt = order.path("fulfilledAt").textValue();
            String cancelledAt = order.path("cancelledAt").textValue();

            orderDetailList.add(new OrderDetailDto(
                    id,
                    code,
                    customerId,
                    customerName,
                    createdBy,
                    status,
                    lines,
                    subtotal,
                    discountPercent,
                    discountAmount,
                    taxPercent,
                    taxAmount,
                    total,
                    currency,
                    createdAt,
                    updatedAt,
                    paidAt,
                    fulfilledAt,
                    cancelledAt
            ));
        }

        return orderDetailList
                .stream()
                .filter(order -> orderUtil.matches(order, filter))
                .toList();
    }
}
