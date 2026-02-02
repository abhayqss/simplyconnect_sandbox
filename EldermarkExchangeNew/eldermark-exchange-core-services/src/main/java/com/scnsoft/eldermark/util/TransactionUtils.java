package com.scnsoft.eldermark.util;

import org.springframework.transaction.NoTransactionException;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.interceptor.TransactionAspectSupport;

public class TransactionUtils {

    public static TransactionStatus getCurrentTransaction() {
        try {
            return TransactionAspectSupport.currentTransactionStatus();
        } catch (NoTransactionException ex) {
            return null;
        }
    }
}
