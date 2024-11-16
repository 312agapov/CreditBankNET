package ru.agapovla.Calculator.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.agapovla.Calculator.enums.Theme;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class EmailMessage {
    public String address;
    public Theme theme;
    public Long statementId;
    public String text;
}
