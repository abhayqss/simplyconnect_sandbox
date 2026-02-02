package com.scnsoft.eldermark.beans.reports.constants;

import java.util.regex.Pattern;

public class ArizonaMatrixConstants {

    public final static String COMMUNITY_NAME = "Community name";
    public final static String CLIENT_ID = "Client ID";
    public final static String CLIENT_NAME = "Client Name";
    public final static String DATE_OF_BIRTH = "Date of Birth";
    public final static String CASE_MANAGER = "Case manager";
    public final static String PROGRAM_NAME = "Program name";
    public final static String ASSESSMENT_TYPE = "Assessment type";
    public final static String SURVEY_FREQUENCY = "Survey frequency";
    public final static String ASSESSMENT_DATE = "Assessment date";
    public final static String ASSESSMENT_TOTAL_SCORE = "Assessment total score";
    public final static String INCOME = "Income";
    public final static String CREDIT_STATUS = "Credit Status";
    public final static String EMPLOYMENT = "Employment";
    public final static String SHELTER = "Shelter";
    public final static String FOOD = "Food";
    public final static String CHILD_CARE = "Child Care";
    public final static String KINDS_OF_CHILD_CARE = "What kind of childcare have you utilized in the past year?";
    public final static String CHILDREN_EDUCATION = "Children's Education";
    public final static String ADULT_EDUCATION = "Adult Education";
    public final static String HIGHEST_GRADE = "Highest Grade";
    public final static String LEGAL = "Legal";
    public final static String CONVICTED_OF = "Ever convicted of the following";
    public final static String CONVICTED_AND_CHARGED_WITH = "Ever convicted/charged with the following";
    public final static String IS_290_REGISTRANT = "290 Registrant?";
    public final static String HEALTH_CARE_COVERAGE = "Health Care Coverage";
    public final static String LIFE_SKILLS = "Life Skills";
    public final static String MENTAL_HEALTH = "Mental Health";
    public final static String SUBSTANCE_ABUSE = "Substance Abuse";
    public final static String FAMILY_AND_SOCIAL_RELATIONS = "Family/Social Relations";
    public final static String TRANSPORTATION = "Transportation";
    public final static String COMMUNITY_INVOLVEMENT = "Community Involvement";
    public final static String SAFETY = "Safety";
    public final static String GANG_AFFILIATION = "Gang Affiliation";
    public final static String PARENTING_SKILLS = "Parenting Skills";
    public final static String ACTIVE_CPS_CASE = "Active CPS Case?";
    public final static String PREVIOUS_CPS_INVOLVEMENT = "Previous CPS Involvement?";
    public final static String DISABILITIES = "Disabilities";

    public static final Pattern FOLLOW_UP_PATTERN = Pattern.compile("Follow Up - (\\d+) months");

    private ArizonaMatrixConstants() {
    }
}
