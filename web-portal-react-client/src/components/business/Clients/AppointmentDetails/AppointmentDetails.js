import React from 'react'
import { Link } from 'react-router-dom'

import { AlertPanel } from 'components'

import { SwitchField } from 'components/Form'

import {
    Detail as BaseDetail
} from 'components/business/common'

import {
    isEmpty,
    isNotEmpty,
    DateUtils as DU,
} from 'lib/utils/Utils'

import {
    unshiftIf
} from 'lib/utils/ArrayUtils'

import {
    isString
} from 'lib/utils/StringUtils'

import {
    path
} from 'lib/utils/ContextUtils'

import './AppointmentDetails.scss'

const { format } = DU

const formatDate = date => format(date, DU.formats.americanMediumDate)

function Detail({ children, ...props }) {
    const value = isString(children) ? children.trim() : children

    if (isEmpty(value)) return null

    return (
        <BaseDetail
            {...props}
            className="AppointmentDetail"
            titleClassName="AppointmentDetail-Title"
            valueClassName="AppointmentDetail-Value"
        >
            {value}
        </BaseDetail>
    )
}

export default function AppointmentDetails({ data = {} }) {
    return (
        <>
            <div className="AppointmentDetails-Section">
                <div
                    id="appointment-details__main"
                    className="AppointmentDetails-SectionAnchor"
                />
                <div className="d-flex justify-content-between margin-bottom-16">
                    <div className="AppointmentDetails-SectionTitle">
                        Main Information
                    </div>
                </div>

                <Detail title="APPOINTMENT TITLE">
                    {data.title}
                </Detail>

                <Detail title="APPOINTMENT STATUS">
                    {data.statusTitle}
                </Detail>

                <Detail title="ORGANIZATION">
                    {data.organizationName}
                </Detail>

                <Detail title="COMMUNITY">
                    {data.communityName}
                </Detail>

                <Detail title="LOCATION">
                    {data.location}
                </Detail>

                <Detail title="APPOINTMENT TYPE">
                    {data.typeTitle}
                </Detail>

                <Detail title="SERVICE CATEGORY">
                    {data.serviceCategoryTitle}
                </Detail>

                <Detail title="REFERRAL SOURCE">
                    {data.referralSource}
                </Detail>

                <Detail title="REASON FOR VISIT">
                    {data.reasonForVisit}
                </Detail>

                <Detail title="APPOINTMENT DIRECTIONS & INSTRUCTIONS">
                    {data.directionsInstructions}
                </Detail>

                <Detail title="NOTES">
                    {data.notes}
                </Detail>

                <Detail title="PUBLIC CALENDAR">
                    <SwitchField
                        name="isPublic"
                        isDisabled
                        isChecked={data.isPublic}
                        className="AppointmentDetails-SwitchField"
                    />
                </Detail>

            </div>

            <div className="AppointmentDetails-Section">
                <div
                    id="appointment-details__schedule"
                    className="AppointmentDetails-SectionAnchor"
                />
                <div className="d-flex justify-content-between margin-bottom-16">
                    <div className="AppointmentDetails-SectionTitle">
                        Schedule & Resources
                    </div>
                </div>

                {isNotEmpty(data.clientName) && (
                    <Detail title="CLIENT">
                        {data.canViewClient ? (
                            <Link
                                className="link"
                                to={path(`/clients/${data.clientId}`)}
                            >
                                {data.clientName}
                            </Link>) : data.clientName}
                        {data.clientGender && `, ${data.clientGender}`}
                        {data.clientDOB && `, ${data.clientDOB}`}
                    </Detail>
                )}

                <Detail title="CREATOR">
                    {data.creatorName}
                </Detail>

                <Detail title="SERVICE PROVIDER">
                    {unshiftIf(
                            data.serviceProviderNames ?? [],
                            'External Provider',
                            data.isExternalProviderServiceProvider
                        ).join(", ")
                    }
                </Detail>

                <Detail title="APPOINTMENT DATE">
                    {formatDate(data.date)}
                </Detail>

                <Detail title="APPOINTMENT TIME">
                    {data.time}
                </Detail>

            </div>

            <div className="AppointmentDetails-Section">
                <div
                    id="appointment-details__reminders"
                    className="AppointmentDetails-SectionAnchor"
                />
                <div className="d-flex justify-content-between margin-bottom-16">
                    <div className="AppointmentDetails-SectionTitle">
                        Reminders
                    </div>
                </div>

                <AlertPanel className="AppointmentDetails-AlertPanel">
                    Client / Community Care Team Members can set up notifications about new, 
                    updated or cancelled appointments through Care Team Settings. If you 
                    don’t want to receive notifications, select Not viewable responsibility. 
                    Client notifications can be set up in the current section.
                </AlertPanel>

                <Detail title="CLIENT REMINDER">
                    {data.reminderTitles?.join(", ")}
                </Detail>

                <Detail title="NOTIFICATION METHOD">
                    {data.notificationMethodTitles?.join(", ")}
                </Detail>

                <Detail title="EMAIL">
                    {data.email}
                </Detail>

                <Detail title="CELL PHONE #">
                    {data.phone}
                </Detail>
            </div>
        </>
    )
}