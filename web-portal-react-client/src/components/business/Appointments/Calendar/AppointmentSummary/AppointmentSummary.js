import React, {
    memo
} from 'react'

import PTypes from 'prop-types'

import cn from 'classnames'

import {
    diff
} from 'date-arithmetic'

import { Link } from 'react-router-dom'

import {
    Button
} from 'components/buttons'

import {
    noop
} from 'lib/utils/FuncUtils'

import {
    format,
    formats,
    formatDuration
} from 'lib/utils/DateUtils'

import {
    APPOINTMENT_TYPE_COLOR_SCHEME
} from 'lib/Constants'

import { path } from 'lib/utils/ContextUtils'

import './AppointmentSummary.scss'

function AppointmentSummary({ data, className, onView }) {
    function _onView(e){
        onView(data, e)
    }

    return (
        <div className={cn("AppointmentSummary", className)}>
            <div className="AppointmentSummary-Info">
                <div className="AppointmentSummary-Time">
                    <div className="AppointmentSummary-TimeIndicator"/>
                    <div className="v-flexbox align-items-start">
                        <div className="AppointmentSummary-StartTime">
                            {format(data.dateFrom, formats.time2)}
                        </div>
                        <div className="AppointmentSummary-Duration">
                            {formatDuration(diff(
                                    new Date(data.dateFrom),
                                    new Date(data.dateTo),
                                    'milliseconds'
                                ), 'ms', 'h[h] m[min]'
                            ).replace('mins', 'min')}
                        </div>
                    </div>
                </div>
                <div className="v-flexbox align-items-start padding-left-10 padding-right-10">
                    <div className="AppointmentSummary-Client">
                        {data.canViewClient ? (
                            <Link
                                className="link"
                                to={path(`/clients/${data.clientId}`)}
                            >
                                {data.clientName}
                            </Link>) : data.clientName}
                    </div>
                    <div className="AppointmentSummary-Location">
                        {data?.communityName}{data.location ? `, ${data.location}` : ''}
                    </div>
                    {data.typeName && (
                        <div
                            className="AppointmentSummary-Type"
                            style={{ backgroundColor: APPOINTMENT_TYPE_COLOR_SCHEME[data.typeName].value }}
                        >
                            {data.typeTitle}
                        </div>
                    )}
                </div>
            </div>
            <div className="AppointmentSummary-Actions">
                {data.canView && (
                    <Button
                        outline
                        color="success"
                        disabled={!data.canView}
                        className="AppointmentSummary-Action"
                        onClick={_onView}
                    >
                        View
                    </Button>
                )}
            </div>
        </div>
    )
}

AppointmentSummary.propTypes = {
    data: PTypes.object,
    onView: PTypes.func,
    onViewClient: PTypes.func
}

AppointmentSummary.defaultProps = {
    onView: noop,
    onViewClient: noop
}

export default memo(AppointmentSummary)