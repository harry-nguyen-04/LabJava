package vhuwng.lab.D3.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import vhuwng.lab.D3.dto.OrderDetailDto;
import vhuwng.lab.D3.dto.SampleQueryFilterDto;

import java.time.LocalDate;
import java.time.OffsetDateTime;

public class OrderUtil {
    public boolean matches(OrderDetailDto order, SampleQueryFilterDto filter) {
        if (filter == null) {
            return true;
        }

        if (filter.status() != null && !filter.status().equals(order.status())) {
            return false;
        }

        LocalDate orderDate = orderDate(order);

        if (filter.from() != null && orderDate.isBefore(filter.from())) {
            return false;
        }

        return filter.to() == null || !orderDate.isAfter(filter.to());
    }

    public boolean isRevenueOrder(OrderDetailDto order) {
        return "PAID".equals(order.status()) || "FULFILLED".equals(order.status());
    }

    public LocalDate orderDate(OrderDetailDto order) {
        return OffsetDateTime.parse(order.createdAt()).toLocalDate();
    }

    public String prettyJson(Object obj){
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(obj);
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }
    }
}
