package com.scnsoft.eldermark.jpa;

import org.springframework.transaction.TransactionStatus;

public interface TransactionListener {

    void afterTransactionBegin(TransactionStatus txStatus);

}
