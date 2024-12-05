package ru.agapovla.Deal.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.agapovla.Deal.enums.ChangeType;
import ru.agapovla.Deal.enums.Status;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatementStatusHistoryDto {
    public Status status;
    public Timestamp time;
    public ChangeType changeType;
}
