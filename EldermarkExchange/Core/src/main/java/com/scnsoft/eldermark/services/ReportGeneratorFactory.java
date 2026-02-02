package com.scnsoft.eldermark.services;

public interface ReportGeneratorFactory {
    ReportGenerator getGenerator(String documentType);
}
