import React, { memo } from 'react'

import { Col, Row } from 'reactstrap'

import { TextField, DateField } from 'components/Form'

import PersonNotificationSectionRow from './PersonNotificationSectionRow'

function PhysicianNotificationSectionRow({
    data,
    name,
    errors = {},
    onChangeField,
    onChangeDateField,
    ...restProps
}) {
    let props = {
        data,
        name,
        errors,
        onChangeField,
        onChangeDateField,
        ...restProps
    }

    const withAsterisk = str => (
        data.isNotified ? (str + '*') : str
    )

    return (
        <PersonNotificationSectionRow {...props}>
            <Row>
                <Col md={{ size: 4, offset: 4 }}>
                    <TextField
                        type="text"
                        name={`${name}.response`}
                        label={withAsterisk(`Physician's response`)}
                        value={data.response}
                        className="IncidentReportForm-TextField"
                        maxLength={256}
                        isDisabled={!data.isNotified}
                        errorText={errors.response}
                        onChange={onChangeField}
                    />
                </Col>

                <Col md="4">
                    <DateField
                        name={`${name}.responseDate`}
                        hasTimeSelect
                        timeFormat="hh:mm aa"
                        dateFormat="MM/dd/yyyy hh:mm a"
                        className="IncidentReportForm-DateField"
                        value={data.responseDate}
                        label={withAsterisk(`Date & Time`)}
                        isDisabled={!data.isNotified}
                        errorText={errors.responseDate}
                        onChange={onChangeDateField}
                    />
                </Col>
            </Row>
        </PersonNotificationSectionRow>
    )
}

export default memo(PhysicianNotificationSectionRow)
