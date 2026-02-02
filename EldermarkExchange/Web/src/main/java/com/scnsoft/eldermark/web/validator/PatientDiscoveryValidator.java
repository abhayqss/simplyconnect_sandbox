package com.scnsoft.eldermark.web.validator;


import com.scnsoft.eldermark.shared.ResidentFilterUiDto;
import com.scnsoft.eldermark.shared.SearchScope;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;


@Component
public class PatientDiscoveryValidator implements Validator {
    public @Value("${patient.discovery.ssn.required}") boolean ssnRequired;
    public @Value("${patient.discovery.dateOfBirth.required}") boolean dateOfBirthRequired;

    @Override
    public boolean supports(Class<?> paramClass) {
        return ResidentFilterUiDto.class.equals(paramClass);
    }

    @Override
    public void validate(Object obj, Errors errors) {
        ResidentFilterUiDto filter = (ResidentFilterUiDto) obj;
        if (filter.getSearchScopes().contains(SearchScope.ELDERMARK)  && ssnRequired) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "lastFourDigitsOfSsn", "field.required");
        }
        if (dateOfBirthRequired) {
            ValidationUtils.rejectIfEmptyOrWhitespace(errors, "dateOfBirth", "field.required");
        }
    }
}