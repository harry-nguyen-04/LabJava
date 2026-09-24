package vhuwng.lab.D2.dto;

import java.time.LocalDate;
import java.util.TreeMap;

public record RevenueByDayDto(
        TreeMap<LocalDate, RevenueByDayItemDto> revenueByDay
) {}
