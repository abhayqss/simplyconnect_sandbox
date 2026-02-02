package com.scnsoft.eldermark.dump.model;

public class RawComprehensiveAssessmentDump extends RawAssessmentDump<RawComprehensiveAssessmentDumpEntry> {
    @Override
    public DumpType getDumpType() {
        return DumpType.RAW_COMPREHENSIVE_ASSESSMENT;
    }
}
