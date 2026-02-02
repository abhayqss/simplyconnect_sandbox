import React from 'react'

import {
    Detail as BaseDetail
} from 'components/business/common'

import { isNotEmpty } from 'lib/utils/Utils'

import { DateUtils as DU } from 'lib/utils/Utils'

import './AllergyDetails.scss'

const { format, formats } = DU

const LONG_DATE_FORMAT = formats.longDateMediumTime12

const formatDate = date => format(date, LONG_DATE_FORMAT)

const STATUS_COLORS = {
    INACTIVE: '#e0e0e0',
    ACTIVE: '#d5f3b8',
    RESOLVED: '#ffedc2',
    UNKNOWN: '#fde1d5',
}

function Detail({ title, children }) {
    return (
        <BaseDetail
            title={title}
            titleClassName="AllergyDetail-Title"
            valueClassName="AllergyDetail-Value"
            className="AllergyDetail"
        >
            {children}
        </BaseDetail>
    )
}

export default function AllergyDetails({ data = {} }) {
    return isNotEmpty(data) && (
        <>
            <Detail title="Substance">
                {data.substance}
            </Detail>

            <Detail title="Type">
                {data.type}
            </Detail>

            <Detail title="Reaction">
                {data.reaction}
            </Detail>

            <Detail title="Severity">
                {data.severity}
            </Detail>

            <Detail title="Status">
                <div
                    className="AllergyDetail-Status"
                    style={{ backgroundColor: STATUS_COLORS[data.statusName] }}
                >
                    {data.statusTitle}
                </div>
            </Detail>

            <Detail title="Identified">
                {formatDate(data.identifiedDate)}
            </Detail>

            <Detail title="Stopped">
                {formatDate(data.stoppedDate)}
            </Detail>
            
            <Detail title="Data Source">
                {data.dataSource}
            </Detail>
        </>
    )
}