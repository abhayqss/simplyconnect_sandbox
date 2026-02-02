import React, {
    useMemo,
    useState,
    useCallback
} from 'react'

import cn from 'classnames'

import { map } from 'underscore'

import { connect } from 'react-redux'

import {
    Tabs,
    Loader,
    Dropdown,
    IconButton,
    DataLoadable
} from 'components'

import {
    SuccessDialog,
    WarningDialog
} from 'components/dialogs'

import {
    AssessmentDataDetails,
    ClientDemographicsDetails
} from 'components/business/Clients'

import Avatar from 'containers/Avatar/Avatar'
import ContactViewer from 'containers/Admin/Contacts/ContactViewer/ContactViewer'
import ContactEditor from 'containers/Admin/Contacts/ContactEditor/ContactEditor'

import {
    BillingDetails
} from 'containers/common/details'

import { ReactComponent as HL7WarningIcon } from 'images/warning-hl7.svg'

import {
    pushIf
} from 'lib/utils/ArrayUtils'

import {
    MedicalContactList,
    HouseholdMemberList
} from '../'

import EmergencyContacts from '../EmergencyContacts/EmergencyContacts'

import './ClientDetails.scss'
import FreshEmergencyContacts from "../FreshEmergencyContacts/FreshEmergencyContacts";

const TAB = {
    DEMOGRAPHICS: 0,
    EMERGENCY_CONTACTS: 1,
    MEDICAL_CONTACTS: 2,
    BILLING_INFO: 3,
    HOUSEHOLD_MEMBERS: 4,
    ASSESSMENT_DATA: 5,
    CONTACTS: 6,
    POA:7,
}

const TAB_TITLE = {
    [TAB.DEMOGRAPHICS]: 'Demographics',
    [TAB.POA]: 'Power of Attorney',
    [TAB.EMERGENCY_CONTACTS]: 'Emergency Contacts',
    [TAB.MEDICAL_CONTACTS]: 'Medical Contacts',
    [TAB.BILLING_INFO]: 'Billing Info',
    [TAB.HOUSEHOLD_MEMBERS]: 'Household Members',
    [TAB.ASSESSMENT_DATA]: 'Assessment Data',
}

const BASE_TABS = [
    { title: TAB_TITLE[TAB.DEMOGRAPHICS], value: 0 },
    { title: TAB_TITLE[TAB.POA], value:7 },
    { title: TAB_TITLE[TAB.EMERGENCY_CONTACTS], value: 1},
    { title: TAB_TITLE[TAB.MEDICAL_CONTACTS], value: 2 },
    { title: TAB_TITLE[TAB.BILLING_INFO], value: 3 },
    { title: TAB_TITLE[TAB.HOUSEHOLD_MEMBERS], value: 4 },
]

function mapStateToProps(state) {
    const {
        details
    } = state.client

    return {
        data: details.data,
        error: details.error,
        isFetching: details.isFetching,
        shouldReload: details.shouldReload
    }
}

function ClientDetails({ data, isFetching, clientId, onRefresh }) {
    const [tab, setTab] = useState(TAB.DEMOGRAPHICS)
    const [isContactViewerOpen, toggleContactViewer] = useState(false)
    const [isContactEditorOpen, toggleContactEditor] = useState(false)
    const [isSaveContactSuccessDialogOpen, toggleSaveContactSuccessDialog] = useState(false)
    const [isPharmacySyncSourceConfirmDialogOpen, togglePharmacySyncSourceConfirmDialog] = useState(false)

    const contact = data?.associatedContact

    const options = useMemo(() => {
        let tabs = map(BASE_TABS, o => ({
            ...o,
            text: o.title,
            isActive: o.value === tab,
            onClick: () => setTab(o.value)
        }))

        tabs = pushIf(tabs, {
            value: TAB.ASSESSMENT_DATA,
            text: TAB_TITLE[TAB.ASSESSMENT_DATA],
            isActive: tab === TAB.ASSESSMENT_DATA,
            onClick: () => setTab(TAB.ASSESSMENT_DATA)
        }, data?.canViewAssessmentData)

        return tabs
    }, [tab, data])

    const tabs = useMemo(() => {
        let tabs = map(BASE_TABS, o => ({
            ...o, isActive: o.value === tab
        }))

        tabs = pushIf(tabs, {
            title: TAB_TITLE[TAB.ASSESSMENT_DATA],
            value: TAB.ASSESSMENT_DATA,
            isActive: tab === TAB.ASSESSMENT_DATA
        }, data?.canViewAssessmentData)

        return tabs
    }, [tab, data])

    const onChangeTab = useCallback(tab => setTab(tab), [])

    const onOpenContactViewer = useCallback(() => {
        toggleContactViewer(true)
    }, [])

    const onCreateContact = useCallback(() => {
        if (data.isHL7) {
            togglePharmacySyncSourceConfirmDialog(true)
        } else toggleContactEditor(true)
    }, [data])

    const onSaveContactSuccess = useCallback(() => {
        onRefresh()
        toggleSaveContactSuccessDialog(true)
    }, [onRefresh])

    return (
        <div className="ClientDetails">
            {isFetching ? (
                <Loader/>
            ) : (
                <>
                    <div className='d-flex margin-bottom-40'>
                        <div className="margin-right-15">
                            <Avatar
                                size={75}
                                alt="Avatar"
                                id={data.avatarId}
                                name={data.fullName}
                                className={cn(
                                    'ClientDetails-Avatar',
                                    !data.isActive && 'black-white-filter'
                                )}
                            />
                        </div>
                        <div className='d-inline-block'>
                            <div className='ClientDetails-FullName'>
                                {data.fullName}
                                {data.isHL7 && (
                                    <IconButton
                                        size={25}
                                        tipPlace='top'
                                        Icon={HL7WarningIcon}
                                        shouldHighLight={false}
                                        name={`HL7WarningIcon-${data.id}`}
                                        className="margin-left-10"
                                        tipText="Client's record was created through Pharmacy sync, thus some data might be missing."
                                    />
                                )}
                            </div>
                            <div className='ClientDetails-SSN'>
                                {data.ssnLastFourDigits && `###-##-${data.ssnLastFourDigits}`}
                            </div>
                            <div
                                className='ClientDetails-Status'
                                style={{ backgroundColor: data.isActive ? '#d5f3b8' : '#e0e0e0' }}
                            >
                                {data.isActive ? 'Active' : 'Inactive'}
                            </div>
                        </div>
                    </div>

                    {tabs.length <= 5 && (
                        <Tabs
                            items={tabs}
                            className="ClientDetails-Tabs"
                            onChange={onChangeTab}
                        />
                    )}

                    <Dropdown
                        value={tab}
                        items={options}
                        toggleText={TAB_TITLE[tab]}
                        className={cn(
                            'ClientDetails-Dropdown',
                            'Dropdown_theme_blue',
                            options.length >= 6 && 'd-block'
                        )}
                    />
                    <div className="padding-top-30">
                        {tab === TAB.DEMOGRAPHICS && (
                            <DataLoadable
                                data={data}
                                isLoading={isFetching}
                            >
                                {data => (
                                    <ClientDemographicsDetails
                                        data={data}
                                        onViewContact={onOpenContactViewer}
                                        onCreateContact={onCreateContact}
                                    />
                                )}
                            </DataLoadable>
                        )}


                        {tab === TAB.POA && (
                            <EmergencyContacts clientId={clientId} />
                        )}

                        {tab === TAB.EMERGENCY_CONTACTS && (
                            <FreshEmergencyContacts clientId={clientId} />
                        )}

                        {tab === TAB.MEDICAL_CONTACTS && (
                            <MedicalContactList clientId={clientId}/>
                        )}

                        {tab === TAB.BILLING_INFO && (
                            <BillingDetails clientId={clientId}/>
                        )}
                        {tab === TAB.HOUSEHOLD_MEMBERS && (
                            <HouseholdMemberList clientId={clientId}/>
                        )}
                        {tab === TAB.ASSESSMENT_DATA && (
                            <AssessmentDataDetails data={data.assessmentData}/>
                        )}

                    </div>
                </>
            )}

            {isContactViewerOpen && (
                <ContactViewer
                    isOpen
                    contactId={contact.id}
                    onClose={() => toggleContactViewer(false)}
                />
            )}

            {isContactEditorOpen && (
                <ContactEditor
                    isOpen
                    clientId={clientId}
                    onSaveSuccess={onSaveContactSuccess}
                    onClose={() => toggleContactEditor(false)}
                />
            )}

            {isSaveContactSuccessDialogOpen && (
                <SuccessDialog
                    isOpen
                    title="Contact has been created."
                    buttons={[
                        {
                            text: 'Close',
                            outline: true,
                            className: 'min-width-120 margin-left-80',
                            onClick: () => {
                                toggleContactEditor(false)
                                toggleSaveContactSuccessDialog(false)
                            }
                        },
                        {
                            text: 'View Details',
                            className: 'min-width-120 margin-right-80',
                            onClick: () => {
                                toggleContactEditor(false)
                                toggleContactViewer(true)
                                toggleSaveContactSuccessDialog(false)
                            }
                        }
                    ]}
                />
            )}

            {isPharmacySyncSourceConfirmDialogOpen && (
                <WarningDialog
                    isOpen
                    title="Client's record was created through Pharmacy sync."
                    buttons={[
                        {
                            text: 'Cancel',
                            outline: true,
                            className: 'min-width-120 margin-left-80',
                            onClick: () => {
                                togglePharmacySyncSourceConfirmDialog(false)
                            }
                        },
                        {
                            text: 'OK',
                            className: 'min-width-120 margin-right-80',
                            onClick: () => {
                                toggleContactEditor(true)
                                togglePharmacySyncSourceConfirmDialog(false)
                            }
                        }
                    ]}
                />
            )}
        </div>
    )
}

export default connect(mapStateToProps)(ClientDetails)
