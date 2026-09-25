package vhuwng.lab.D3;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import vhuwng.lab.D3.dto.OrderDetailDto;
import vhuwng.lab.D3.dto.SampleQueryFilterDto;
import vhuwng.lab.D3.dto.ShipmentResponseDto;
import vhuwng.lab.D3.repository.JsonOrderRepository;
import vhuwng.lab.D3.service.OrderUtil;

public class Lab3 {
    private static final Duration SHIPMENT_TIMEOUT = Duration.ofSeconds(1);
    private static final String SHIPMENT_URL = "http://127.0.0.1:3001/shipments/";

    public static void main(String[] args) throws IOException, InterruptedException {
        String status = null;
        LocalDate from = null;
        LocalDate to = null;
        for (int i = 0; i < args.length; i++){
            switch (args[i]) {
                case "--status" -> status = args[++i];
                case "--from" -> from = LocalDate.parse(args[++i]);
                case "--to" -> to = LocalDate.parse(args[++i]);
            }
        }

        HttpClient client = HttpClient.newBuilder()
                .connectTimeout(SHIPMENT_TIMEOUT)
                .build();
        ObjectMapper objectMapper = new ObjectMapper();

        SampleQueryFilterDto filter = new SampleQueryFilterDto(status, from, to);

        OrderUtil orderUtil = new OrderUtil();
        JsonOrderRepository dataset = new JsonOrderRepository(orderUtil);
        List<OrderDetailDto> orders = dataset.getAllOrdersWithOptionalFilter(filter);

        // Group orders by customer ID
        Map<Integer, List<OrderDetailDto>> ordersByCustomerId = orders.stream()
                .collect(
                        Collectors.groupingBy(OrderDetailDto::customerId)
                );

        ordersByCustomerId.forEach((customerId, customerOrders) -> {
            String name = customerOrders.getFirst().customerName();
            System.out.printf("%s (%d): %d orders%n", name, customerId, customerOrders.size());
        });

        // Get top 20 orders by status
        List<OrderDetailDto> ordersByStatus = orders.stream()
                .limit(20)
                .toList();

        System.out.println("Top 20 orders by status:");
        ordersByStatus.forEach(System.out::println);

        long sequentialMs = fetchShipmentsSequentially(client, objectMapper, ordersByStatus);
        long concurrentMs = fetchShipmentsConcurrently(client, objectMapper, ordersByStatus);
        System.out.printf("sequential: %d ms%n", sequentialMs);
        System.out.printf("concurrent: %d ms%n", concurrentMs);
    }

    private static long fetchShipmentsSequentially(
            HttpClient client,
            ObjectMapper objectMapper,
            List<OrderDetailDto> orders
    ) {
        System.out.println("Sequential shipments:");
        long start = System.nanoTime();
        for (OrderDetailDto order : orders) {
            try {
                reportShipment(order, queryShipment(client, objectMapper, order));
            } catch (Exception e) {
                reportFailure(order, e);
            }
        }
        return elapsedMillis(start);
    }

    private static long fetchShipmentsConcurrently(
            HttpClient client,
            ObjectMapper objectMapper,
            List<OrderDetailDto> orders
    ) throws InterruptedException {
        System.out.println("Concurrent shipments:");
        long start = System.nanoTime();
        try (var executor = Executors.newVirtualThreadPerTaskExecutor()) {
            List<Future<ShipmentResponseDto>> futures = orders.stream()
                    .map(order -> executor.submit(() -> queryShipment(client, objectMapper, order)))
                    .toList();

            for (int i = 0; i < futures.size(); i++) {
                OrderDetailDto order = orders.get(i);
                try {
                    reportShipment(order, futures.get(i).get());
                } catch (ExecutionException e) {
                    reportFailure(order, e.getCause() == null ? e : e.getCause());
                }
            }
        }
        return elapsedMillis(start);
    }

    private static ShipmentResponseDto queryShipment(
            HttpClient client,
            ObjectMapper objectMapper,
            OrderDetailDto order
    ) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(SHIPMENT_URL + order.id()))
                .timeout(SHIPMENT_TIMEOUT)
                .GET()
                .build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("Shipment status is null");
        }
        return objectMapper.readValue(response.body(), ShipmentResponseDto.class);
    }

    private static void reportShipment(OrderDetailDto order, ShipmentResponseDto shipment) {
        System.out.printf(
                "%s shipment=%s carrier=%s tracking=%s%n",
                order.code(),
                shipment.status(),
                shipment.carrier(),
                shipment.trackingNumber()
        );
    }

    private static void reportFailure(OrderDetailDto order, Throwable error) {
        if (error instanceof HttpTimeoutException) {
            System.out.printf(
                    "%s TIMEOUT after %s: %s%n",
                    order.code(),
                    SHIPMENT_TIMEOUT,
                    error.getMessage()
            );
            return;
        }
        System.out.printf("%s FAILED: %s%n", order.code(), error.getMessage());
    }

    private static long elapsedMillis(long startNanos) {
        return (System.nanoTime() - startNanos) / 1_000_000;
    }
}
