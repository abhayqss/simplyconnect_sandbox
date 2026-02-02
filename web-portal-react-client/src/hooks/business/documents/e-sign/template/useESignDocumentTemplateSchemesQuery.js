import { map } from 'underscore'

import { useQuery } from '@tanstack/react-query'

import service from 'services/DocumentESignService'

import {
    HIPAATemplateFormJsonSchema,
    AdmissionAgreementTemplateFormJsonSchema,
    DirectorsAdmissionTemplateFormJsonSchema,
    AL_CONTRACT_ATT_C_MealPlan_FormJsonSchema,
    AL_CONTRACT_ATT_E_FormJsonSchema,
    AL_CONTRACT_SUMMARY_FormJsonSchema,
    SecurityDepositTemplateFormJsonSchema,
    ArbitrationFormJsonSchema,
    CovidConsentTemplateFormJsonSchema,
    PharmacyConsentTemplateFormJsonSchema,
    AcknowledgeTemplateFormJsonSchema
} from 'containers/Clients/Clients/Documents/schemes'

const TEMP_ID_SCHEMA = {
    1: AdmissionAgreementTemplateFormJsonSchema,
    2: DirectorsAdmissionTemplateFormJsonSchema,
    3: HIPAATemplateFormJsonSchema,
    4: AL_CONTRACT_ATT_C_MealPlan_FormJsonSchema,
    5: AL_CONTRACT_ATT_E_FormJsonSchema,
    6: SecurityDepositTemplateFormJsonSchema,
    7: ArbitrationFormJsonSchema,
    8: CovidConsentTemplateFormJsonSchema,
    9: PharmacyConsentTemplateFormJsonSchema,
    10: SecurityDepositTemplateFormJsonSchema,
    11: AL_CONTRACT_SUMMARY_FormJsonSchema,
    12: AcknowledgeTemplateFormJsonSchema
}

function fetch({ templateIds, ...params }) {  
    return Promise.all(map(templateIds, templateId => (
        service.findTemplateSchemeById({ templateId, ...params })
    )))  
}

export default function useESignDocumentTemplateSchemesQuery(params, options) {
    return useQuery(['ESign.DocumentTemplate.Schemes', params], () => fetch(params), options)
}