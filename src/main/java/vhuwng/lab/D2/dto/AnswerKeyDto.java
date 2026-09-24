package vhuwng.lab.D2.dto;

import java.util.List;
import java.util.Map;

public record AnswerKeyDto(
        DefinitionsDto definitions,
        TotalsDto totals,
        Map<String, Integer> countByStatus,
        Map<String, Long> sumOfTotalByStatus,
        Map<String, RevenueByDayDto> revenueByDay,
        List<RevenueByCustomerDto> revenueByCustomer,
        List<SampleQueryDto> sampleQueries) {}
