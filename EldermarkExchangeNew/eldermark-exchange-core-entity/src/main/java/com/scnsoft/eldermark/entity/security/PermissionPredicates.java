package com.scnsoft.eldermark.entity.security;

import com.scnsoft.eldermark.entity.Employee;
import org.apache.commons.lang3.BooleanUtils;

import java.util.function.Predicate;

public class PermissionPredicates {

    public static Predicate<Employee> QA_INCIDENT_REPORTS = employee -> BooleanUtils.isTrue(employee.getQaIncidentReports());
    public static Predicate<Employee> LABS_COORDINATOR = Employee::getLabsCoordinator;
    public static Predicate<Employee> PAPERLESS_HEALTHCARE = employee -> BooleanUtils.isTrue(employee.getOrganization().getIsPaperlessHealthcareEnabled());
    public static Predicate<Employee> RELEASE_NOTES_ENABLED = employee -> BooleanUtils.isTrue(employee.getOrganization().getAreReleaseNotesEnabled());
}
