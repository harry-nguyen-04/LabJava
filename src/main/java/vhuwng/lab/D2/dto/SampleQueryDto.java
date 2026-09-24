package vhuwng.lab.D2.dto;

import java.util.List;

public record SampleQueryDto(
        SampleQueryFilterDto filter,
        Integer orderCount,
        Long sumOfTotal,
        List<String> orderCodes) {}
