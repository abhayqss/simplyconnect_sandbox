package com.scnsoft.eldermark.dao.projections;

import java.util.Date;

/**
 * Not used.
 * @see <a href="https://jira.spring.io/browse/DATAJPA-980">DATAJPA-980</a> Projections with native queries don't work as expected =(
 * @author phomal
 * Created on 11/16/2017.
 */
public interface VitalSignTypeAndObservation {
    String getType();
    Date getDate();
    Double getValue();
    String getUnit();
    Long getRn();
}
