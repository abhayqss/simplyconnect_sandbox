package com.scnsoft.eldermark.service.palatiumcare;

import com.scnsoft.eldermark.entity.palatiumcare.AlertStatus;

public class IncorrectAlertFlowException extends Exception {

    public IncorrectAlertFlowException(AlertStatus from, AlertStatus to) {
        super("Incorrect alert flow exception! Transition from " + from.toString() + " to " + to.toString()  + " is impossible");
    }
}
