package com.scnsoft.eldermark.service.pointclickcare;

import com.scnsoft.eldermark.dto.pointclickcare.filter.adt.PccAdtListFilter;
import com.scnsoft.eldermark.dto.pointclickcare.filter.patient.PCCPatientFilterExactMatchCriteria;
import com.scnsoft.eldermark.dto.pointclickcare.filter.patient.PCCPatientListFilter;
import com.scnsoft.eldermark.dto.pointclickcare.filter.webhook.PccWebhookSubscriptionListFilter;
import com.scnsoft.eldermark.dto.pointclickcare.model.adt.PccADTRecordDetailsList;
import com.scnsoft.eldermark.dto.pointclickcare.model.facility.PccFacilityDetails;
import com.scnsoft.eldermark.dto.pointclickcare.model.patient.PCCPatientDetails;
import com.scnsoft.eldermark.dto.pointclickcare.model.patient.PCCPatientMatchResponse;
import com.scnsoft.eldermark.dto.pointclickcare.model.patient.PccPatientList;
import com.scnsoft.eldermark.dto.pointclickcare.model.webhook.PccPostWebhookSubscription;
import com.scnsoft.eldermark.dto.pointclickcare.model.webhook.PccPostWebhookSubscriptionResponse;
import com.scnsoft.eldermark.dto.pointclickcare.model.webhook.PccPublicGetWebhookSubscriptionList;

interface PointClickCareApiGateway {

    PccFacilityDetails facilityById(String orgUuid, Long facilityId);

    PCCPatientDetails patientById(String orgUuid, Long patientId);

    PCCPatientMatchResponse patientMatch(String orgUuid, PCCPatientFilterExactMatchCriteria matchCriteria);

    PccPatientList listOfPatients(String organizationPccOrgUuid, PCCPatientListFilter filter, int page, int pageSize);

    PccPublicGetWebhookSubscriptionList listOfWebhookSubscriptions(PccWebhookSubscriptionListFilter filter, int page, int pageSize);

    PccPostWebhookSubscriptionResponse subscribeWebhook(PccPostWebhookSubscription body);

    PccADTRecordDetailsList adtList(String orgUuid, PccAdtListFilter filter, int page, int pageSize);
}
