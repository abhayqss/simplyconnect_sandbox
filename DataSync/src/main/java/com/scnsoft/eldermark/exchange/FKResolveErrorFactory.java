package com.scnsoft.eldermark.exchange;

import com.scnsoft.eldermark.framework.fk.FKResolveError;

public class FKResolveErrorFactory {
    private final String sourceTable;
    private final Object sourceRecordId;

    public FKResolveErrorFactory(String sourceTable, Object sourceRecordId) {
        this.sourceTable = sourceTable;
        this.sourceRecordId = sourceRecordId;
    }

    public FKResolveError newInvalidSourceFkError(String referencedSourceTable, Object referencedSourceRecordId) {
        StringBuilder sb = new StringBuilder();
        sb.append("Record \"").append(sourceTable).append("#")
                .append(sourceRecordId.toString()).append("\" refers to record \"")
                .append(referencedSourceTable).append("#").append(referencedSourceRecordId)
                .append("\", but corresponding record doesn't exist in the SimplyConnect system database. ")
                .append("This is either a violation of foreign key constraint or a datasync service issue.");
        return new FKResolveError(sb.toString());
    }

    public FKResolveError newInvalidCcdIdError(String ccdIdColumn, long ccdIdValue) {
        StringBuilder sb = new StringBuilder();
        sb.append("Field \"").append(ccdIdColumn).append("\" in record \"").append(sourceTable).append("#")
                .append(sourceRecordId.toString()).append("\" contains CCDID value \"").append(ccdIdValue)
                .append("\", but corresponding code doesn't exist in the SimplyConnect system database.")
                .append("It means that either the CCDID value is invalid or SimplyConnect system CCD-codes table is incomplete.");
        return new FKResolveError(sb.toString());
    }
}