package vhuwng.lab.D1;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import vhuwng.lab.D1.dto.*;
import vhuwng.lab.D1.function.CalcAnswer;
import vhuwng.lab.D1.function.OrderFilter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {
    static String DEFAULT_PATH = "datasets/orders.json";

    public static void main(String[] args) throws IOException {
        String qStatus = null;
        LocalDate qFrom = null;
        LocalDate qTo = null;
        String dataset = DEFAULT_PATH;

        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "--status" -> qStatus = args[++i];
                case "--from" -> qFrom = LocalDate.parse(args[++i]);
                case "--to" -> qTo = LocalDate.parse(args[++i]);
                case "--dataset" -> dataset = args[++i];
            }
        }
        String content = Files.readString(Path.of(dataset));

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode orders = objectMapper.readTree(content);
        ArrayList<OrderDetailDto> orderDetailList = new ArrayList<>();

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

        SampleQueryFilterDto filter = new SampleQueryFilterDto(qStatus, qFrom, qTo);
        List<OrderDetailDto> reportOrders = OrderFilter.apply(orderDetailList, filter);

        CalcAnswer calcAnswer = new CalcAnswer();
        TotalsDto totals = calcAnswer.calcTotals(reportOrders);
        CountByStatusDto countByStatus = calcAnswer.calcCountByStatus(reportOrders);
        SumOfTotalByStatus sumOfTotalByStatus =
                calcAnswer.calcSumOfTotalByStatus(reportOrders);
        RevenueByDayDto revenueByDay = calcAnswer.calcRevenueByDay(reportOrders);
        List<RevenueByCustomerDto> revenueByCustomer =
                calcAnswer.calcRevenueByCustomer(reportOrders);
        SampleQueryDto sampleQuery = calcAnswer.calcSampleQuery(orderDetailList, filter);

        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(totals));
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(countByStatus));
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(sumOfTotalByStatus));
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(revenueByDay));
        System.out.println(objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(revenueByCustomer));
        System.out.printf(
                "sampleQuery: status=%s, from=%s, to=%s, orderCount=%d, "
                        + "sumOfTotal=%d, orderCodes=%s%n",
                sampleQuery.filter().status(),
                sampleQuery.filter().from(),
                sampleQuery.filter().to(),
                sampleQuery.orderCount(),
                sampleQuery.sumOfTotal(),
                sampleQuery.orderCodes()
        );
    }
}