package com.scnsoft.eldermark.converter.dto2entity.organization;

import com.scnsoft.eldermark.dto.assessment.ClientAssessmentResultDto;
import com.scnsoft.eldermark.entity.assessment.AssessmentStatus;
import com.scnsoft.eldermark.entity.assessment.ClientAssessmentResult;
import com.scnsoft.eldermark.service.AssessmentService;
import com.scnsoft.eldermark.service.ClientAssessmentResultService;
import com.scnsoft.eldermark.service.ClientService;
import com.scnsoft.eldermark.service.security.LoggedUserService;
import com.scnsoft.eldermark.util.DateTimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Optional;

@Component
public class ClientAssessmentResultEntityConverter implements Converter<ClientAssessmentResultDto, ClientAssessmentResult> {

    @Autowired
    private AssessmentService assessmentService;

    @Autowired
    private ClientAssessmentResultService clientAssessmentResultService;

    @Autowired
    private LoggedUserService loggedUserService;

    @Autowired
    private ClientService clientService;

    //persistent entity should not be the result of this method because ServicePlan
    //is Auditable and setting id to null on persistent entity causes exception
    @Override
    @Transactional(readOnly = true)
    public ClientAssessmentResult convert(ClientAssessmentResultDto source) {
        ClientAssessmentResult clientAssessmentResult = Optional.ofNullable(source.getId())
                .map(clientAssessmentResultService::findById)
                .map(this::createCopyWithNecessaryFields)
                .orElseGet(() -> {
                    var assessmentResult = new ClientAssessmentResult();
                    assessmentResult.setAssessment(assessmentService.findById(source.getTypeId()).orElseThrow());
                    assessmentResult.setEmployee(loggedUserService.getCurrentEmployee());
                    assessmentResult.setClient(clientService.getById(source.getClientId()));
                    assessmentResult.setDateStarted(DateTimeUtils.toInstant(source.getDateAssigned()));
                    return assessmentResult;
                });

        clientAssessmentResult.setComment(source.getComment());
        clientAssessmentResult.setResult(source.getDataJson());
        clientAssessmentResult.setAssessmentStatus(source.getStatusName());

        updateDates(clientAssessmentResult, DateTimeUtils.toInstant(source.getDateCompleted()));

        Optional<Long> previousTimeToComplete =  Optional.ofNullable(clientAssessmentResult.getTimeToComplete());
        Optional<Long> editTime = Optional.ofNullable(source.getTimeToEdit());
        clientAssessmentResult.setTimeToComplete(previousTimeToComplete.orElse(0L) + editTime.orElse(0L));

        clientAssessmentResult.setHasErrors(source.getHasErrors());
        clientAssessmentResult.setServicePlanNeedIdentificationExcludedQuestions(source.getServicePlanNeedIdentificationExcludedQuestions());
        clientAssessmentResult.setServicePlanNeedIdentificationExcludedSections(source.getServicePlanNeedIdentificationExcludedSections());

        return clientAssessmentResult;
    }

    private void updateDates(ClientAssessmentResult clientAssessmentResult, Instant dateCompleted) {
        if (clientAssessmentResult.getAssessment().getDraftEnabled()) {
            if (clientAssessmentResult.getAssessmentStatus() == AssessmentStatus.COMPLETED) {
                clientAssessmentResult.setDateCompleted(Instant.now());
            } else if (clientAssessmentResult.getAssessmentStatus() == AssessmentStatus.IN_PROCESS) {
                clientAssessmentResult.setDateCompleted(null);
            }
        } else {
            clientAssessmentResult.setDateStarted(dateCompleted);
            clientAssessmentResult.setDateCompleted(dateCompleted);
        }
    }

    private ClientAssessmentResult createCopyWithNecessaryFields(ClientAssessmentResult assessmentResult) {
        var copy = new ClientAssessmentResult();
        copy.setId(assessmentResult.getId());

        copy.setChainId(assessmentResult.getChainId());
        copy.setAuditableStatus(assessmentResult.getAuditableStatus());
        copy.setArchived(assessmentResult.getArchived());

        copy.setClient(assessmentResult.getClient());
        copy.setEmployee(loggedUserService.getCurrentEmployee());
        copy.setAssessment(assessmentResult.getAssessment());
        copy.setDateStarted(assessmentResult.getDateStarted());
        copy.setDateCompleted(assessmentResult.getDateCompleted());
        copy.setTimeToComplete(assessmentResult.getTimeToComplete());

        return copy;
    }
}
