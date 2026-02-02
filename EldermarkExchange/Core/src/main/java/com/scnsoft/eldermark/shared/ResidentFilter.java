package com.scnsoft.eldermark.shared;

import com.scnsoft.eldermark.entity.Database;
import com.scnsoft.eldermark.shared.administration.MatchStatus;
import com.scnsoft.eldermark.shared.administration.MergeStatus;
import com.scnsoft.eldermark.shared.administration.SearchMode;

import java.util.Date;

public interface ResidentFilter {
    String getFirstName();

    String getLastName();

    String getMiddleName();

    Gender getGender();

    Date getDateOfBirth();

    String getPhone();

    String getEmail();

    Database getDatabase();

    String getCity();

    String getState();

    String getPostalCode();

    String getSsn();

    String getLastFourDigitsOfSsn();

    String getStreet();

    /**
     * Use SearchMode.MATCH_ALL for joining predicates with conjunction (AND) in constructed SQL query.
     * Use SearchMode.MATCH_ANY for joining predicates with disjunction (OR) in constructed SQL query.
     *
     * Default mode is SearchMode.MATCH_ALL.
     */
    SearchMode getMode();

    /**
     * MatchStatus, if set, is mandatory condition. It's joined to other predicates with conjunction (AND) in despite of SearchMode.
     */
    MatchStatus getMatchStatus();

    /**
     * MergeStatus, if set, is mandatory condition. It's joined to other predicates with conjunction (AND) in despite of SearchMode.
     */
    MergeStatus getMergeStatus();

    String getProviderOrganization();

    String getCommunity();

}
