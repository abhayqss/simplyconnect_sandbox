package com.scnsoft.eldermark.service.pointclickcare;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.scnsoft.eldermark.dto.pointclickcare.PointClickCareApiException;
import com.scnsoft.eldermark.dto.pointclickcare.filter.adt.PccAdtListFilter;
import com.scnsoft.eldermark.dto.pointclickcare.filter.adt.PccAdtRecordType;
import com.scnsoft.eldermark.dto.pointclickcare.filter.patient.PCCPatientFilterExactMatchCriteria;
import com.scnsoft.eldermark.dto.pointclickcare.filter.patient.PCCPatientListFilter;
import com.scnsoft.eldermark.dto.pointclickcare.filter.webhook.PccWebhookSubscriptionListFilter;
import com.scnsoft.eldermark.dto.pointclickcare.model.PCCPagingResponseByPage;
import com.scnsoft.eldermark.dto.pointclickcare.model.PccApplicationType;
import com.scnsoft.eldermark.dto.pointclickcare.model.patient.PCCPatientDetails;
import com.scnsoft.eldermark.dto.pointclickcare.model.webhook.PccOrgWebhookSubscriptionStatusState;
import com.scnsoft.eldermark.dto.pointclickcare.model.webhook.PccPostWebhookSubscription;
import com.scnsoft.eldermark.dto.pointclickcare.model.webhook.PccWebhookSubscriptionAction;
import com.scnsoft.eldermark.dto.pointclickcare.model.webhook.PccWebhookSubscriptionStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.support.RestGatewaySupport;

import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

class PointClickCareApiGatewayImplTest {

    private final static String HOST = "http://localhost:8080";
    private final static String ORG_UUID = "1750abe8-1b7c-4e26-969c-9d7076ff8c02";
    private final static Long PATIENT_ID = 1L;

    private final static String FACILITY_RESPONSE = "{\n" +
            "  \"active\": true,\n" +
            "  \"addressLine1\": \"389 Skidmore Road\",\n" +
            "  \"addressLine2\": \"P.O.Box # 21678-9087\",\n" +
            "  \"assessmentsWithCustomScheduling\": [\n" +
            "    {\n" +
            "      \"defaultFrequency\": 7,\n" +
            "      \"templateAssessId\": 42\n" +
            "    },\n" +
            "    {\n" +
            "      \"defaultFrequency\": 5,\n" +
            "      \"templateAssessId\": 41\n" +
            "    }\n" +
            "  ],\n" +
            "  \"bedCount\": 150,\n" +
            "  \"billingStyleCountry\": \"USA\",\n" +
            "  \"city\": \"Crockett\",\n" +
            "  \"clinicalConfiguration\": {\n" +
            "    \"progressNote\": {\n" +
            "      \"signature\": \"password\"\n" +
            "    },\n" +
            "    \"units\": {\n" +
            "      \"bloodPressure\": \"mmHg\",\n" +
            "      \"bloodSugar\": \"mg/dL\",\n" +
            "      \"heartRate\": \"bpm\",\n" +
            "      \"height\": \"Cm\",\n" +
            "      \"oxygenSaturation\": \"%\",\n" +
            "      \"respirations\": \"Breaths/min\",\n" +
            "      \"temperature\": \"°C\",\n" +
            "      \"weight\": \"Kg\"\n" +
            "    },\n" +
            "    \"weightScaleTypes\": [\n" +
            "      \"Standing\",\n" +
            "      \"Wheelchair\",\n" +
            "      \"Hoyer\",\n" +
            "      \"BATH\",\n" +
            "      \"SITTING\",\n" +
            "      \"Mechanical lift\"\n" +
            "    ]\n" +
            "  },\n" +
            "  \"contentDirectoryBrandTierConfiguration\": [\n" +
            "    {\n" +
            "      \"brandDescription\": \"Infection Prevention and Control\",\n" +
            "      \"brandName\": \"PCC Infection Control solution\",\n" +
            "      \"confValue\": \"Y\",\n" +
            "      \"currentStatusOption\": {\n" +
            "        \"enableOption\": false,\n" +
            "        \"name\": \"Disabled\",\n" +
            "        \"readonly\": false,\n" +
            "        \"statusId\": 4\n" +
            "      },\n" +
            "      \"enabled\": true,\n" +
            "      \"enabledBy\": \"systemuser\",\n" +
            "      \"enabledDate\": \"2020-09-17\",\n" +
            "      \"revisionBy\": \"systemuser\",\n" +
            "      \"revisionDate\": \"2020-09-17 00:11:37\",\n" +
            "      \"sourceConfValue\": \"Y\",\n" +
            "      \"tier\": \"INFECTION_PREVENTION_AND_CONTROL\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"brandDescription\": \" eINTERACT&trade;  Program for Skilled Nursing Facilities (SNF)\",\n" +
            "      \"brandName\": \"eINTERACT&trade; \",\n" +
            "      \"confValue\": \"N\",\n" +
            "      \"disabledBy\": \"systemuser\",\n" +
            "      \"disabledDate\": \"2020-09-17\",\n" +
            "      \"enabled\": false,\n" +
            "      \"revisionBy\": \"systemuser\",\n" +
            "      \"revisionDate\": \"2020-09-17 00:11:37\",\n" +
            "      \"sourceConfValue\": \"N\",\n" +
            "      \"tier\": \"EINTERACT\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"careContentDirectoryConfiguration\": {\n" +
            "    \"canadianCareContent\": false,\n" +
            "    \"clinicalStandardContent\": true,\n" +
            "    \"connectedCareCenter\": false,\n" +
            "    \"eInteract\": false,\n" +
            "    \"nursingAdvantage\": false,\n" +
            "    \"pccSkinAndWound\": false,\n" +
            "    \"thinkResearch\": true,\n" +
            "    \"woundRounds\": true\n" +
            "  },\n" +
            "  \"country\": \"USA\",\n" +
            "  \"emailAddress\": \"info@pcc.com\",\n" +
            "  \"environment\": \"www7\",\n" +
            "  \"episodeOfCareEnabled\": false,\n" +
            "  \"facId\": 50,\n" +
            "  \"facilityCode\": \"6640\",\n" +
            "  \"facilityName\": \"FACILITY_50\",\n" +
            "  \"facilityStatus\": \"Live\",\n" +
            "  \"facilityTerms\": {\n" +
            "    \"admission\": \"Admission\",\n" +
            "    \"assessment\": \"Assessment\",\n" +
            "    \"bed\": \"Bed\",\n" +
            "    \"discharge\": \"Discharge\",\n" +
            "    \"facility\": \"Community\",\n" +
            "    \"focus\": \"Focus\",\n" +
            "    \"goal\": \"Goal\",\n" +
            "    \"intervention\": \"Intervention\",\n" +
            "    \"kardex\": \"Kardex\",\n" +
            "    \"patient\": \"Participant\",\n" +
            "    \"room\": \"Room\",\n" +
            "    \"task\": \"Task\"\n" +
            "  },\n" +
            "  \"fax\": \"(678) 555-3246\",\n" +
            "  \"generalConfiguration\": {\n" +
            "    \"assessment\": {\n" +
            "      \"signature\": \"password\"\n" +
            "    }\n" +
            "  },\n" +
            "  \"headOffice\": false,\n" +
            "  \"healthType\": \"SNF\",\n" +
            "  \"isOmpiEnabled\": true,\n" +
            "  \"isTrackingHiePrivacyConsent\": true,\n" +
            "  \"isUploadEnabled\": true,\n" +
            "  \"lineOfBusiness\": {\n" +
            "    \"longDesc\": \"Skilled Nursing Facility\",\n" +
            "    \"shortDesc\": \"SNF\"\n" +
            "  },\n" +
            "  \"orderAdministrationConfiguration\": {\n" +
            "    \"allowedSupplementaryDocumentationResponses\": [\n" +
            "      \"LAST_RECORDED\",\n" +
            "      \"NEW_VALUE\"\n" +
            "    ],\n" +
            "    \"hasOrderAdministrationBarcodeScanning\": true,\n" +
            "    \"hasResidentBarcodeScanning\": true,\n" +
            "    \"orderAdministrationBarcodeRegex\": \"\\\\d{0,11}$\",\n" +
            "    \"requireProgressNoteforPRN\": false,\n" +
            "    \"shouldAutoPopulateProgressNote\": true,\n" +
            "    \"showProgressNoteOn24HourReport\": true,\n" +
            "    \"showProgressNoteOnShiftReport\": true,\n" +
            "    \"signature\": \"password\",\n" +
            "    \"usePharmacyDirections\": false\n" +
            "  },\n" +
            "  \"orderConfiguration\": {\n" +
            "    \"defaultMedicationLibrary\": 4,\n" +
            "    \"defaultMedicationLibraryDescription\": \"MediSpanAPI\",\n" +
            "    \"epcsEnabled\": true,\n" +
            "    \"pdmpEnabled\": true,\n" +
            "    \"quantityAndRefillRequired\": false\n" +
            "  },\n" +
            "  \"orgName\": \"A.G. Rhodes Home\",\n" +
            "  \"orgUuid\": \"1750abe8-1b7c-4e26-969c-9d7076ff8c02\",\n" +
            "  \"phone\": \"(936) 555-9890\",\n" +
            "  \"pickList\": {\n" +
            "    \"censusActionCode\": [\n" +
            "      {\n" +
            "        \"actionCode\": \"AA\",\n" +
            "        \"actionType\": \"Actual Admit\",\n" +
            "        \"id\": 1,\n" +
            "        \"standardActionType\": \"Admission\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"actionCode\": \"L\",\n" +
            "        \"actionType\": \"Leave of Absence/LOA\",\n" +
            "        \"id\": 2,\n" +
            "        \"standardActionType\": \"Leave\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"actionCode\": \"RAA\",\n" +
            "        \"actionType\": \"Respite - Actual Admit/ReAdmit\",\n" +
            "        \"id\": 3,\n" +
            "        \"standardActionType\": \"Admission\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"citizenship\": [\n" +
            "      {\n" +
            "        \"description\": \"Canadian\",\n" +
            "        \"id\": 1\n" +
            "      }\n" +
            "    ],\n" +
            "    \"contactRelationships\": [\n" +
            "      {\n" +
            "        \"description\": \"Aunt\",\n" +
            "        \"id\": 1\n" +
            "      },\n" +
            "      {\n" +
            "        \"description\": \"Brother\",\n" +
            "        \"id\": 2\n" +
            "      },\n" +
            "      {\n" +
            "        \"description\": \"Child\",\n" +
            "        \"id\": 3\n" +
            "      },\n" +
            "      {\n" +
            "        \"description\": \"Cousin\",\n" +
            "        \"id\": 4\n" +
            "      },\n" +
            "      {\n" +
            "        \"description\": \"Daughter\",\n" +
            "        \"id\": 5\n" +
            "      }\n" +
            "    ],\n" +
            "    \"contactTypes\": [\n" +
            "      {\n" +
            "        \"description\": \"A/R Guarantor\",\n" +
            "        \"guarantor\": true,\n" +
            "        \"id\": 1,\n" +
            "        \"isSurveyContact\": false,\n" +
            "        \"standardContactType\": \"Agent\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"description\": \"Next of Kin\",\n" +
            "        \"guarantor\": false,\n" +
            "        \"id\": 2,\n" +
            "        \"isSurveyContact\": true,\n" +
            "        \"standardContactType\": \"Next of Kin\"\n" +
            "      }\n" +
            "    ],\n" +
            "    \"documentCategories\": [\n" +
            "      {\n" +
            "        \"description\": \"Clinical Document\",\n" +
            "        \"id\": 1,\n" +
            "        \"isCustom\": false\n" +
            "      },\n" +
            "      {\n" +
            "        \"description\": \"Other\",\n" +
            "        \"id\": 2,\n" +
            "        \"isCustom\": true\n" +
            "      }\n" +
            "    ],\n" +
            "    \"language\": [\n" +
            "      {\n" +
            "        \"description\": \"English\",\n" +
            "        \"id\": 1\n" +
            "      },\n" +
            "      {\n" +
            "        \"description\": \"French\",\n" +
            "        \"id\": 2\n" +
            "      }\n" +
            "    ],\n" +
            "    \"maritalStatus\": [\n" +
            "      {\n" +
            "        \"description\": \"Married\",\n" +
            "        \"id\": 1\n" +
            "      }\n" +
            "    ],\n" +
            "    \"prefix\": [\n" +
            "      {\n" +
            "        \"description\": \"Dr.\",\n" +
            "        \"id\": 1\n" +
            "      },\n" +
            "      {\n" +
            "        \"description\": \"Mr.\",\n" +
            "        \"id\": 2\n" +
            "      },\n" +
            "      {\n" +
            "        \"description\": \"Mrs.\",\n" +
            "        \"id\": 3\n" +
            "      },\n" +
            "      {\n" +
            "        \"description\": \"Ms.\",\n" +
            "        \"id\": 4\n" +
            "      }\n" +
            "    ],\n" +
            "    \"race\": [\n" +
            "      {\n" +
            "        \"description\": \"Hispanic\",\n" +
            "        \"id\": 1\n" +
            "      }\n" +
            "    ],\n" +
            "    \"religion\": [\n" +
            "      {\n" +
            "        \"description\": \"Catholic\",\n" +
            "        \"id\": 1\n" +
            "      }\n" +
            "    ],\n" +
            "    \"suffix\": [\n" +
            "      {\n" +
            "        \"description\": \"Jr\",\n" +
            "        \"id\": 1\n" +
            "      },\n" +
            "      {\n" +
            "        \"description\": \"Sr\",\n" +
            "        \"id\": 2\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"postalCode\": \"75835-1810\",\n" +
            "  \"reportFilters\": [\n" +
            "    {\n" +
            "      \"items\": [\n" +
            "        {\n" +
            "          \"reportFilterItemId\": 1\n" +
            "        }\n" +
            "      ],\n" +
            "      \"reportFilterId\": 1\n" +
            "    }\n" +
            "  ],\n" +
            "  \"serviceConfiguration\": {\n" +
            "    \"allowDocumentationPeriod\": 5,\n" +
            "    \"dueNowPeriod\": 5,\n" +
            "    \"isBatchUpdateEnabled\": true,\n" +
            "    \"lateEntryPeriod\": 1,\n" +
            "    \"overduePeriod\": 5,\n" +
            "    \"patientCarePlan\": {\n" +
            "      \"applicableToCareGivers\": true,\n" +
            "      \"id\": -52,\n" +
            "      \"type\": \"CARE_PLAN\"\n" +
            "    },\n" +
            "    \"standardBehaviorTrackingLevel\": \"CATEGORY\",\n" +
            "    \"standardResponses\": [\n" +
            "      {\n" +
            "        \"code\": \"-99\",\n" +
            "        \"description\": \"Resident Not Available\",\n" +
            "        \"isResponseForFollowUpQuestions\": true,\n" +
            "        \"isResponseForMDSQuestions\": true\n" +
            "      },\n" +
            "      {\n" +
            "        \"code\": \"-98\",\n" +
            "        \"description\": \"Resident Refused\",\n" +
            "        \"isResponseForFollowUpQuestions\": true,\n" +
            "        \"isResponseForMDSQuestions\": true\n" +
            "      },\n" +
            "      {\n" +
            "        \"code\": \"-97\",\n" +
            "        \"description\": \"Not Applicable\",\n" +
            "        \"isResponseForFollowUpQuestions\": true,\n" +
            "        \"isResponseForMDSQuestions\": true\n" +
            "      }\n" +
            "    ],\n" +
            "    \"timeTracking\": true,\n" +
            "    \"trackUnscheduledTasks\": true\n" +
            "  },\n" +
            "  \"state\": \"TX\",\n" +
            "  \"timeZone\": \"America/Chicago\"\n" +
            "}";

    private final static String PATIENT_RESPONSE = "{\n" +
            "  \"admissionDate\": \"2005-12-06\",\n" +
            "  \"bedDesc\": \"A\",\n" +
            "  \"bedId\": 201,\n" +
            "  \"birthDate\": \"1919-05-03\",\n" +
            "  \"birthPlace\": \"Denver\",\n" +
            "  \"canadianHCN\": {\n" +
            "    \"coding\": [\n" +
            "      {\n" +
            "        \"system\": \"https://simplifier.net/canadianuriregistry/ca-on-patient-hcn\",\n" +
            "        \"code\": 3243982726\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"citizenship\": \"U.S.\",\n" +
            "  \"createdBy\": \"PCC-bhojaa\",\n" +
            "  \"createdDate\": \"2003-10-08 04:00:00+21:20\",\n" +
            "  \"deathDateTime\": \"2010-01-30T05:00:00.000Z\",\n" +
            "  \"deceased\": true,\n" +
            "  \"desiredBooking\": {\n" +
            "    \"dateRequested\": \"2018-05-03\",\n" +
            "    \"desiredLocation\": 1,\n" +
            "    \"desiredMoveDate\": \"2018-06-03\"\n" +
            "  },\n" +
            "  \"dischargeDate\": \"2010-01-30\",\n" +
            "  \"email\": \"jadr@haddo.eu\",\n" +
            "  \"ethnicityDesc\": \"Black, not of Hispanic origin\",\n" +
            "  \"ethnicityCode\": {\n" +
            "    \"codings\": [\n" +
            "      {\n" +
            "        \"system\": \"http://snomed.info/sct\",\n" +
            "        \"version\": \"1\",\n" +
            "        \"code\": \"2186-5\",\n" +
            "        \"display\": \"Not Hispanic or Latino\"\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"facId\": 1,\n" +
            "  \"firstName\": \"James\",\n" +
            "  \"floorDesc\": \"2nd\",\n" +
            "  \"floorId\": 3,\n" +
            "  \"gender\": \"MALE\",\n" +
            "  \"hasPhoto\": false,\n" +
            "  \"homePhone\": \"(475) 529-8541\",\n" +
            "  \"ituPhone\": \"(475) 529 8541\",\n" +
            "  \"languageCode\": \"en\",\n" +
            "  \"languageDesc\": \"English\",\n" +
            "  \"lastName\": \"Bond\",\n" +
            "  \"legalMailingAddress\": {\n" +
            "    \"addressLine1\": \"565 Stratton Building\",\n" +
            "    \"addressLine2\": \"696 Roderick Avenue\",\n" +
            "    \"city\": \"ATLANTA\",\n" +
            "    \"country\": \"U.S.\",\n" +
            "    \"county\": \"Dekalb\",\n" +
            "    \"postalCode\": \"30306\",\n" +
            "    \"state\": \"GA\"\n" +
            "  },\n" +
            "  \"maidenName\": \"Born\",\n" +
            "  \"maritalStatus\": \"Widowed\",\n" +
            "  \"medicaidNumber\": \"SCRUBBED_50582\",\n" +
            "  \"medicalRecordNumber\": \"ALC3442\",\n" +
            "  \"medicareBeneficiaryIdentifier\": \"1254778\",\n" +
            "  \"medicareNumber\": \"581185786V\",\n" +
            "  \"occupation\": \"Farmer\",\n" +
            "  \"ompId\": 878885,\n" +
            "  \"orgUuid\": \"1750abe8-1b7c-4e26-969c-9d7076ff8c02\",\n" +
            "  \"outpatient\": false,\n" +
            "  \"patientContacts\": [\n" +
            "    {\n" +
            "      \"addressLine1\": \"433 Waldgrave Building\",\n" +
            "      \"addressLine2\": \"690 Dixie Street\",\n" +
            "      \"addressLine3\": \"856 Heaton Lane\",\n" +
            "      \"cellPhone\": \"(404) 555-9658\",\n" +
            "      \"city\": \"Phoenix\",\n" +
            "      \"comments\": \"Comments about this patient contact.\",\n" +
            "      \"contactId\": 159279,\n" +
            "      \"contactType\": [\n" +
            "        \"A/R Guarantor\",\n" +
            "        \"Alternate Contact\"\n" +
            "      ],\n" +
            "      \"country\": \"United States\",\n" +
            "      \"county\": \"Maricopa\",\n" +
            "      \"email\": \"mayme.ali@msn.com\",\n" +
            "      \"firstName\": \"Mayme\",\n" +
            "      \"gender\": \"MALE\",\n" +
            "      \"homePhone\": \"(678) 555-8675\",\n" +
            "      \"isSurveyContact\": true,\n" +
            "      \"lastName\": \"Ali\",\n" +
            "      \"namePrefix\": \"Dr.\",\n" +
            "      \"officePhone\": \"(678) 555-5555\",\n" +
            "      \"postalCode\": \"85004\",\n" +
            "      \"relationship\": \"Son\",\n" +
            "      \"state\": \"AZ\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"addressLine1\": \"912 Simcoe Street\",\n" +
            "      \"cellPhone\": \"(404) 555-9658\",\n" +
            "      \"city\": \"Boston\",\n" +
            "      \"comments\": \"Comments about this patient contact.\",\n" +
            "      \"contactId\": 223422,\n" +
            "      \"contactType\": [\n" +
            "        \"Next of kin\"\n" +
            "      ],\n" +
            "      \"country\": \"United States\",\n" +
            "      \"county\": \"Suffolk County\",\n" +
            "      \"firstName\": \"John\",\n" +
            "      \"gender\": \"MALE\",\n" +
            "      \"homePhone\": \"(678) 555-8675\",\n" +
            "      \"isSurveyContact\": false,\n" +
            "      \"lastName\": \"Smith\",\n" +
            "      \"namePrefix\": \"Mr.\",\n" +
            "      \"postalCode\": \"85009\",\n" +
            "      \"relationship\": \"Brother\",\n" +
            "      \"state\": \"MA\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"patientExternalId\": \"1\",\n" +
            "  \"patientId\": 1,\n" +
            "  \"patientRaces\": [\n" +
            "    {\n" +
            "      \"raceDesc\": \"White, Irish origin\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"raceDesc\": \"Brown, Egyptian origin\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"raceDesc\": \"Black, not of Hispanic origin\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"raceCode\": {\n" +
            "    \"codings\": [\n" +
            "      {\n" +
            "        \"system\": \"http://phinvads.cdc.gov\",\n" +
            "        \"version\": \"1\",\n" +
            "        \"code\": \"2054-5\",\n" +
            "        \"display\": \"Black or African American\"\n" +
            "      },\n" +
            "      {\n" +
            "        \"system\": \"http://phinvads.cdc.gov\",\n" +
            "        \"version\": \"1\",\n" +
            "        \"code\": \"2106-3\",\n" +
            "        \"display\": \"White\"\n" +
            "      }\n" +
            "    ]\n" +
            "  },\n" +
            "  \"patientStatus\": \"Discharged\",\n" +
            "  \"phoneNumberType\": \"home\",\n" +
            "  \"preferredName\": \"James\",\n" +
            "  \"prefix\": \"Mr.\",\n" +
            "  \"previousAddress\": {\n" +
            "    \"addressLine1\": \"565 Stratton Building\",\n" +
            "    \"addressLine2\": \"696 Roderick Avenue\",\n" +
            "    \"city\": \"ATLANTA\",\n" +
            "    \"country\": \"U.S.\",\n" +
            "    \"county\": \"Dekalb\",\n" +
            "    \"postalCode\": \"30306\",\n" +
            "    \"state\": \"GA\"\n" +
            "  },\n" +
            "  \"religion\": \"Protestant\",\n" +
            "  \"revisionBy\": \"PCC-bhojaa\",\n" +
            "  \"revisionDate\": \"2003-10-08 04:00:00+21:20\",\n" +
            "  \"roomDesc\": \"225\",\n" +
            "  \"roomId\": 211,\n" +
            "  \"smokingStatusCode\": \"449868002\",\n" +
            "  \"smokingStatusDesc\": \"Current every day smoker\",\n" +
            "  \"socialBeneficiaryIdentifier\": \"000-456-454\",\n" +
            "  \"suffix\": \"Jr.\",\n" +
            "  \"unitDesc\": \"ICF Left\",\n" +
            "  \"unitId\": 789,\n" +
            "  \"userDefinedFields\": [\n" +
            "    {\n" +
            "      \"name\": \"Admission Type\",\n" +
            "      \"description\": \"Long Term\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Key Fob\",\n" +
            "      \"description\": \"IU3HS6JU\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"name\": \"Meal Plan\",\n" +
            "      \"description\": \"mealPlan/1\"\n" +
            "    }\n" +
            "  ],\n" +
            "  \"waitingList\": false\n," +
            //fields below are not actually in from api example
            "  \"admissionDateTime\": \"2020-05-21T15:32:21.000Z\"\n," +
            "  \"middleName\": \"Alex\"\n" +
            "}";


    private static final String PATIENT_MATCH_RESPONSE = "{\n" +
            "  \"data\": [\n" +
            "    {\n" +
            "      \"birthDate\": \"1968-11-29\",\n" +
            "      \"facId\": 1,\n" +
            "      \"firstName\": \"Mary\",\n" +
            "      \"gender\": \"FEMALE\",\n" +
            "      \"healthCardNumber\": \"HEALTH-0643070\",\n" +
            "      \"lastName\": \"Lewis\",\n" +
            "      \"medicaidNumber\": \"MEDIC-AID-001\",\n" +
            "      \"medicareNumber\": \"MEDIC-CARE-001\",\n" +
            "      \"middleName\": \"Middle\",\n" +
            "      \"patientId\": 222,\n" +
            "      \"socialBeneficiaryIdentifier\": \"000-22-3333\"\n" +
            "    }\n" +
            "  ]\n" +
            "}";
    private static final String WEBHOOK_SUBSCRIBE_REQUEST = "{\n" +
            "  \"applicationName\": \"MyTestApp\",\n" +
            "  \"enableRoomReservationCancellation\": true,\n" +
            "  \"endUrl\": \"https://www.testurl.com:443/\",\n" +
            "  \"eventGroupList\": [\n" +
            "    \"ADT01\",\n" +
            "    \"ADT02\"\n" +
            "  ],\n" +
            "  \"includeDischarged\": true,\n" +
            "  \"includeOutpatient\": true,\n" +
            "  \"password\": \"secret01\",\n" +
            "  \"username\": \"user1234\",\n" +
            "  \"vendorExternalId\": \"e9d8e6dd-d4d7-480a-a367-fb91eabfd402\"\n" +
            "}";
    private static final String ADT_LIST_RESPONSE = "{\n" +
            "  \"data\": [\n" +
            "    {\n" +
            "      \"accessingEntityId\": \"8D04A8D1-6097-46A9-BB58-A8A5007BE59C\",\n" +
            "      \"actionCode\": \"TI\",\n" +
            "      \"actionType\": \"Transfer In\",\n" +
            "      \"additionalBedDesc\": \"1A\",\n" +
            "      \"additionalBedId\": 16466,\n" +
            "      \"additionalFloorDesc\": \"1Floor 1\",\n" +
            "      \"additionalFloorId\": 11,\n" +
            "      \"additionalRoomDesc\": \"1V302\",\n" +
            "      \"additionalRoomId\": 15624,\n" +
            "      \"additionalUnitDesc\": \"1West\",\n" +
            "      \"additionalUnitId\": 121999,\n" +
            "      \"adtRecordId\": 669776,\n" +
            "      \"bedDesc\": \"A\",\n" +
            "      \"bedId\": 6466,\n" +
            "      \"effectiveDateTime\": \"2018-10-31T08:00:19Z\",\n" +
            "      \"enteredBy\": \"Mary Smith\",\n" +
            "      \"enteredByPositionId\": 793867,\n" +
            "      \"enteredDate\": \"2017-10-30T08:00:19Z\",\n" +
            "      \"floorDesc\": \"Floor 1\",\n" +
            "      \"floorId\": 1,\n" +
            "      \"isCancelledRecord\": false,\n" +
            "      \"modifiedDateTime\": \"2017-11-01T08:00:19Z\",\n" +
            "      \"origin\": \"Chuck Hospital\",\n" +
            "      \"originType\": \"Acute Care Hospital\",\n" +
            "      \"outpatient\": false,\n" +
            "      \"patientId\": 475694,\n" +
            "      \"payerCode\": \"MCA\",\n" +
            "      \"payerName\": \"Medicare A\",\n" +
            "      \"payerType\": \"medicareA\",\n" +
            "      \"qhsWaiver\": false,\n" +
            "      \"roomDesc\": \"V302\",\n" +
            "      \"roomId\": 5624,\n" +
            "      \"skilledCare\": true,\n" +
            "      \"skilledEffectiveFromDate\": \"2017-10-31\",\n" +
            "      \"skilledEffectiveToDate\": \"2018-11-01\",\n" +
            "      \"standardActionType\": \"Return from Leave\",\n" +
            "      \"unitDesc\": \"West\",\n" +
            "      \"unitId\": 21999\n" +
            "    },\n" +
            "    {\n" +
            "      \"actionCode\": \"TO\",\n" +
            "      \"actionType\": \"Transfer Out\",\n" +
            "      \"additionalBedDesc\": \"1A\",\n" +
            "      \"additionalBedId\": 16466,\n" +
            "      \"additionalFloorDesc\": \"1Floor 1\",\n" +
            "      \"additionalFloorId\": 11,\n" +
            "      \"additionalRoomDesc\": \"1V302\",\n" +
            "      \"additionalRoomId\": 15624,\n" +
            "      \"additionalUnitDesc\": \"1West\",\n" +
            "      \"additionalUnitId\": 121999,\n" +
            "      \"adtRecordId\": 667416,\n" +
            "      \"bedDesc\": \"A\",\n" +
            "      \"bedId\": 6466,\n" +
            "      \"destination\": \"Angelina\",\n" +
            "      \"destinationType\": \"Acute Care Hospital\",\n" +
            "      \"effectiveDateTime\": \"2017-10-30T08:00:19Z\",\n" +
            "      \"enteredBy\": \"John Doe\",\n" +
            "      \"enteredByPositionId\": 56784,\n" +
            "      \"enteredDate\": \"2017-10-28T08:00:19Z\",\n" +
            "      \"floorDesc\": \"Floor 1\",\n" +
            "      \"floorId\": 1,\n" +
            "      \"isCancelledRecord\": false,\n" +
            "      \"modifiedDateTime\": \"2017-10-31T08:00:19Z\",\n" +
            "      \"outpatient\": false,\n" +
            "      \"patientId\": 270234,\n" +
            "      \"payerCode\": \"MCA\",\n" +
            "      \"payerName\": \"Medicare A\",\n" +
            "      \"payerType\": \"medicareA\",\n" +
            "      \"roomDesc\": \"V302\",\n" +
            "      \"roomId\": 5624,\n" +
            "      \"standardActionType\": \"Leave\",\n" +
            "      \"stopBillingDate\": \"2017-10-30\",\n" +
            "      \"unitDesc\": \"West\",\n" +
            "      \"unitId\": 21999\n" +
            "    },\n" +
            "    {\n" +
            "      \"actionCode\": \"AA\",\n" +
            "      \"actionType\": \"Admission\",\n" +
            "      \"additionalBedDesc\": \"1A\",\n" +
            "      \"additionalBedId\": 16466,\n" +
            "      \"additionalFloorDesc\": \"1Floor 1\",\n" +
            "      \"additionalFloorId\": 11,\n" +
            "      \"additionalRoomDesc\": \"1V302\",\n" +
            "      \"additionalRoomId\": 15624,\n" +
            "      \"additionalUnitDesc\": \"1West\",\n" +
            "      \"additionalUnitId\": 121999,\n" +
            "      \"admissionSource\": \"Transfer from a Hospital\",\n" +
            "      \"admissionSourceCode\": \"4\",\n" +
            "      \"admissionType\": \"Elective\",\n" +
            "      \"admissionTypeCode\": \"3\",\n" +
            "      \"adtRecordId\": 662632,\n" +
            "      \"bedDesc\": \"A\",\n" +
            "      \"bedId\": 6466,\n" +
            "      \"effectiveDateTime\": \"2017-09-06T08:00:19Z\",\n" +
            "      \"enteredBy\": \"Alex Jones\",\n" +
            "      \"enteredByPositionId\": 35579,\n" +
            "      \"enteredDate\": \"2017-09-06T08:00:19Z\",\n" +
            "      \"floorDesc\": \"Scrubbed_1\",\n" +
            "      \"floorId\": 1,\n" +
            "      \"isCancelledRecord\": true,\n" +
            "      \"modifiedDateTime\": \"2017-09-06T08:00:19Z\",\n" +
            "      \"origin\": \"Angelina\",\n" +
            "      \"originType\": \"Acute Care Hospital\",\n" +
            "      \"outpatient\": false,\n" +
            "      \"patientId\": 779023,\n" +
            "      \"payerCode\": \"MCA\",\n" +
            "      \"payerName\": \"Medicare A\",\n" +
            "      \"payerType\": \"medicareA\",\n" +
            "      \"qhsWaiver\": false,\n" +
            "      \"roomDesc\": \"V302\",\n" +
            "      \"roomId\": 5624,\n" +
            "      \"standardActionType\": \"Admission\",\n" +
            "      \"unitDesc\": \"West\",\n" +
            "      \"unitId\": 21999\n" +
            "    },\n" +
            "    {\n" +
            "      \"adtRecordId\": 663090,\n" +
            "      \"dischargeStatus\": \"Discharged to home or self care\",\n" +
            "      \"dischargeStatusCode\": \"01\",\n" +
            "      \"effectiveDateTime\": \"2017-06-02T08:00:19Z\",\n" +
            "      \"enteredBy\": \"Dee Brown\",\n" +
            "      \"enteredDate\": \"2017-06-02T08:00:19Z\",\n" +
            "      \"isCancelledRecord\": false,\n" +
            "      \"modifiedDateTime\": \"2017-09-06T08:00:19Z\",\n" +
            "      \"outpatient\": true,\n" +
            "      \"outpatientStatus\": \"inactive\",\n" +
            "      \"patientId\": 1589003,\n" +
            "      \"payerCode\": \"MBO\",\n" +
            "      \"payerName\": \"Medicare B Outpatient\",\n" +
            "      \"payerType\": \"outpatient\",\n" +
            "      \"stopBillingDate\": \"2017-06-30\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"admissionSource\": \"Physician Referral\",\n" +
            "      \"admissionSourceCode\": \"1\",\n" +
            "      \"admissionType\": \"Elective\",\n" +
            "      \"admissionTypeCode\": \"3\",\n" +
            "      \"adtRecordId\": 622608,\n" +
            "      \"effectiveDateTime\": \"2017-09-06T08:00:19Z\",\n" +
            "      \"enteredBy\": \"Kim Johnson\",\n" +
            "      \"enteredByPositionId\": 345672,\n" +
            "      \"enteredDate\": \"2017-09-01T08:00:19Z\",\n" +
            "      \"isCancelledRecord\": true,\n" +
            "      \"modifiedDateTime\": \"2017-09-06T08:00:19Z\",\n" +
            "      \"outpatient\": true,\n" +
            "      \"outpatientStatus\": \"active\",\n" +
            "      \"patientId\": 6789035,\n" +
            "      \"payerCode\": \"MBO\",\n" +
            "      \"payerName\": \"Medicare B Outpatient\",\n" +
            "      \"payerType\": \"outpatient\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"actionCode\": \"DD\",\n" +
            "      \"actionType\": \"Discharge\",\n" +
            "      \"adtRecordId\": 581663,\n" +
            "      \"destination\": \"Rozella\",\n" +
            "      \"destinationType\": \"Private Home/ Residential\",\n" +
            "      \"dischargeStatus\": \"Disharged to Private Residence\",\n" +
            "      \"dischargeStatusCode\": \"01\",\n" +
            "      \"effectiveDateTime\": \"2017-01-13T08:00:19Z\",\n" +
            "      \"enteredBy\": \"Wayne Wilson\",\n" +
            "      \"enteredDate\": \"2017-01-14T08:00:19Z\",\n" +
            "      \"isCancelledRecord\": false,\n" +
            "      \"modifiedDateTime\": \"2017-01-13T08:00:19Z\",\n" +
            "      \"outpatient\": false,\n" +
            "      \"patientId\": 234796,\n" +
            "      \"standardActionType\": \"Discharge\",\n" +
            "      \"stopBillingDate\": \"2017-01-15\"\n" +
            "    },\n" +
            "    {\n" +
            "      \"accessingEntityId\": \"D090EA4C-5D02-41A5-8358-087087D6F9CE\",\n" +
            "      \"actionCode\": \"AA\",\n" +
            "      \"actionType\": \"Admission\",\n" +
            "      \"admissionSource\": \"Physician Referral\",\n" +
            "      \"admissionSourceCode\": \"1\",\n" +
            "      \"admissionType\": \"Elective\",\n" +
            "      \"admissionTypeCode\": \"3\",\n" +
            "      \"adtRecordId\": 568639,\n" +
            "      \"bedDesc\": \"B\",\n" +
            "      \"bedId\": 6481,\n" +
            "      \"effectiveDateTime\": \"2016-12-10T08:00:19Z\",\n" +
            "      \"enteredBy\": \"Rick Miller\",\n" +
            "      \"enteredByPositionId\": 3336876,\n" +
            "      \"enteredDate\": \"2016-12-10T08:00:19Z\",\n" +
            "      \"floorDesc\": \"Floor 2\",\n" +
            "      \"floorId\": 1,\n" +
            "      \"isCancelledRecord\": true,\n" +
            "      \"modifiedDateTime\": \"2017-01-13T08:00:19Z\",\n" +
            "      \"origin\": \"Cordie Hospital\",\n" +
            "      \"originType\": \"Acute Care Hospital\",\n" +
            "      \"outpatient\": false,\n" +
            "      \"patientId\": 789364,\n" +
            "      \"payerCode\": \"MCA\",\n" +
            "      \"payerName\": \"Medicare A\",\n" +
            "      \"payerType\": \"medicareA\",\n" +
            "      \"qhsWaiver\": false,\n" +
            "      \"roomDesc\": \"V312\",\n" +
            "      \"roomId\": 5643,\n" +
            "      \"standardActionType\": \"Admission\",\n" +
            "      \"unitDesc\": \"West\",\n" +
            "      \"unitId\": 21999\n" +
            "    }\n" +
            "  ],\n" +
            "  \"paging\": {\n" +
            "    \"hasMore\": false,\n" +
            "    \"page\": 1,\n" +
            "    \"pageSize\": 50\n" +
            "  }\n" +
            "}";
    private static final String WEBHOOK_SUBSCRIBE_RESPONSE = "{\n" +
            "  \"webhookSubscriptionId\": 196\n" +
            "}";
    private final String PATIENT_LIST_RESPONSE = "{\n" +
            "  \"data\": [\n" +
            "    {\n" +
            "      \"bedDesc\": \"A\",\n" +
            "      \"bedId\": 201,\n" +
            "      \"canadianHCN\": {\n" +
            "        \"coding\": [\n" +
            "          {\n" +
            "            \"system\": \"https://simplifier.net/canadianuriregistry/ca-on-patient-hcn\",\n" +
            "            \"code\": 3243982726\n" +
            "          }\n" +
            "        ]\n" +
            "      },\n" +
            "      \"citizenship\": \"U.S.\",\n" +
            "      \"deceased\": false,\n" +
            "      \"ethnicityDesc\": \"White, Irish origin\",\n" +
            "      \"facId\": 1,\n" +
            "      \"firstName\": \"John\",\n" +
            "      \"floorDesc\": \"2nd\",\n" +
            "      \"floorId\": 3,\n" +
            "      \"gender\": \"MALE\",\n" +
            "      \"hasPhoto\": false,\n" +
            "      \"isOnLeave\": true,\n" +
            "      \"languageCode\": \"en\",\n" +
            "      \"languageDesc\": \"English\",\n" +
            "      \"lastName\": \"Doe\",\n" +
            "      \"maritalStatus\": \"Single\",\n" +
            "      \"medicaidNumber\": \"SCRUBBED_50582\",\n" +
            "      \"medicalRecordNumber\": \"0643070\",\n" +
            "      \"medicareBeneficiaryIdentifier\": \"1A2C3DE0GH89\",\n" +
            "      \"ompId\": 100001,\n" +
            "      \"orgUuid\": \"1750abe8-1b7c-4e26-969c-9d7076ff8c02\",\n" +
            "      \"outpatient\": false,\n" +
            "      \"patientExternalId\": \"222\",\n" +
            "      \"patientId\": 478677,\n" +
            "      \"patientStatus\": \"Current\",\n" +
            "      \"preferredName\": \"John\",\n" +
            "      \"prefix\": \"Mr.\",\n" +
            "      \"religion\": \"Baptist\",\n" +
            "      \"roomDesc\": \"225\",\n" +
            "      \"roomId\": 228,\n" +
            "      \"suffix\": \"Jr.\",\n" +
            "      \"unitDesc\": \"ICF Left\",\n" +
            "      \"unitId\": 123,\n" +
            "      \"waitingList\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"admissionDate\": \"2010-01-30\",\n" +
            "      \"admissionDateTime\": \"2010-01-30T05:00:00.000Z\",\n" +
            "      \"birthDate\": \"1921-05-03\",\n" +
            "      \"citizenship\": \"U.S.\",\n" +
            "      \"deathDateTime\": \"2012-04-20T04:00:00.000Z\",\n" +
            "      \"deceased\": true,\n" +
            "      \"dischargeDate\": \"2012-04-20\",\n" +
            "      \"ethnicityDesc\": \"Brown, Egyptian origin\",\n" +
            "      \"facId\": 1,\n" +
            "      \"firstName\": \"Jane\",\n" +
            "      \"gender\": \"MALE\",\n" +
            "      \"hasPhoto\": true,\n" +
            "      \"languageCode\": \"fr\",\n" +
            "      \"languageDesc\": \"French\",\n" +
            "      \"lastName\": \"Doe\",\n" +
            "      \"maritalStatus\": \"Married\",\n" +
            "      \"medicaidNumber\": \"SCRUBBED_50582\",\n" +
            "      \"medicalRecordNumber\": \"643019\",\n" +
            "      \"medicareNumber\": \"581185786V\",\n" +
            "      \"middleName\": \"M.\",\n" +
            "      \"ompId\": 100002,\n" +
            "      \"orgUuid\": \"2950abe8-1b7c-4e26-969c-9d7076ff8c03\",\n" +
            "      \"outpatient\": false,\n" +
            "      \"patientExternalId\": \"234\",\n" +
            "      \"patientId\": 230181,\n" +
            "      \"patientStatus\": \"Discharged\",\n" +
            "      \"preferredName\": \"Jane\",\n" +
            "      \"prefix\": \"Ms.\",\n" +
            "      \"religion\": \"Catholic\",\n" +
            "      \"suffix\": \"Jr.\",\n" +
            "      \"waitingList\": false\n" +
            "    },\n" +
            "    {\n" +
            "      \"admissionDate\": \"2006-01-30\",\n" +
            "      \"admissionDateTime\": \"2006-01-30T05:00:00.000Z\",\n" +
            "      \"bedDesc\": \"A\",\n" +
            "      \"bedId\": 203,\n" +
            "      \"birthDate\": \"1923-05-03\",\n" +
            "      \"citizenship\": \"U.S.\",\n" +
            "      \"deceased\": false,\n" +
            "      \"ethnicityDesc\": \"Black, not of Hispanic origin\",\n" +
            "      \"facId\": 1,\n" +
            "      \"firstName\": \"John\",\n" +
            "      \"floorDesc\": \"4th\",\n" +
            "      \"floorId\": 11,\n" +
            "      \"gender\": \"MALE\",\n" +
            "      \"hasPhoto\": true,\n" +
            "      \"languageCode\": \"es\",\n" +
            "      \"languageDesc\": \"Spanish\",\n" +
            "      \"lastName\": \"Smith\",\n" +
            "      \"maritalStatus\": \"Married\",\n" +
            "      \"medicaidNumber\": \"SCRUBBED_50582\",\n" +
            "      \"medicareBeneficiaryIdentifier\": \"5A2C3DE0GH88\",\n" +
            "      \"ompId\": 100003,\n" +
            "      \"orgUuid\": \"3950abe8-1b7c-4e36-969c-9d70763f8c03\",\n" +
            "      \"outpatient\": false,\n" +
            "      \"patientExternalId\": \"333\",\n" +
            "      \"patientId\": 222,\n" +
            "      \"patientStatus\": \"Current\",\n" +
            "      \"preferredName\": \"John\",\n" +
            "      \"prefix\": \"Mr.\",\n" +
            "      \"religion\": \"Presbyterian\",\n" +
            "      \"roomDesc\": \"316\",\n" +
            "      \"roomId\": 371,\n" +
            "      \"suffix\": \"Jr.\",\n" +
            "      \"unitDesc\": \"4 SOUTH\",\n" +
            "      \"unitId\": 987,\n" +
            "      \"waitingList\": false\n" +
            "    }\n" +
            "  ],\n" +
            "  \"paging\": {\n" +
            "    \"hasMore\": false,\n" +
            "    \"page\": 1,\n" +
            "    \"pageSize\": 50\n" +
            "  }\n" +
            "}";
    private final String WEBHOOK_SUBSCRIPTIONS_LIST_RESPONSE = "{\n" +
            "  \"data\": [\n" +
            "    {\n" +
            "      \"action\": \"SUBSCRIBE\",\n" +
            "      \"applicationName\": \"pcc\",\n" +
            "      \"applicationType\": \"PRODUCTION\",\n" +
            "      \"createdDate\": \"2022-08-24T15:13:00.910Z\",\n" +
            "      \"currentSubscription\": [\n" +
            "        {\n" +
            "          \"action\": \"SUBSCRIBE\",\n" +
            "          \"orgUuid\": \"a60aba9f-ab02-48fa-b7e6-08c1efb93d32\",\n" +
            "          \"status\": \"FAILED\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"action\": \"SUBSCRIBE\",\n" +
            "          \"orgUuid\": \"a60aba9f-ab02-48fa-b7e6-08c1efb93d32\",\n" +
            "          \"status\": \"PROCESSING\"\n" +
            "        },\n" +
            "        {\n" +
            "          \"action\": \"UNSUBSCRIBE\",\n" +
            "          \"orgUuid\": \"a60aba9f-ab02-48fa-b7e6-08c1efb93d32\",\n" +
            "          \"status\": \"SUCCESS\"\n" +
            "        }\n" +
            "      ],\n" +
            "      \"enableRoomReservationCancellation\": true,\n" +
            "      \"endUrl\": \"https://www.testurl.com\",\n" +
            "      \"eventGroupList\": [\n" +
            "        \"ADT01\",\n" +
            "        \"ADT02\"\n" +
            "      ],\n" +
            "      \"includeDischarged\": true,\n" +
            "      \"includeOutpatient\": true,\n" +
            "      \"revisionDate\": \"2022-08-24T15:14:22.910Z\",\n" +
            "      \"status\": \"APPROVED\",\n" +
            "      \"username\": \"pcc-testcase1\",\n" +
            "      \"vendorExternalId\": \"2b87c6a7-4bf4-4ef5-b78f-db274544e4e5\",\n" +
            "      \"webhookSubscriptionId\": 1963\n" +
            "    }\n" +
            "  ],\n" +
            "  \"paging\": {\n" +
            "    \"hasMore\": false,\n" +
            "    \"page\": 1,\n" +
            "    \"pageSize\": 50\n" +
            "  }\n" +
            "}";
    private PointClickCareAuthenticationTokenManager authenticationTokenManager;
    private PointClickCareApiGatewayImpl instance;
    private MockRestServiceServer mockServer;
    private PccAdtListFilter filter;
    private final String TOO_MANY_REQUESTS_RESPONSE = "{\n" +
            "    \"errors\": [\n" +
            "        {\n" +
            "            \"id\": \"0\",\n" +
            "            \"code\": \"9000\",\n" +
            "            \"status\": \"429\",\n" +
            "            \"title\": \"Too Many Requests.\",\n" +
            "            \"detail\": \"This is mocked 429 error response.\"\n" +
            "        }\n" +
            "    ]\n" +
            "}";
    private PointClickCareNotificationService notificationService;

    private RestTemplateBuilder jsonRestTemplateBuilder() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter(
                new ObjectMapper().findAndRegisterModules()
        );

        var builder = new RestTemplateBuilder().messageConverters(converter);
        return builder;
    }

    @Test
    void facilityById() throws URISyntaxException {
        var facilityId = 1L;
        var url = HOST + "/public/preview1/orgs/" + ORG_UUID + "/facs/" + facilityId;
        var token = "asdfasdfasdfasdf";

        when(authenticationTokenManager.getBearerToken()).thenReturn(token);
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(url)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer " + token))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(FACILITY_RESPONSE)
                );


        var facility = instance.facilityById(ORG_UUID, facilityId);

        assertThat(facility).isNotNull();

        assertThat(facility.getCountry()).isEqualTo("USA");
        assertThat(facility.getTimeZone()).isEqualTo("America/Chicago");
    }

    @BeforeEach
    public void setUp() {
        authenticationTokenManager = mock(PointClickCareAuthenticationTokenManager.class);
        RestGatewaySupport gateway = new RestGatewaySupport();
        notificationService = mock(PointClickCareNotificationService.class);
        instance = new PointClickCareApiGatewayImpl(authenticationTokenManager, jsonRestTemplateBuilder(), HOST, notificationService, List.of(20, 5));

        gateway.setRestTemplate(instance.getRestTemplate());
        mockServer = MockRestServiceServer.createServer(gateway);
    }

    @Test
    void testTooManyRequests() throws URISyntaxException {
        var facilityId = 1L;
        var url = HOST + "/public/preview1/orgs/" + ORG_UUID + "/facs/" + facilityId;
        var token = "asdfasdfasdfasdf";

        when(authenticationTokenManager.getBearerToken()).thenReturn(token);
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(url)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer " + token))
                .andRespond(withStatus(HttpStatus.valueOf(429))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(TOO_MANY_REQUESTS_RESPONSE)
                );


        var thrown = assertThrows(PointClickCareApiException.class, () -> instance.facilityById(ORG_UUID, facilityId));

        assertThat(thrown.getMessage()).contains("429 Too Many Requests");
        assertThat((boolean) ReflectionTestUtils.getField(instance, "stopApiCalls")).isTrue();
    }

    @Test
    void testRequestsStopped() throws URISyntaxException {
        var facilityId = 1L;
        var url = HOST + "/public/preview1/orgs/" + ORG_UUID + "/facs/" + facilityId;
        var token = "asdfasdfasdfasdf";

        when(authenticationTokenManager.getBearerToken()).thenReturn(token);
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(url)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer " + token))
                .andRespond(withStatus(HttpStatus.valueOf(429))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(TOO_MANY_REQUESTS_RESPONSE)
                );

        ReflectionTestUtils.setField(instance, "stopApiCalls", true);

        var thrown = assertThrows(PointClickCareApiException.class, () -> instance.facilityById(ORG_UUID, facilityId));

        assertThat(thrown.getMessage()).startsWith("Reached daily threshold, reset at ");
    }

    @Test
    void patientById() throws URISyntaxException {
        var url = HOST + "/public/preview1/orgs/" + ORG_UUID + "/patients/" + PATIENT_ID;
        var token = "asdfasdfasdfasdf";

        when(authenticationTokenManager.getBearerToken()).thenReturn(token);
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(url)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer " + token))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(PATIENT_RESPONSE)
                );


        var patient = instance.patientById(ORG_UUID, PATIENT_ID);

        assertThat(patient).isNotNull();
        assertThat(patient.getAdmissionDate()).isEqualTo(LocalDate.of(2005, 12, 6));
        assertThat(patient.getAdmissionDateTime()).isEqualTo(
                ZonedDateTime.of(2020, 5, 21, 15, 32, 21, 0, ZoneOffset.UTC)
                        .toInstant()
        );
        assertThat(patient.getBirthDate()).isEqualTo(LocalDate.of(1919, 5, 3));
        assertThat(patient.getCitizenship()).isEqualTo("U.S.");
        assertThat(patient.getDeathDateTime()).isEqualTo(LocalDateTime.of(2010, 1, 30, 5, 0, 0).atOffset(ZoneOffset.UTC).toInstant());
        assertThat(patient.getDeceased()).isEqualTo(true);
        assertThat(patient.getDischargeDate()).isEqualTo(LocalDate.of(2010, 1, 30));
        assertThat(patient.getEmail()).isEqualTo("jadr@haddo.eu");
        assertThat(patient.getFacId()).isEqualTo(1L);
        assertThat(patient.getFirstName()).isEqualTo("James");
        assertThat(patient.getGender()).isEqualTo(PCCPatientDetails.Gender.MALE);
        assertThat(patient.getLanguageCode()).isEqualTo("en");
        assertThat(patient.getLanguageDesc()).isEqualTo("English");
        assertThat(patient.getLastName()).isEqualTo("Bond");
        assertThat(patient.getMaidenName()).isEqualTo("Born");
        assertThat(patient.getMaritalStatus()).isEqualTo("Widowed");
        assertThat(patient.getMedicaidNumber()).isEqualTo("SCRUBBED_50582");
        assertThat(patient.getMedicalRecordNumber()).isEqualTo("ALC3442");
        assertThat(patient.getMedicareBeneficiaryIdentifier()).isEqualTo("1254778");
        assertThat(patient.getMedicareNumber()).isEqualTo("581185786V");
        assertThat(patient.getMiddleName()).isEqualTo("Alex");
        assertThat(patient.getOrgUuid()).isEqualTo("1750abe8-1b7c-4e26-969c-9d7076ff8c02");
        assertThat(patient.isOutpatient()).isFalse();
        assertThat(patient.getPatientId()).isEqualTo(1L);
        assertThat(patient.getPatientStatus()).isEqualTo("Discharged");
        assertThat(patient.getPreferredName()).isEqualTo("James");
        assertThat(patient.getPrefix()).isEqualTo("Mr.");
        assertThat(patient.getSocialBeneficiaryIdentifier()).isEqualTo("000-456-454");
        assertThat(patient.getSuffix()).isEqualTo("Jr.");
        assertThat(patient.getPhoneNumberType()).isEqualTo("home");
        assertThat(patient.getItuPhone()).isEqualTo("(475) 529 8541");

        assertThat(patient.getRaceCode()).isNotNull();
        assertThat(patient.getRaceCode().getCodings()).hasSize(2);

        assertThat(patient.getRaceCode().getCodings().get(0).getSystem()).isEqualTo("http://phinvads.cdc.gov");
        assertThat(patient.getRaceCode().getCodings().get(0).getVersion()).isEqualTo("1");
        assertThat(patient.getRaceCode().getCodings().get(0).getCode()).isEqualTo("2054-5");
        assertThat(patient.getRaceCode().getCodings().get(0).getDisplay()).isEqualTo("Black or African American");

        assertThat(patient.getRaceCode().getCodings().get(1).getSystem()).isEqualTo("http://phinvads.cdc.gov");
        assertThat(patient.getRaceCode().getCodings().get(1).getVersion()).isEqualTo("1");
        assertThat(patient.getRaceCode().getCodings().get(1).getCode()).isEqualTo("2106-3");
        assertThat(patient.getRaceCode().getCodings().get(1).getDisplay()).isEqualTo("White");

        assertThat(patient.getEthnicityCode()).isNotNull();
        assertThat(patient.getEthnicityCode().getCodings()).hasSize(1);

        assertThat(patient.getEthnicityCode().getCodings().get(0).getSystem()).isEqualTo("http://snomed.info/sct");
        assertThat(patient.getEthnicityCode().getCodings().get(0).getVersion()).isEqualTo("1");
        assertThat(patient.getEthnicityCode().getCodings().get(0).getCode()).isEqualTo("2186-5");
        assertThat(patient.getEthnicityCode().getCodings().get(0).getDisplay()).isEqualTo("Not Hispanic or Latino");

        assertThat(patient.getLegalMailingAddress()).isNotNull();
        assertThat(patient.getLegalMailingAddress().getAddressLine1()).isEqualTo("565 Stratton Building");
        assertThat(patient.getLegalMailingAddress().getAddressLine2()).isEqualTo("696 Roderick Avenue");
        assertThat(patient.getLegalMailingAddress().getCity()).isEqualTo("ATLANTA");
        assertThat(patient.getLegalMailingAddress().getCountry()).isEqualTo("U.S.");
        assertThat(patient.getLegalMailingAddress().getCounty()).isEqualTo("Dekalb");
        assertThat(patient.getLegalMailingAddress().getPostalCode()).isEqualTo("30306");
        assertThat(patient.getLegalMailingAddress().getState()).isEqualTo("GA");
    }

    @Test
    void patientMatch() throws URISyntaxException {
        var url = HOST + "/public/preview1/orgs/" + ORG_UUID + "/patients/match";
        var token = "asdfasdfasdfasdf";

        var patientMatchRequest = "{\n" +
                "  \"birthDate\": \"1968-11-29\",\n" +
                "  \"facId\": 1,\n" +
                "  \"firstName\": \"Mary\",\n" +
                "  \"gender\": \"FEMALE\",\n" +
                "  \"healthCardNumber\": \"HEALTH-0643070\",\n" +
                "  \"lastName\": \"Lewis\",\n" +
                "  \"medicaidNumber\": \"MEDIC-AID-001\",\n" +
                "  \"medicareNumber\": \"MEDIC-CARE-001\",\n" +
                "  \"socialBeneficiaryIdentifier\": \"000-22-3333\"\n" +
                "}";

        when(authenticationTokenManager.getBearerToken()).thenReturn(token);
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(url)))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json(patientMatchRequest))
                .andExpect(header("Authorization", "Bearer " + token))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(PATIENT_MATCH_RESPONSE)
                );


        var criteria = new PCCPatientFilterExactMatchCriteria();
        criteria.setBirthDate(LocalDate.of(1968, 11, 29));
        criteria.setFacId(1L);
        criteria.setFirstName("Mary");
        criteria.setGender(PCCPatientDetails.Gender.FEMALE);
        criteria.setHealthCardNumber("HEALTH-0643070");
        criteria.setLastName("Lewis");
        criteria.setMedicaidNumber("MEDIC-AID-001");
        criteria.setMedicareNumber("MEDIC-CARE-001");
        criteria.setSocialBeneficiaryIdentifier("000-22-3333");

        var response = instance.patientMatch(ORG_UUID, criteria);


        assertThat(response).isNotNull();
        assertThat(response.getData()).hasSize(1);

        var patientMatch = response.getData().get(0);
        assertThat(patientMatch.getBirthDate()).isEqualTo(LocalDate.of(1968, 11, 29));
        assertThat(patientMatch.getFacId()).isEqualTo(1L);
        assertThat(patientMatch.getFirstName()).isEqualTo("Mary");
        assertThat(patientMatch.getGender()).isEqualTo(PCCPatientDetails.Gender.FEMALE);
        assertThat(patientMatch.getHealthCardNumber()).isEqualTo("HEALTH-0643070");
        assertThat(patientMatch.getLastName()).isEqualTo("Lewis");
        assertThat(patientMatch.getMedicaidNumber()).isEqualTo("MEDIC-AID-001");
        assertThat(patientMatch.getMedicareNumber()).isEqualTo("MEDIC-CARE-001");
        assertThat(patientMatch.getMiddleName()).isEqualTo("Middle");
        assertThat(patientMatch.getPatientId()).isEqualTo(222L);
        assertThat(patientMatch.getSocialBeneficiaryIdentifier()).isEqualTo("000-22-3333");
    }

    @Test
    void listOfPatients() throws URISyntaxException {
        int page = 1;
        int pageSize = 50;
        long facId = 1L;
        var url = HOST + "/public/preview1/orgs/" + ORG_UUID + "/patients?" +
                "facId=" + facId +
                "&patientStatus=New,Current" +
                "&page=" + page +
                "&pageSize=" + pageSize;
        var token = "asdfasdfasdfasdf";

        when(authenticationTokenManager.getBearerToken()).thenReturn(token);
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(url)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer " + token))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(PATIENT_LIST_RESPONSE)
                );


        var filter = new PCCPatientListFilter(facId);
        filter.setPatientStatus(List.of(PCCPatientDetails.PATIENT_STATUS_NEW, PCCPatientDetails.PATIENT_STATUS_CURRENT));
        var patientList = instance.listOfPatients(ORG_UUID, filter, page, pageSize);

        assertThat(patientList).isNotNull();
        assertPaging(patientList.getPaging(), false, page, pageSize);

        assertThat(patientList.getData()).hasSize(3);

        assertThat(patientList.getData().get(0).getPatientId()).isEqualTo(478677L);
        assertThat(patientList.getData().get(1).getPatientId()).isEqualTo(230181L);
        assertThat(patientList.getData().get(2).getPatientId()).isEqualTo(222L);
    }

    @Test
    void listOfWebhookSubscriptions() throws URISyntaxException {
        int page = 1;
        int pageSize = 50;
        var appName = "pcc";
        LocalDate subscriptionDate = LocalDate.of(2022, 10, 25);
        var filter = new PccWebhookSubscriptionListFilter(PccWebhookSubscriptionStatus.PENDING, appName, subscriptionDate);

        var url = HOST + "/public/preview1/webhook-subscriptions" +
                "?status=" + filter.getStatus() +
                "&applicationName=" + filter.getApplicationName() +
                "&subscriptionDate=" + filter.getSubscriptionDate() +
                "&page=" + page +
                "&pageSize=" + pageSize;
        var token = "asdfasdfasdfasdf";

        when(authenticationTokenManager.getBearerToken()).thenReturn(token);
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(url)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer " + token))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(WEBHOOK_SUBSCRIPTIONS_LIST_RESPONSE)
                );

        var subscriptionList = instance.listOfWebhookSubscriptions(filter, 1, 50);

        assertThat(subscriptionList).isNotNull();
        assertPaging(subscriptionList.getPaging(), false, 1, 50);

        assertThat(subscriptionList.getData()).hasSize(1);

        var subscription = subscriptionList.getData().get(0);

        assertThat(subscription.getAction()).isEqualTo(PccWebhookSubscriptionAction.SUBSCRIBE);
        assertThat(subscription.getApplicationName()).isEqualTo(appName);
        assertThat(subscription.getApplicationType()).isEqualTo(PccApplicationType.PRODUCTION);
        assertThat(subscription.getCreatedDate()).isEqualTo(
                ZonedDateTime.of(2022, 8, 24, 15, 13, 0, 910000000,
                        ZoneOffset.UTC).toInstant()
        );

        assertThat(subscription.isEnableRoomReservationCancellation()).isTrue();
        assertThat(subscription.getEndUrl()).isEqualTo("https://www.testurl.com");
        assertThat(subscription.getEventGroupList()).containsExactly("ADT01", "ADT02");
        assertThat(subscription.isIncludeDischarged()).isTrue();
        assertThat(subscription.isIncludeOutpatient()).isTrue();
        assertThat(subscription.getRevisionDate()).isEqualTo(
                ZonedDateTime.of(2022, 8, 24, 15, 14, 22, 910000000,
                        ZoneOffset.UTC).toInstant()
        );
        assertThat(subscription.getStatus()).isEqualTo(PccWebhookSubscriptionStatus.APPROVED);
        assertThat(subscription.getUsername()).isEqualTo("pcc-testcase1");
        assertThat(subscription.getVendorExternalId()).isEqualTo("2b87c6a7-4bf4-4ef5-b78f-db274544e4e5");
        assertThat(subscription.getWebhookSubscriptionId()).isEqualTo(1963);

        assertThat(subscription.getCurrentSubscription()).hasSize(3);

        assertThat(subscription.getCurrentSubscription().get(0).getAction()).isEqualTo(PccWebhookSubscriptionAction.SUBSCRIBE);
        assertThat(subscription.getCurrentSubscription().get(0).getOrgUuid()).isEqualTo("a60aba9f-ab02-48fa-b7e6-08c1efb93d32");
        assertThat(subscription.getCurrentSubscription().get(0).getStatus()).isEqualTo(PccOrgWebhookSubscriptionStatusState.FAILED);

        assertThat(subscription.getCurrentSubscription().get(1).getAction()).isEqualTo(PccWebhookSubscriptionAction.SUBSCRIBE);
        assertThat(subscription.getCurrentSubscription().get(1).getOrgUuid()).isEqualTo("a60aba9f-ab02-48fa-b7e6-08c1efb93d32");
        assertThat(subscription.getCurrentSubscription().get(1).getStatus()).isEqualTo(PccOrgWebhookSubscriptionStatusState.PROCESSING);

        assertThat(subscription.getCurrentSubscription().get(2).getAction()).isEqualTo(PccWebhookSubscriptionAction.UNSUBSCRIBE);
        assertThat(subscription.getCurrentSubscription().get(2).getOrgUuid()).isEqualTo("a60aba9f-ab02-48fa-b7e6-08c1efb93d32");
        assertThat(subscription.getCurrentSubscription().get(2).getStatus()).isEqualTo(PccOrgWebhookSubscriptionStatusState.SUCCESS);
    }

    @Test
    void subscribeWebhook() throws URISyntaxException {
        var body = new PccPostWebhookSubscription();
        body.setApplicationName("MyTestApp");
        body.setEnableRoomReservationCancellation(true);
        body.setEndUrl("https://www.testurl.com:443/");
        body.setEventGroupList(List.of("ADT01", "ADT02"));
        body.setIncludeDischarged(true);
        body.setIncludeOutpatient(true);
        body.setPassword("secret01");
        body.setUsername("user1234");
        body.setVendorExternalId("e9d8e6dd-d4d7-480a-a367-fb91eabfd402");

        var url = HOST + "/public/preview1/webhook-subscriptions";
        var token = "asdfasdfasdfasdf";

        when(authenticationTokenManager.getBearerToken()).thenReturn(token);
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(url)))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("Authorization", "Bearer " + token))
                .andExpect(content().json(WEBHOOK_SUBSCRIBE_REQUEST))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(WEBHOOK_SUBSCRIBE_RESPONSE)
                );

        var subscriptionResponse = instance.subscribeWebhook(body);

        assertThat(subscriptionResponse).isNotNull();
        assertThat(subscriptionResponse.getWebhookSubscriptionId()).isEqualTo(196L);
    }

    @Test
    void adtList() throws URISyntaxException {
        int page = 1;
        int pageSize = 50;
        var filter = new PccAdtListFilter();
        filter.setFacId(7L);
        filter.setPatientId(432L);
        filter.setEffectiveDate(LocalDate.of(2002, 4, 6));
        filter.setRecordType(PccAdtRecordType.ALL);

        var url = HOST + "/public/preview1/orgs/" + ORG_UUID + "/adt-records" +
                "?facId=" + filter.getFacId() +
                "&patientId=" + filter.getPatientId() +
                "&effectiveDate=" + filter.getEffectiveDate() +
                "&recordType=" + filter.getRecordType().name().toLowerCase() +
                "&page=" + page +
                "&pageSize=" + pageSize;
        var token = "asdfasdfasdfasdf";

        when(authenticationTokenManager.getBearerToken()).thenReturn(token);
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(url)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer " + token))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(ADT_LIST_RESPONSE)
                );

        var adtList = instance.adtList(ORG_UUID, filter, 1, 50);

        assertThat(adtList).isNotNull();
        assertPaging(adtList.getPaging(), false, 1, 50);

        assertThat(adtList.getData()).hasSize(7);

        var adtRecord = adtList.getData().get(0);
        assertThat(adtRecord.getAccessingEntityId()).isEqualTo("8D04A8D1-6097-46A9-BB58-A8A5007BE59C");
        assertThat(adtRecord.getActionCode()).isEqualTo("TI");
        assertThat(adtRecord.getActionType()).isEqualTo("Transfer In");
        assertThat(adtRecord.getAdditionalBedDesc()).isEqualTo("1A");
        assertThat(adtRecord.getAdditionalBedId()).isEqualTo(16466L);
        assertThat(adtRecord.getAdditionalFloorDesc()).isEqualTo("1Floor 1");
        assertThat(adtRecord.getAdditionalFloorId()).isEqualTo(11L);
        assertThat(adtRecord.getAdditionalRoomDesc()).isEqualTo("1V302");
        assertThat(adtRecord.getAdditionalRoomId()).isEqualTo(15624L);
        assertThat(adtRecord.getAdditionalUnitDesc()).isEqualTo("1West");
        assertThat(adtRecord.getAdditionalUnitId()).isEqualTo(121999L);
        assertThat(adtRecord.getAdtRecordId()).isEqualTo(669776L);
        assertThat(adtRecord.getBedDesc()).isEqualTo("A");
        assertThat(adtRecord.getBedId()).isEqualTo(6466L);
        assertThat(adtRecord.getEffectiveDateTime()).isEqualTo(
                ZonedDateTime.of(2018, 10, 31, 8, 0, 19, 0,
                        ZoneOffset.UTC).toInstant()
        );
        assertThat(adtRecord.getEnteredBy()).isEqualTo("Mary Smith");
        assertThat(adtRecord.getEnteredByPositionId()).isEqualTo(793867L);
        assertThat(adtRecord.getEnteredDate()).isEqualTo(
                ZonedDateTime.of(2017, 10, 30, 8, 0, 19, 0,
                        ZoneOffset.UTC).toInstant()
        );
        assertThat(adtRecord.getFloorDesc()).isEqualTo("Floor 1");
        assertThat(adtRecord.getFloorId()).isEqualTo(1L);
        assertThat(adtRecord.getIsCancelledRecord()).isEqualTo(false);
        assertThat(adtRecord.getModifiedDateTime()).isEqualTo(
                ZonedDateTime.of(2017, 11, 1, 8, 0, 19, 0,
                        ZoneOffset.UTC).toInstant()
        );
        assertThat(adtRecord.getOrigin()).isEqualTo("Chuck Hospital");
        assertThat(adtRecord.getOriginType()).isEqualTo("Acute Care Hospital");
        assertThat(adtRecord.isOutpatient()).isEqualTo(false);
        assertThat(adtRecord.getPatientId()).isEqualTo(475694);
        assertThat(adtRecord.getPayerCode()).isEqualTo("MCA");
        assertThat(adtRecord.getPayerName()).isEqualTo("Medicare A");
        assertThat(adtRecord.getPayerType()).isEqualTo("medicareA");
        assertThat(adtRecord.isQhsWaiver()).isEqualTo(false);
        assertThat(adtRecord.getRoomDesc()).isEqualTo("V302");
        assertThat(adtRecord.getRoomId()).isEqualTo(5624L);
        assertThat(adtRecord.isSkilledCare()).isEqualTo(true);
        assertThat(adtRecord.getSkilledEffectiveFromDate()).isEqualTo(LocalDate.of(2017, 10, 31));
        assertThat(adtRecord.getSkilledEffectiveToDate()).isEqualTo(LocalDate.of(2018, 11, 1));
        assertThat(adtRecord.getStandardActionType()).isEqualTo("Return from Leave");
        assertThat(adtRecord.getUnitDesc()).isEqualTo("West");
        assertThat(adtRecord.getUnitId()).isEqualTo(21999L);

        adtRecord = adtList.getData().get(1);
        assertThat(adtRecord.getActionCode()).isEqualTo("TO");
        assertThat(adtRecord.getActionType()).isEqualTo("Transfer Out");
        assertThat(adtRecord.getAdditionalBedDesc()).isEqualTo("1A");
        assertThat(adtRecord.getAdditionalBedId()).isEqualTo(16466L);
        assertThat(adtRecord.getAdditionalFloorDesc()).isEqualTo("1Floor 1");
        assertThat(adtRecord.getAdditionalFloorId()).isEqualTo(11L);
        assertThat(adtRecord.getAdditionalRoomDesc()).isEqualTo("1V302");
        assertThat(adtRecord.getAdditionalRoomId()).isEqualTo(15624L);
        assertThat(adtRecord.getAdditionalUnitDesc()).isEqualTo("1West");
        assertThat(adtRecord.getAdditionalUnitId()).isEqualTo(121999L);
        assertThat(adtRecord.getAdtRecordId()).isEqualTo(667416L);
        assertThat(adtRecord.getBedDesc()).isEqualTo("A");
        assertThat(adtRecord.getBedId()).isEqualTo(6466L);
        assertThat(adtRecord.getDestination()).isEqualTo("Angelina");
        assertThat(adtRecord.getDestinationType()).isEqualTo("Acute Care Hospital");
        assertThat(adtRecord.getEffectiveDateTime()).isEqualTo(
                ZonedDateTime.of(2017, 10, 30, 8, 0, 19, 0,
                        ZoneOffset.UTC).toInstant()
        );
        assertThat(adtRecord.getEnteredBy()).isEqualTo("John Doe");
        assertThat(adtRecord.getEnteredByPositionId()).isEqualTo(56784L);
        assertThat(adtRecord.getEnteredDate()).isEqualTo(
                ZonedDateTime.of(2017, 10, 28, 8, 0, 19, 0,
                        ZoneOffset.UTC).toInstant()
        );
        assertThat(adtRecord.getFloorDesc()).isEqualTo("Floor 1");
        assertThat(adtRecord.getFloorId()).isEqualTo(1L);
        assertThat(adtRecord.getIsCancelledRecord()).isEqualTo(false);
        assertThat(adtRecord.getModifiedDateTime()).isEqualTo(
                ZonedDateTime.of(2017, 10, 31, 8, 0, 19, 0,
                        ZoneOffset.UTC).toInstant()
        );
        assertThat(adtRecord.isOutpatient()).isEqualTo(false);
        assertThat(adtRecord.getPatientId()).isEqualTo(270234L);
        assertThat(adtRecord.getPayerCode()).isEqualTo("MCA");
        assertThat(adtRecord.getPayerName()).isEqualTo("Medicare A");
        assertThat(adtRecord.getPayerType()).isEqualTo("medicareA");
        assertThat(adtRecord.getRoomDesc()).isEqualTo("V302");
        assertThat(adtRecord.getRoomId()).isEqualTo(5624L);
        assertThat(adtRecord.getStandardActionType()).isEqualTo("Leave");
        assertThat(adtRecord.getStopBillingDate()).isEqualTo(
                LocalDate.of(2017, 10, 30)
        );
        assertThat(adtRecord.getUnitDesc()).isEqualTo("West");
        assertThat(adtRecord.getUnitId()).isEqualTo(21999L);


        adtRecord = adtList.getData().get(2);
        assertThat(adtRecord.getActionCode()).isEqualTo("AA");
        assertThat(adtRecord.getActionType()).isEqualTo("Admission");
        assertThat(adtRecord.getAdditionalBedDesc()).isEqualTo("1A");
        assertThat(adtRecord.getAdditionalBedId()).isEqualTo(16466L);
        assertThat(adtRecord.getAdditionalFloorDesc()).isEqualTo("1Floor 1");
        assertThat(adtRecord.getAdditionalFloorId()).isEqualTo(11L);
        assertThat(adtRecord.getAdditionalRoomDesc()).isEqualTo("1V302");
        assertThat(adtRecord.getAdditionalRoomId()).isEqualTo(15624L);
        assertThat(adtRecord.getAdditionalUnitDesc()).isEqualTo("1West");
        assertThat(adtRecord.getAdditionalUnitId()).isEqualTo(121999L);
        assertThat(adtRecord.getAdmissionSource()).isEqualTo("Transfer from a Hospital");
        assertThat(adtRecord.getAdmissionSourceCode()).isEqualTo("4");
        assertThat(adtRecord.getAdmissionType()).isEqualTo("Elective");
        assertThat(adtRecord.getAdmissionTypeCode()).isEqualTo("3");
        assertThat(adtRecord.getAdtRecordId()).isEqualTo(662632L);
        assertThat(adtRecord.getBedDesc()).isEqualTo("A");
        assertThat(adtRecord.getBedId()).isEqualTo(6466L);
        assertThat(adtRecord.getEffectiveDateTime()).isEqualTo(
                ZonedDateTime.of(2017, 9, 6, 8, 0, 19, 0,
                        ZoneOffset.UTC).toInstant()
        );
        assertThat(adtRecord.getEnteredBy()).isEqualTo("Alex Jones");
        assertThat(adtRecord.getEnteredByPositionId()).isEqualTo(35579L);
        assertThat(adtRecord.getEnteredDate()).isEqualTo(
                ZonedDateTime.of(2017, 9, 6, 8, 0, 19, 0,
                        ZoneOffset.UTC).toInstant()
        );
        assertThat(adtRecord.getFloorDesc()).isEqualTo("Scrubbed_1");
        assertThat(adtRecord.getFloorId()).isEqualTo(1L);
        assertThat(adtRecord.getIsCancelledRecord()).isEqualTo(true);
        assertThat(adtRecord.getModifiedDateTime()).isEqualTo(
                ZonedDateTime.of(2017, 9, 6, 8, 0, 19, 0,
                        ZoneOffset.UTC).toInstant()
        );
        assertThat(adtRecord.getOrigin()).isEqualTo("Angelina");
        assertThat(adtRecord.getOriginType()).isEqualTo("Acute Care Hospital");
        assertThat(adtRecord.isOutpatient()).isEqualTo(false);
        assertThat(adtRecord.getPatientId()).isEqualTo(779023L);
        assertThat(adtRecord.getPayerCode()).isEqualTo("MCA");
        assertThat(adtRecord.getPayerName()).isEqualTo("Medicare A");
        assertThat(adtRecord.getPayerType()).isEqualTo("medicareA");
        assertThat(adtRecord.isQhsWaiver()).isEqualTo(false);
        assertThat(adtRecord.getRoomDesc()).isEqualTo("V302");
        assertThat(adtRecord.getRoomId()).isEqualTo(5624L);
        assertThat(adtRecord.getStandardActionType()).isEqualTo("Admission");
        assertThat(adtRecord.getUnitDesc()).isEqualTo("West");
        assertThat(adtRecord.getUnitId()).isEqualTo(21999L);

        adtRecord = adtList.getData().get(3);
        assertThat(adtRecord.getAdtRecordId()).isEqualTo(663090L);
        assertThat(adtRecord.getDischargeStatus()).isEqualTo("Discharged to home or self care");
        assertThat(adtRecord.getDischargeStatusCode()).isEqualTo("01");
        assertThat(adtRecord.getEffectiveDateTime()).isEqualTo(
                ZonedDateTime.of(2017, 6, 2, 8, 0, 19, 0,
                        ZoneOffset.UTC).toInstant()
        );
        assertThat(adtRecord.getEnteredBy()).isEqualTo("Dee Brown");
        assertThat(adtRecord.getEnteredDate()).isEqualTo(
                ZonedDateTime.of(2017, 6, 2, 8, 0, 19, 0,
                        ZoneOffset.UTC).toInstant()
        );
        assertThat(adtRecord.getIsCancelledRecord()).isEqualTo(false);
        assertThat(adtRecord.getModifiedDateTime()).isEqualTo(
                ZonedDateTime.of(2017, 9, 6, 8, 0, 19, 0,
                        ZoneOffset.UTC).toInstant()
        );
        assertThat(adtRecord.isOutpatient()).isEqualTo(true);
        assertThat(adtRecord.getOutpatientStatus()).isEqualTo("inactive");
        assertThat(adtRecord.getPatientId()).isEqualTo(1589003L);
        assertThat(adtRecord.getPayerCode()).isEqualTo("MBO");
        assertThat(adtRecord.getPayerName()).isEqualTo("Medicare B Outpatient");
        assertThat(adtRecord.getPayerType()).isEqualTo("outpatient");
        assertThat(adtRecord.getStopBillingDate()).isEqualTo(
                LocalDate.of(2017, 6, 30)
        );
    }

    @Test
    void adtListByIds() throws URISyntaxException {
        int page = 1;
        int pageSize = 50;
        var filter = new PccAdtListFilter();
        filter.setAdtRecordIds(List.of(5L, 4L));
        filter.setFacId(7L);
        filter.setEffectiveDate(LocalDate.of(2002, 4, 6));
        filter.setRecordType(PccAdtRecordType.ALL);

        var url = HOST + "/public/preview1/orgs/" + ORG_UUID + "/adt-records" +
                "?adtRecordIds=5,4" +
                "&facId=" + filter.getFacId() +
                "&effectiveDate=" + filter.getEffectiveDate() +
                "&recordType=" + filter.getRecordType().name().toLowerCase();
        var token = "asdfasdfasdfasdf";

        when(authenticationTokenManager.getBearerToken()).thenReturn(token);
        mockServer.expect(ExpectedCount.once(), requestTo(new URI(url)))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("Authorization", "Bearer " + token))
                .andRespond(withStatus(HttpStatus.OK)
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(ADT_LIST_RESPONSE)
                );

        var adtList = instance.adtList(ORG_UUID, filter, 1, 50);

        assertThat(adtList).isNotNull();
        assertPaging(adtList.getPaging(), false, 1, 50);

        assertThat(adtList.getData()).hasSize(7);
    }

    private void assertPaging(PCCPagingResponseByPage actual, boolean hasMore, int page, int pageSize) {
        assertThat(actual).isNotNull();

        assertThat(actual.isHasMore()).isEqualTo(hasMore);
        assertThat(actual.getPage()).isEqualTo(page);
        assertThat(actual.getPageSize()).isEqualTo(pageSize);
    }
}