package vhuwng.lab.D2.service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.TreeMap;

import org.springframework.stereotype.Service;

import vhuwng.lab.D2.dto.CountByStatusDto;
import vhuwng.lab.D2.dto.OrderDetailDto;
import vhuwng.lab.D2.dto.RevenueByCustomerDto;
import vhuwng.lab.D2.dto.RevenueByDayDto;
import vhuwng.lab.D2.dto.RevenueByDayItemDto;
import vhuwng.lab.D2.dto.SampleQueryDto;
import vhuwng.lab.D2.dto.SampleQueryFilterDto;
import vhuwng.lab.D2.dto.SumOfTotalByStatus;
import vhuwng.lab.D2.dto.TotalsDto;
import vhuwng.lab.D2.repository.IOrderRepository;

@Service
public class OrderTotalCalculator {

    private final IOrderRepository orderRepository;
    private final OrderUtil orderUtil;
    public OrderTotalCalculator(IOrderRepository orderRepository, OrderUtil orderUtil) {
        this.orderRepository = orderRepository;
        this.orderUtil = orderUtil;
    }

    public TotalsDto calcTotals(SampleQueryFilterDto filter) {
        List<OrderDetailDto> orders = orderRepository.getAllOrdersWithOptionalFilter(filter);
        long revenue = 0;
        int revenueOrderCount = 0;
        LocalDate firstOrderDate = null;
        LocalDate lastOrderDate = null;

        for (OrderDetailDto order : orders) {
            LocalDate orderDate = orderUtil.orderDate(order);

            if (firstOrderDate == null || orderDate.isBefore(firstOrderDate)) {
                firstOrderDate = orderDate;
            }
            if (lastOrderDate == null || orderDate.isAfter(lastOrderDate)) {
                lastOrderDate = orderDate;
            }

            if (orderUtil.isRevenueOrder(order)) {
                revenue += order.total();
                revenueOrderCount++;
            }
        }

        return new TotalsDto(
                orders.size(),
                revenue,
                revenueOrderCount,
                firstOrderDate == null ? null : firstOrderDate.toString(),
                lastOrderDate == null ? null : lastOrderDate.toString()
        );
    }

    public CountByStatusDto calcCountByStatus(SampleQueryFilterDto filter) {
        List<OrderDetailDto> orders = orderRepository.getAllOrdersWithOptionalFilter(filter);
        LinkedHashMap<String, Integer> countByStatus = new LinkedHashMap<>();
        for (OrderDetailDto order : orders) {
            countByStatus.merge(order.status(), 1, Integer::sum);
        }

        return new CountByStatusDto(
                countByStatus.getOrDefault("DRAFT", 0),
                countByStatus.getOrDefault("PENDING_PAYMENT", 0),
                countByStatus.getOrDefault("PAID", 0),
                countByStatus.getOrDefault("FULFILLED", 0),
                countByStatus.getOrDefault("CANCELLED", 0)
        );
    }

    public SumOfTotalByStatus calcSumOfTotalByStatus(SampleQueryFilterDto filter) {
        List<OrderDetailDto> orders = orderRepository.getAllOrdersWithOptionalFilter(filter);
        LinkedHashMap<String, Long> sumByStatus = new LinkedHashMap<>();
        for (OrderDetailDto order : orders) {
            sumByStatus.merge(order.status(), order.total(), Long::sum);
        }

        return new SumOfTotalByStatus(
                sumByStatus.getOrDefault("DRAFT", 0L),
                sumByStatus.getOrDefault("PENDING_PAYMENT", 0L),
                sumByStatus.getOrDefault("PAID", 0L),
                sumByStatus.getOrDefault("FULFILLED", 0L),
                sumByStatus.getOrDefault("CANCELLED", 0L)
        );
    }

    public RevenueByDayDto calcRevenueByDay(SampleQueryFilterDto filter) {
        List<OrderDetailDto> orders = orderRepository.getAllOrdersWithOptionalFilter(filter);
        TreeMap<LocalDate, RevenueByDayItemDto> revenueByDay = new TreeMap<>();

        for (OrderDetailDto order : orders) {
            if (!orderUtil.isRevenueOrder(order)) {
                continue;
            }

            LocalDate orderDate = orderUtil.orderDate(order);
            RevenueByDayItemDto current = revenueByDay.get(orderDate);
            long orderTotal = order.total();

            if (current == null) {
                revenueByDay.put(
                        orderDate,
                        new RevenueByDayItemDto(1, orderTotal)
                );
            } else {
                revenueByDay.put(
                        orderDate,
                        new RevenueByDayItemDto(
                                current.orders() + 1,
                                current.revenue() + orderTotal
                        )
                );
            }
        }

        return new RevenueByDayDto(revenueByDay);
    }

    public List<RevenueByCustomerDto> calcRevenueByCustomer(SampleQueryFilterDto filter) {
        List<OrderDetailDto> orders = orderRepository.getAllOrdersWithOptionalFilter(filter);
        LinkedHashMap<Integer, RevenueByCustomerDto> revenueByCustomer
                = new LinkedHashMap<>();

        for (OrderDetailDto order : orders) {
            if (!orderUtil.isRevenueOrder(order)) {
                continue;
            }

            int customerId = order.customerId();
            RevenueByCustomerDto current = revenueByCustomer.get(customerId);
            long orderTotal = order.total();

            if (current == null) {
                revenueByCustomer.put(
                        customerId,
                        new RevenueByCustomerDto(
                                customerId,
                                order.customerName(),
                                1,
                                orderTotal
                        )
                );
            } else {
                revenueByCustomer.put(
                        customerId,
                        new RevenueByCustomerDto(
                                customerId,
                                current.customerName(),
                                current.orders() + 1,
                                current.revenue() + orderTotal
                        )
                );
            }
        }

        return revenueByCustomer.values()
                .stream()
                .sorted(
                        Comparator.comparingLong(RevenueByCustomerDto::revenue)
                                .reversed()
                                .thenComparingInt(RevenueByCustomerDto::customerId)
                )
                .toList();
    }

    public SampleQueryDto calcSampleQuery(SampleQueryFilterDto filter) {
        List<OrderDetailDto> matchedOrders = orderRepository.getAllOrdersWithOptionalFilter(filter);

        return new SampleQueryDto(
                filter,
                matchedOrders.size(),
                matchedOrders.stream()
                        .mapToLong(OrderDetailDto::total)
                        .sum(),
                matchedOrders.stream()
                        .map(OrderDetailDto::code)
                        .toList()
        );
    }
}
