import React, { memo } from 'react'

import { Col, Row } from 'reactstrap'

import { TextField } from 'components/Form'

import NotificationSectionRow from './NotificationSectionRow'

function OtherNotificationSectionRow({
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
                <Col md={{ size: 8, offset: 4 }}>
                    <TextField
                        type="text"
                        name={`${name}.comment`}
                        label={withAsterisk('Comment')}
                        value={data.comment}
                        className="IncidentReportForm-TextField"
                        maxLength={256}
                        isDisabled={!data.isNotified}
                        errorText={errors.comment}
                        onChange={onChangeField}
                    />
                </Col>
            </Row>

            {children}
        </NotificationSectionRow>
    )
}

export default memo(OtherNotificationSectionRow)
