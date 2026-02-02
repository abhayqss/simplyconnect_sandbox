package com.scnsoft.eldermark.dto.healthpartners;

import java.time.LocalDate;

public interface HpCsvRecord {
    String getMemberIdentifier();

    String getMemberFirstName();

    String getMemberMiddleName();

    String getMemberLastName();

    LocalDate getDateOfBirth();

    void setLineNumber(int lineNumber);
}
