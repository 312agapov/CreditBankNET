package ru.agapovla.Deal.entity;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class Passport {
    private UUID passport_id;
    private String series;
    private String number;
    private String issue_branch;
    private LocalDate issue_date;
}
