import React, {
    useState
} from 'react'

import {
    compact
} from 'underscore'

import {
    Dropdown,
    Breadcrumbs,
    DataLoadable
} from 'components'

import {
    useParams,
    useLocation
} from 'react-router-dom'

import DocumentTitle from 'react-document-title'

import { useAuthUser } from 'hooks/common/redux'

import { useExternalProviderRoleCheck } from 'hooks/business/external'

import { Detail } from 'components/business/common'

import {
    useInquiryQuery
} from 'hooks/business/inquiry'

import { UpdateSideBarAction } from 'actions/clients'
import { UpdateSideBarAction as UpdateReferralSideBarAction } from 'actions/referrals'

import {
    REFERRAL_TYPES
} from 'lib/Constants'

import {
    isInteger,
    DateUtils as DU
} from 'lib/utils/Utils'

import ContactToIndividualConfirmPopup from './ContactToIndividualConfirmPopup/ContactToIndividualConfirmPopup'

import './InquiryDetails.scss'

const {
    INBOUND,
    OUTBOUND
} = REFERRAL_TYPES

const REFERRAL_TYPE_TITLES = {
    [INBOUND]: 'Inbound',
    [OUTBOUND]: 'Outbound'
}

const { format, formats } = DU

const DATE_FORMAT = formats.longDateMediumTime12

const InquiryDetails = () => {
    const [isContactToIndividualDialogOpen, setContactToIndividualDialogOpen] = useState(false)

    const user = useAuthUser()

    const { inquiryId, clientId } = useParams()

    const isClient = isInteger(parseInt(clientId))

    const isExternalProvider = useExternalProviderRoleCheck()

    const { pathname } = useLocation()

    const type = pathname.includes('inbound') ?
        INBOUND : OUTBOUND

    const friendlyName = REFERRAL_TYPE_TITLES[type]

    const {
        data,
        refetch,
        isFetching
    } = useInquiryQuery({ inquiryId })

    const onMarkAsDone = () => {
        setContactToIndividualDialogOpen(true)
    }

    const onCancelMarkAsDone = () => {
        setContactToIndividualDialogOpen(false)
    }

    const onSaveSuccessMarkAsDone = () => {
        setContactToIndividualDialogOpen(false)
        refetch()
    }

    const inquiryActions = [
        {
            value: 'MarkAsDone',
            text: 'Mark as done',
            onClick: onMarkAsDone,
            isVisible: data?.canMarkAsDone
        },
    ].filter(o => o.isVisible)

    return (
        <DocumentTitle
            title={`Simply Connect | Details of Inquiry`}
        >
            <div className='ReferralsInquiryDetails'>
                {user && (
                    <>
                        {isClient ? (
                            <UpdateSideBarAction params={{ clientId }} />
                        ) : (
                            <UpdateReferralSideBarAction />
                        )}
                    </>
                )}
                <DataLoadable
                    data={data}
                    noDataText='No Data'
                    isLoading={isFetching}
                >
                    {data => <>
                        <Breadcrumbs
                            items={compact([
                                {
                                    title: 'Referrals and Inquiries',
                                    href: `${isExternalProvider ? '/external-provider' : ''}/${type.toLowerCase()}-referrals`,
                                    isEnabled: isExternalProvider
                                },
                                ...!isExternalProvider ? [{
                                    title: friendlyName,
                                    href: `/${type.toLowerCase()}-referrals`,
                                    isEnabled: true
                                }] : [],
                                {
                                    title: 'Inquiry Details',
                                    href: `${isExternalProvider ? '/external-provider' : ''}/${type.toLowerCase()}-referrals/inquiries/${inquiryId}`,
                                    isActive: true
                                }
                            ])}
                            className="margin-bottom-40"
                        />
                        <div className='InquiryDetails-Header'>
                            <div className='InquiryDetails-Title'>
                                Inbound Inquiry
                            </div>
                            <div className="InquiryDetails-ControlPanel">
                                {
                                    inquiryActions.length > 0 && (
                                        <Dropdown
                                            items={inquiryActions}
                                            toggleText="Take Action"
                                            className="InquiryDetails-ActionDropdown"
                                        />
                                    )
                                }
                            </div>
                        </div>
                        <div className="InquiryDetails-Body">
                            <div className="InquiryDetails-Section">
                                <div className="InquiryDetails-SectionTitle">
                                    Inquiry
                                </div>
                                <>
                                    <Detail
                                        title="Inquiry Id"
                                        titleClassName="InquiryDetail-Title"
                                        valueClassName="InquiryDetail-Value"
                                        className="InquiryDetail"
                                    >
                                        {data.id}
                                    </Detail>
                                    <Detail
                                        title="Inquiry Date"
                                        titleClassName="InquiryDetail-Title"
                                        valueClassName="InquiryDetail-Value"
                                        className="InquiryDetail"
                                    >
                                        {format(data.createdDate, DATE_FORMAT)}
                                    </Detail>
                                    <Detail
                                        title="Status"
                                        titleClassName="InquiryDetail-Title"
                                        valueClassName="InquiryDetail-Value"
                                        className="InquiryDetail"
                                    >
                                        {data.statusTitle}
                                    </Detail>
                                    {data.contactedDate && (
                                        <Detail
                                            title="Date of contact"
                                            titleClassName="InquiryDetail-Title"
                                            valueClassName="InquiryDetail-Value"
                                            className="InquiryDetail"
                                        >
                                            {format(data.contactedDate, 'MM/dd/YYYY')}
                                        </Detail>
                                    )}
                                    <Detail
                                        title="Service"
                                        titleClassName="InquiryDetail-Title"
                                        valueClassName="InquiryDetail-Value"
                                        className="InquiryDetail"
                                    >
                                        {data.serviceTitle}
                                    </Detail>
                                    <Detail
                                        title="Phone #"
                                        titleClassName="InquiryDetail-Title"
                                        valueClassName="InquiryDetail-Value"
                                        className="InquiryDetail"
                                    >
                                        {data.phone}
                                    </Detail>
                                    <Detail
                                        title="Email"
                                        titleClassName="InquiryDetail-Title"
                                        valueClassName="InquiryDetail-Value"
                                        className="InquiryDetail"
                                    >
                                        {data.email}
                                    </Detail>
                                    <Detail
                                        title="Notes"
                                        titleClassName="InquiryDetail-Title"
                                        valueClassName="InquiryDetail-Value"
                                        className="InquiryDetail"
                                    >
                                        {data.notes}
                                    </Detail>
                                    <Detail
                                        title="Referring Community"
                                        titleClassName="InquiryDetail-Title"
                                        valueClassName="InquiryDetail-Value"
                                        className="InquiryDetail"
                                    >
                                        {data.communityName}
                                    </Detail>
                                </>
                            </div>
                            <div className='InquiryDetails-Section'>
                                <div className="InquiryDetails-SectionTitle">
                                    Client information
                                </div>
                                <>
                                    <Detail
                                        title="Name"
                                        titleClassName="InquiryDetail-Title"
                                        valueClassName="InquiryDetail-Value"
                                        className="InquiryDetail"
                                    >
                                        {`${data.firstName} ${data.lastName}`}
                                    </Detail>
                                </>
                            </div>
                        </div>
                    </>}
                </DataLoadable>

                <ContactToIndividualConfirmPopup
                    isOpen={isContactToIndividualDialogOpen}
                    inquiryId={data?.id}
                    inquiryDate={data?.createdDate}
                    onClose={onCancelMarkAsDone}
                    onSaveSuccess={onSaveSuccessMarkAsDone}
                />
            </div>
        </DocumentTitle>
    )
}

export default InquiryDetails