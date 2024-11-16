package ru.agapovla.Calculator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.agapovla.Calculator.enums.ChangeType;
import ru.agapovla.Calculator.enums.Status;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class StatementStatusHistoryDto {
    public Status status;
    public LocalDateTime time;
    public ChangeType changeType;
}
