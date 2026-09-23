package vhuwng.lab.D1.dto;

import java.time.LocalDate;

public record SampleQueryFilterDto(String status, LocalDate from, LocalDate to) {}
