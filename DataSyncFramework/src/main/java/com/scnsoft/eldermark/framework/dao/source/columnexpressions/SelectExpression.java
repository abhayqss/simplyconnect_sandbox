package com.scnsoft.eldermark.framework.dao.source.columnexpressions;

public interface SelectExpression {
    String getValue();

    boolean isEscapingNeeded();
}
