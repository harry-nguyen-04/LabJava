package vhuwng.lab.D3.dto;

import java.time.LocalDate;

public record SampleQueryFilterDto(String status, LocalDate from, LocalDate to) {}
