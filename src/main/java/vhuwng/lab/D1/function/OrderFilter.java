package vhuwng.lab.D1.function;

import vhuwng.lab.D1.dto.OrderDetailDto;
import vhuwng.lab.D1.dto.SampleQueryFilterDto;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;

public final class OrderFilter {

    private OrderFilter() {
    }

    public static List<OrderDetailDto> apply(
            List<OrderDetailDto> orders,
            SampleQueryFilterDto filter
    ) {
        return orders.stream()
                .filter(order -> matches(order, filter))
                .toList();
    }

    private static boolean matches(
            OrderDetailDto order,
            SampleQueryFilterDto filter
    ) {
        if (filter == null) {
            return true;
        }

        if (filter.status() != null && !filter.status().equals(order.status())) {
            return false;
        }

        LocalDate orderDate = OffsetDateTime
                .parse(order.createdAt())
                .toLocalDate();

        if (filter.from() != null && orderDate.isBefore(filter.from())) {
            return false;
        }

        return filter.to() == null || !orderDate.isAfter(filter.to());
    }
}
