import React, {
    memo
} from 'react'

import cn from 'classnames'
import PTypes from 'prop-types'

import { Col, Row } from 'reactstrap'

import { Link } from 'react-router-dom'

import {
    Scrollable
} from 'components'

import {
    Button
} from 'components/buttons'

import {
    APPOINTMENT_TYPE_COLOR_SCHEME
} from 'lib/Constants'

import {
    isEmpty,
    isNotEmpty
} from 'lib/utils/Utils'

import {
    isString
} from 'lib/utils/StringUtils'

import {
    noop
} from 'lib/utils/FuncUtils'

import {
    format,
    formats
} from 'lib/utils/DateUtils'

import {
    path
} from 'lib/utils/ContextUtils'

import './AppointmentDetails.scss'

const DATE_FORMAT = formats.americanMediumDate
const TIME_FORMAT = formats.time2

function Detail(
    {
        title,
        children,
        className,
        titleClassName,
        valueClassName
    }
) {
    const value = isString(children) ? children.trim() : children

    if (isEmpty(value)) return null

    return (
        <Row className={cn('Detail AppointmentDetail', 'Detail_layout_h', className)}>
            <Col sm={6} md={5} lg={4}>
                <div className={cn('Detail-Title AppointmentDetail-Title', titleClassName)}>{title}</div>
            </Col>
            <Col sm={6} md={7} lg={8}>
                <div className={cn('Detail-Value AppointmentDetail-Value', valueClassName)}>
                    {value}
                </div>
            </Col>
        </Row>
    )
}

function AppointmentDetails(
    {
        data,
        className,

        onView,
        onEdit,
        onCancel,
        onDuplicate
    }
) {
    return data && (
        <div className={cn('AppointmentDetails', className)}>
            <Scrollable style={{ flex: 1 }}>
                <div className="margin-bottom-28">
                    <div className="AppointmentDetails-Title">
                        {data.title}
                    </div>
                    {data.typeName && (
                        <div
                            className="AppointmentDetails-Type"
                            style={{ backgroundColor: APPOINTMENT_TYPE_COLOR_SCHEME[data.typeName].value }}
                        >
                            {data.typeTitle}
                        </div>
                    )}
                </div>
                <div className="container margin-bottom-28">
                    <Detail title="Status">{data.statusTitle}</Detail>
                    {isNotEmpty(data.clientName) && (
                        <Detail title="Client">
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
                    <Detail title="Creator">{data.creator}</Detail>
                    <Detail title="Service Provider">
                        {data.serviceProviders?.join(',')}
                    </Detail>
                    {isNotEmpty(data.dateFrom) && (
                        <Detail title="Date">
                            {format(data.dateFrom, DATE_FORMAT)}, {format(data.dateFrom, TIME_FORMAT)} - {format(data.dateTo, TIME_FORMAT)}
                        </Detail>
                    )}
                    {isNotEmpty(data.location) && (
                        <Detail title="Location">
                            {data.location}, {data.communityName}
                        </Detail>
                    )}
                    <Detail title="Reason">
                        {data.reasonForVisit}
                    </Detail>
                    <Detail title="Appointment Directions & Instructions">
                        {data.directionsInstructions}
                    </Detail>
                    <Detail title="Cancellation Reason">
                        {data.cancellationReason}
                    </Detail>
                </div>
            </Scrollable>

            <div className="AppointmentDetails-Actions">
                {data.canView && (
                    <Button
                        outline
                        color="success"
                        onClick={() => onView(data)}
                        className="AppointmentDetails-Action width-100"
                    >
                        View
                    </Button>
                )}
                {data.canEdit && (
                    <Button
                        outline
                        color="success"
                        onClick={() => onEdit(data)}
                        className="AppointmentDetails-Action width-100"
                    >
                        Edit
                    </Button>
                )}
                {data.canCancel && (
                    <Button
                        color="success"
                        onClick={() => onCancel(data)}
                        className="AppointmentDetails-Action"
                    >
                        Cancel <span className="AppointmentDetails-ActionEntityName">&nbsp;Appointment</span>
                    </Button>
                )}
                {data.canDuplicate && (
                    <Button
                        color="success"
                        onClick={() => onDuplicate(data)}
                        className="AppointmentDetails-Action"
                    >
                        Duplicate <span className="AppointmentDetails-ActionEntityName">&nbsp;Appointment</span>
                    </Button>
                )}
            </div>
        </div>
    )
}

AppointmentDetails.propTypes = {
    data: PTypes.object,
    className: PTypes.string,
    onView: PTypes.func,
    onEdit: PTypes.func,
    onCancel: PTypes.func,
    onDuplicate: PTypes.func
}

AppointmentDetails.defaultProps = {
    onView: noop,
    onEdit: noop,
    onCancel: noop,
    onDuplicate: noop
}

export default memo(AppointmentDetails)