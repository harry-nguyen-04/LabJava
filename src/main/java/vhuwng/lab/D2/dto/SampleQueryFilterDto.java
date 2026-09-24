package vhuwng.lab.D2.dto;

import java.time.LocalDate;

public record SampleQueryFilterDto(String status, LocalDate from, LocalDate to) {}
