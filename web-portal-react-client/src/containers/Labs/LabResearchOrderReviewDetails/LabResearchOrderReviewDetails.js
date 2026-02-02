import React, { memo, useState, useCallback } from 'react'

import { isString } from 'underscore'

import { Button } from 'reactstrap'

import {
    Detail as BaseDetail
} from 'components/business/common'

import ClientEssentialsEditor from 'containers/Clients/Clients/ClientEssentialsEditor/ClientEssentialsEditor'

import { useDirectoryData } from 'hooks/common'

import {
    isNotEmpty,
    getAddress,
    DateUtils as DU,
} from 'lib/utils/Utils'

import { isNotEmptyOrBlank } from 'lib/utils/ObjectUtils'

import './LabResearchOrderReviewDetails.scss'

const { format, formats } = DU
const DATE_FORMAT = formats.americanMediumDate
const DATE_TIME_FORMAT = formats.longDateMediumTime12

const formatSSN = ssn => ssn ? '###-##-' + ssn.substr(-4, 4) : ''

const Detail = props => (
    <BaseDetail
        titleClassName="LabResearchOrderReviewDetail-Title"
        valueClassName="LabResearchOrderReviewDetail-Value"
        className="LabResearchOrderReviewDetail"
        {...props}
    />
)

const AlertPanel = ({ children }) => (
    <div className="LabResearchOrderReviewDetails-Alert">
        <span className="LabResearchOrderReviewDetails-AlertText">
            {children}
        </span>
    </div>
)

function LabResearchOrderReviewDetails({ data, onChange }) {
    let [isEditorOpen, setIsEditorOpen] = useState(false)

    const {
        races,
        states,
        genders,
        reasons,
        specimenTypes,
        policyHolderRelations,
    } = useDirectoryData({
        races: ['race'],
        states: ['state'],
        genders: ['gender'],
        reasons: ['lab', 'research', 'reason'],
        specimenTypes: ['lab', 'research', 'order', 'specimenType'],
        policyHolderRelations: ['lab', 'research', 'policyHolderRelation']
    })

    let state = states.find(o => o.id === data.client.address.stateId).label
    let reason = reasons.find(o => o.name === data.reason)?.title
    let specimens = data.specimen?.types.map(id => (
        specimenTypes.find(o => o.id === id).title)
    ) ?? []
    let race = races.find(o => o.id === data.client.raceId).title
    let gender = genders.find(o => o.id === data.client.genderId).label
    let policyHolderRelation = policyHolderRelations.find(o => o.name === data.client?.policyHolderRelationName)?.title

    let isSelfHolder = data.client?.policyHolderRelationName === 'SELF'
    let showPolicyHolderSection = !isSelfHolder && !!data.client?.policyHolderRelationName

    let icdCodes = Array.from(
        new Set([
            ...data.icd10Codes,
            ...Object.values(data.customIcdCodes.toJS()).filter(isString)
        ])
    )

    return (
        <>
            <div className="LabResearchOrderReviewDetails">
                <div className="LabResearchOrderReviewDetails-Body">
                    <div className="LabResearchOrderReviewDetails-Section">
                        <div className="LabResearchOrderReviewDetails-SectionTitle">
                            Order Information
                        </div>

                        <Detail title="Reason For Testing">
                            {reason}
                        </Detail>

                        <Detail title="Clinic">
                            {data.clinic}
                        </Detail>

                        <Detail title="Clinic Address">
                            {data.clinicAddress}
                        </Detail>
                    </div>

                    {isNotEmptyOrBlank(data.client) && (
                        <div className="LabResearchOrderReviewDetails-Section">
                            <div className="LabResearchOrderReviewDetails-SectionTitle">
                                Client information

                                <Button
                                    color="success"
                                    onClick={() => setIsEditorOpen(true)}
                                    className="LabResearchOrderReviewDetails-EditBtn"
                                >
                                    Edit
                                </Button>
                            </div>

                            <Detail className="LabResearchOrderReviewDetail_flex_column" title="Name">
                                {data.client.fullName}
                                <span className="LabResearchOrderReviewDetail-Alert">
                                    The name MUST match the name on a tube with a specimen
                                </span>
                            </Detail>

                            <Detail title="Sex">
                                {gender}
                            </Detail>

                            <Detail title="Race">
                                {race}
                            </Detail>

                            <Detail className="LabResearchOrderReviewDetail_flex_column" title="Date Of Birth">
                                {data.client.birthDate}
                                <span className="LabResearchOrderReviewDetail-Alert">
                                    The date of birth MUST match the date of birth on a tube with a specimen
                                </span>
                            </Detail>

                            <Detail title="SSN">
                                {formatSSN(data.client.ssn)}
                            </Detail>

                            <Detail title="Phone #">
                                {data.client.phone}
                            </Detail>

                            <Detail title="Address">
                                {getAddress({
                                    ...data.client.address.toJS(),
                                    state,
                                }, ',')}
                            </Detail>

                            <Detail title="Primary Insurance Provider">
                                {data.client.insuranceNetwork}
                            </Detail>

                            <Detail title="Policy #">
                                {data.client.policyNumber}
                            </Detail>

                            <Detail title="Policy Holder">
                                {policyHolderRelation}
                            </Detail>

                            {showPolicyHolderSection && (
                                <>
                                    <Detail title="Policy Holder Name (if spouse or parent)">
                                        {data.client.policyHolderName}
                                    </Detail>

                                    <Detail title="Policy Holder DOB">
                                        {data.client.policyHolderDOB}
                                    </Detail>
                                </>
                            )}
                        </div>
                    )}

                    {isNotEmptyOrBlank(data.specimen) && (
                        <div className="LabResearchOrderReviewDetails-Section">
                            <div className="LabResearchOrderReviewDetails-SectionTitle">
                                Specimen information
                            </div>

                            <Detail title="Specimen">
                                <div className="d-flex flex-column">
                                    {specimens.map(type => (
                                        <div key={type} className="line-height-2">
                                            {type}
                                        </div>
                                    ))}
                                </div>
                            </Detail>

                            <Detail title="Collector's Name">
                                {data.specimen.collectorName}
                            </Detail>

                            <Detail title="Site">
                                {data.specimen.site}
                            </Detail>

                            <Detail title="Date & Time">
                                {format(data.specimen.date, DATE_TIME_FORMAT) || format(new Date().getTime(), DATE_TIME_FORMAT)}
                            </Detail>
                        </div>
                    )}

                    <div className="LabResearchOrderReviewDetails-Section">
                        <div className="LabResearchOrderReviewDetails-SectionTitle">
                            Ordering Provider
                        </div>

                        <Detail title="Name">
                            {data.providerFirstName} {data.providerLastName}
                        </Detail>

                        <Detail title="Date">
                            {format(data.orderDate, DATE_FORMAT) || format(new Date().getTime(), DATE_FORMAT)}
                        </Detail>

                    </div>

                    <div className="LabResearchOrderReviewDetails-Section">
                        <div className="LabResearchOrderReviewDetails-SectionTitle">
                            Panels
                        </div>

                        <Detail title="Urgent COVID-19">
                            <span className="max-width-600">
                                URGENT COVID-19 (SARS-CoV-2, Coronavirus)
                                Negative results do not preclude SARS‐CoV‐2 infection and should not be used as the sole basis for
                                patient management decisions. Negative results must be combined with clinical observations, patient history,
                                and epidemiological information. The assay is intended for use under the Food and Drug Administration’s
                                Emergency Use Authorization.
                        </span>
                        </Detail>
                    </div>

                    <div className="LabResearchOrderReviewDetails-Section">
                        <div className="LabResearchOrderReviewDetails-SectionTitle">
                            ICD Codes
                        </div>

                        <Detail title="ICD 10 Code">
                            <div className="d-flex flex-column">
                                {icdCodes.map(code => (
                                    <div key={code} className="line-height-2">{code}</div>
                                ))}
                            </div>
                        </Detail>
                    </div>

                    {isNotEmpty(data.notes) && (
                        <div className="LabResearchOrderReviewDetails-Section">
                            <div className="LabResearchOrderReviewDetails-SectionTitle">
                                Additional notes
                            </div>

                            <Detail title="Notes">
                                {data.notes}
                            </Detail>

                        </div>
                    )}

                    <AlertPanel>
                        By clicking the "Submit" button, you sign the order electronically. All orders will be reviewed to ensure they meet the CMS guidelines for medical necessity.
                        If Integrity Laboratories receives insufficient information documenting the medical necessity of the ordered tests, then providers will be asked to review the order
                        and provide additional documentation before the testing is performed. The ordering provider acknowledges that all selections from this document are deemed to be medically
                        necessary as documented in the patient’s clinical record. The ordering provider may order any of the above stated tests separatelyor in necessary combinations
                        consistent with the patient's medical needs. The ordering provider must provide Integrity with all necessary billing information at the time of specimen
                        submission and medical records upon request in a timely manner.
                    </AlertPanel>
                </div>
            </div>

            <ClientEssentialsEditor
                data={data.client}
                isOpen={isEditorOpen}
                onClose={useCallback(() => setIsEditorOpen(false), [])}
                onSubmit={onChange}
            />
        </>
    )
}

export default memo(LabResearchOrderReviewDetails)