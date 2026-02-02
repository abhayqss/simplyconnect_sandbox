import React, {
    memo,
    useState,
    useEffect,
    useCallback
} from 'react'

import {
    useToggle
} from 'hooks/common'

import {
    usePaperlessHealthcareDemoRequestSubmit
} from 'hooks/business/paperless-healthcare'

import {
    Modal
} from 'components'

import {
    SuccessDialog
} from 'components/dialogs'

import { FEATURES, FEATURE_TITLES } from 'lib/Constants'

import { noop } from 'lib/utils/FuncUtils'

import {
    ConsentTile,
    ConsentSummary
} from '../Consent'

import {
    PersonalDashboardTile,
    PersonalDashboardSummary
} from '../PersonalDashboard'

import {
    TeamCareTile,
    TeamCareSummary
} from '../TeamCare'

import {
    CommunicationsTile,
    CommunicationsSummary
} from '../Communications'

import {
    DocumentsTile,
    DocumentsSummary
} from '../Documents'

import {
    PharmacyMedicationsTile,
    PharmacyMedicationsSummary
} from '../PharmacyMedications'

import {
    NotifyTile,
    NotifySummary
} from '../Notify'

import {
    NotifyFallDetectionTile,
    NotifyFallDetectionSummary
} from '../NotifyFallDetection'

import {
    ReferralMarketplaceTile,
    ReferralMarketplaceSummary
} from '../ReferralMarketplace'

import {
    ClinicalPharmacistTile,
    ClinicalPharmacistSummary
} from '../ClinicalPharmacist'

import {
    NonEmergencyTransportationTile,
    NonEmergencyTransportationSummary
} from '../NonEmergencyTransportation'

import {
    HipaaCompliantCalendarTile,
    HipaaCompliantCalendarSummary
} from '../HipaaCompliantCalendar'

import './Features.scss'

const {
    CONSENT,
    TEAM_CARE,
    PHARMACY_MEDICATIONS,
    COMMUNICATIONS,
    DOCUMENTS,
    PERSONAL_DASHBOARD,
    NOTIFY,
    NOTIFY_FALL_DETECTION,
    REFERRAL_MARKETPLACE,
    CLINICAL_PHARMACIST,
    NON_EMERGENCY_TRANSPORTATION,
    HIPAA_COMPLIANT_CALENDAR
} = FEATURES


function Features({ onSelect }) {
    const [selected, setSelected] = useState(null)

    const select = useCallback(name => setSelected(name), [])
    const unselect = useCallback(() => setSelected(null), []);

    const [isSuccessDialogOpen, toggleSuccessDialog] = useToggle();

    const { mutate: requestDemo } = usePaperlessHealthcareDemoRequestSubmit({
        onSuccess: () => {
            unselect();
            toggleSuccessDialog();
        }
    });

    const onDemo = useCallback(
        () => requestDemo(FEATURE_TITLES[selected]),
        [requestDemo, selected]
    );

    useEffect(() => {
        onSelect(selected && {
            name: selected,
            title: FEATURE_TITLES[selected]
        })
    }, [selected, onSelect])

    return (
        <div className="Features">
            <div className="Features-Title">
                Move-in Experience
            </div>
            <div className="FeatureCardList">
                <div className="FeatureCardList-Item">
                    <ConsentTile onClick={select} />
                </div>
                <div className="FeatureCardList-Item">
                    <PersonalDashboardTile onClick={select} />
                </div>
                <div className="FeatureCardList-Item">
                    <TeamCareTile onClick={select} />
                </div>
                <div className="FeatureCardList-Item">
                    <CommunicationsTile onClick={select} />
                </div>
                <div className="FeatureCardList-Item">
                    <DocumentsTile onClick={select} />
                </div>
                <div className="FeatureCardList-Item">
                    <HipaaCompliantCalendarTile onClick={select} />
                </div>
                <div className="FeatureCardList-Item">
                    <NotifyTile onClick={select} />
                </div>
                <div className="FeatureCardList-Item">
                    <NotifyFallDetectionTile onClick={select} />
                </div>
                <div className="FeatureCardList-Item">
                    <ReferralMarketplaceTile onClick={select} />
                </div>
                <div className="FeatureCardList-Item">
                    <PharmacyMedicationsTile onClick={select} />
                </div>
                <div className="FeatureCardList-Item">
                    <NonEmergencyTransportationTile onClick={select} />
                </div>
                <div className="FeatureCardList-Item">
                    <ClinicalPharmacistTile onClick={select} />
                </div>
            </div>
            <Modal
                isCentered
                isLightBackdrop
                hasFooter={false}
                isOpen={Boolean(selected)}
                className="FeatureSummaryViewer"
                onClose={unselect}
            >
                {selected === CONSENT && (
                    <ConsentSummary onClose={unselect} onDemo={onDemo} />
                )}
                {selected === PERSONAL_DASHBOARD && (
                    <PersonalDashboardSummary onClose={unselect} onDemo={onDemo} />
                )}
                {selected === TEAM_CARE && (
                    <TeamCareSummary onClose={unselect} onDemo={onDemo} />
                )}
                {selected === COMMUNICATIONS && (
                    <CommunicationsSummary onClose={unselect} onDemo={onDemo} />
                )}
                {selected === DOCUMENTS && (
                    <DocumentsSummary onClose={unselect} onDemo={onDemo} />
                )}
                {selected === CLINICAL_PHARMACIST && (
                    <ClinicalPharmacistSummary onClose={unselect} onDemo={onDemo} />
                )}
                {selected === NOTIFY && (
                    <NotifySummary onClose={unselect} onDemo={onDemo} />
                )}
                {selected === NOTIFY_FALL_DETECTION && (
                    <NotifyFallDetectionSummary onClose={unselect} onDemo={onDemo} />
                )}
                {selected === REFERRAL_MARKETPLACE && (
                    <ReferralMarketplaceSummary onClose={unselect} onDemo={onDemo} />
                )}
                {selected === PHARMACY_MEDICATIONS && (
                    <PharmacyMedicationsSummary onClose={unselect} onDemo={onDemo} />
                )}
                {selected === NON_EMERGENCY_TRANSPORTATION && (
                    <NonEmergencyTransportationSummary onClose={unselect} onDemo={onDemo} />
                )}
                {selected === HIPAA_COMPLIANT_CALENDAR && (
                    <HipaaCompliantCalendarSummary onClose={unselect} onDemo={onDemo} />
                )}
            </Modal>
            {isSuccessDialogOpen && (
                <SuccessDialog
                    isOpen
                    title="Thank you for submitting your request.
                     Our team will get back to you within one business day"
                    buttons={[
                        {
                            text: 'Close',
                            outline: true,
                            onClick: toggleSuccessDialog
                        },
                    ]}
                />
            )}
        </div>
    )
}

Features.defaultProps = {
    onSelect: noop
}

export default memo(Features)