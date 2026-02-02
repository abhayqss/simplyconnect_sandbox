import React, { memo } from 'react'

import { Col, Row } from 'reactstrap'

import { TextField } from 'components/Form'

import NotificationSectionRow from './NotificationSectionRow'


function PersonNotificationSectionRow({
    data,
    name,
    errors = {},
    children,
    onChangeField,
    ...restProps
}) {
    let props = {
        data,
        name,
        errors,
        children,
        onChangeField,
        ...restProps
    }

    const withAsterisk = str => (
        data.isNotified ? (str + '*') : str
    )

    return (
        <NotificationSectionRow {...props}>
            <Row>
                <Col md={{ size: 4, offset: 4 }}>
                    <TextField
                        type="text"
                        name={`${name}.fullName`}
                        label={withAsterisk('First and last name')}
                        value={data.fullName}
                        className="IncidentReportForm-TextField"
                        maxLength={512}
                        isDisabled={!data.isNotified}
                        errorText={errors.fullName}
                        onChange={onChangeField}
                    />
                </Col>

                <Col md="4">
                    <TextField
                        type="text"
                        name={`${name}.phone`}
                        label={withAsterisk('Phone #')}
                        value={data.phone}
                        className="IncidentReportForm-TextField"
                        maxLength={16}
                        isDisabled={!data.isNotified}
                        errorText={errors.phone}
                        onChange={onChangeField}
                    />
                </Col>
            </Row>

            {children}
        </NotificationSectionRow>
    )
}

export default memo(PersonNotificationSectionRow)
