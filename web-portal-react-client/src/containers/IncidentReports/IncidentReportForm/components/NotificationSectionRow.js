import React, { memo } from 'react'

import { Col, Row } from 'reactstrap'

import {
    TextField,
    DateField,
    CheckboxField,
} from 'components/Form'

import { ReactComponent as Info } from 'images/info.svg'

const MAX_DATE = new Date()

function NotificationSectionRow({
    data,
    hint,
    name,
    label,
    errors = {},
    children,
    onChangeField,
    onChangeDateField
}) {
    const withAsterisk = str => (
        data.isNotified ? (str + '*') : str
    )

    let id = name.replace('.', '-')

    return (
        <>
            <Row className="IncidentReportForm-NotificationRow">
                <Col md="4" className="IncidentReportForm-NotificationRow-CheckboxCol">
                    <CheckboxField
                        label={label}
                        name={`${name}.isNotified`}
                        value={data.isNotified}
                        onChange={onChangeField}
                        className="IncidentReportForm-CheckboxField"
                        renderLabelIcon={() => (
                            hint ? (<Info
                                id={`${id}-hint`}
                                className="IncidentReportForm-LabelIcon"
                            />) : null
                        )}
                        tooltip={hint && {
                            target: `${id}-hint`,
                            text: hint
                        }}
                    />
                </Col>

                <Col md="4">
                    <DateField
                        name={`${name}.date`}
                        hasTimeSelect
                        timeFormat="hh:mm aa"
                        dateFormat="MM/dd/yyyy hh:mm a"
                        className="IncidentReportForm-DateField"
                        value={data.date}
                        placeholder="mm/dd/yyyy"
                        label={withAsterisk(`Date & Time`)}
                        isDisabled={!data.isNotified}
                        errorText={errors.date}
                        onChange={onChangeDateField}
                        maxDate={MAX_DATE}
                    />
                </Col>

                <Col md="4">
                    <TextField
                        type="text"
                        name={`${name}.byWhom`}
                        label={withAsterisk('By Whom')}
                        value={data.byWhom}
                        className="IncidentReportForm-TextField"
                        maxLength={256}
                        isDisabled={!data.isNotified}
                        errorText={errors.byWhom}
                        onChange={onChangeField}
                    />
                </Col>
            </Row>

            {children}
        </>
    )
}

export default memo(NotificationSectionRow)
