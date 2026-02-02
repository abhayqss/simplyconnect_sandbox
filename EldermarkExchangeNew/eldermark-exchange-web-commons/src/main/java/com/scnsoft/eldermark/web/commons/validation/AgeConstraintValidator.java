package com.scnsoft.eldermark.web.commons.validation;

import org.apache.commons.lang.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.function.BiFunction;

public class AgeConstraintValidator implements ConstraintValidator<Age, LocalDate> {
    public static final int NO_DATA = -1;
    private static final Logger logger = LoggerFactory.getLogger(AgeConstraintValidator.class);
    private Age constraintAnnotation;

    @Override
    public void initialize(Age constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
        this.constraintAnnotation = constraintAnnotation;
        if (constraintAnnotation.value() == NO_DATA || ArrayUtils.isEmpty(constraintAnnotation.rules())) {
            throw new RuntimeException("Illegal use of Age validation");
        }
    }

    @Override
    public boolean isValid(LocalDate localDate, ConstraintValidatorContext constraintValidatorContext) {
        logger.debug("Age validation of {}", localDate);
        if (localDate == null) {
            return true;
        }

        var nowOffset = LocalDate.now(constraintValidatorContext.getClockProvider().getClock())
                .minusYears(constraintAnnotation.value());
        return Arrays.stream(constraintAnnotation.rules()).anyMatch(rule -> rule.test(nowOffset, localDate));
    }


    public enum Rule {
        GREATER_THAN((nowOffset, actual) -> actual.isBefore(nowOffset)),
        LESS_THAN((nowOffset, actual) -> actual.isAfter(nowOffset)),
        EQUAL((nowOffset, actual) -> nowOffset.isEqual(actual));

        private final BiFunction<LocalDate, LocalDate, Boolean> checker;

        Rule(BiFunction<LocalDate, LocalDate, Boolean> checker) {
            this.checker = checker;
        }


        boolean test(LocalDate nowOffset, LocalDate actual) {
            return checker.apply(nowOffset, actual);
        }
    }

}
