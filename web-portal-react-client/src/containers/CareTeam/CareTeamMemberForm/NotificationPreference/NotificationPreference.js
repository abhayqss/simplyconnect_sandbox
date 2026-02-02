import React, { memo } from 'react'

import {
    Col,
    Row,
    UncontrolledTooltip as Tooltip
} from 'reactstrap'

import { ResponsibilityInfo } from 'components/info'

import SelectField from 'components/Form/SelectField/SelectField'

import { NOTIFICATION_RESPONSIBILITY_TYPES } from 'lib/Constants'

import { ReactComponent as Info } from 'images/info.svg'

import './NotificationPreference.scss'

const {
    VIEWABLE,
    NOT_VIEWABLE
} = NOTIFICATION_RESPONSIBILITY_TYPES

const COVID19 = 'COVID19'

function NotificationPreference({
    data,
    name,
    title,
    errors,
    channels,
    showLabel,
    isDisabledResp,
    isDisabledChannel,
    placeholder,
    onChangeChannel,
    responsibilities,
    onChangeResponsibility,
}) {
    if (name === COVID19) {
        responsibilities = responsibilities.map(o => ({
            ...o,
            isDisabled: [VIEWABLE, NOT_VIEWABLE].includes(o.name)
                ? true
                : o.isDisabled
        }))
    }

    return (
        <Row className="NotificationPreference">
            <Col md={6} className="NotificationPreference-Title">
                {title}
            </Col>

            <Col md={3}>
                <SelectField
                    label={showLabel ? 'Responsibility' : ''}
                    className="NotificationPreference-Select"
                    placeholder={placeholder ?? 'Nothing selected'}
                    name={`${data.eventTypeId}-responsibility`}
                    value={data.responsibilityName}
                    options={responsibilities}
                    onChange={(name, value) => {
                        onChangeResponsibility(data.eventTypeId, value)
                    }}
                    renderLabelIcon={() => (
                        <Info
                            id={`responsibility-info-hint-${name}`}
                            className="NotificationPreference-SelectLabelIcon"
                        />
                    )}
                    isDisabled={isDisabledResp}
                    hasError={!!errors?.responsibilityName}
                    errorText={errors?.responsibilityName}
                />
                {showLabel && (
                    <Tooltip
                        trigger="hover click"
                        boundariesElement={document.body}
                        className="ResponsibilityInfoHint"
                        target={`responsibility-info-hint-${name}`}
                        modifiers={[
                            {
                                name: 'offset',
                                options: { offset: [0, 6] }
                            },
                            {
                                name: 'preventOverflow',
                                options: { boundary: document.body }
                            }
                        ]}
                    >
                        <ResponsibilityInfo/>
                    </Tooltip>
                )}
            </Col>

            <Col md={3}>
                <SelectField
                    isMultiple
                    label={showLabel ? 'Channel' : ''}
                    className="NotificationPreference-Select"
                    placeholder={placeholder ?? 'Nothing selected'}
                    name={`${data.eventTypeId}-channel`}
                    value={data.channels}
                    options={channels}
                    onChange={(name, value) => {
                        onChangeChannel(data.eventTypeId, value)
                    }}
                    errorText={errors?.channels}
                    hasError={!!errors?.channels}
                    isDisabled={
                        isDisabledChannel || [VIEWABLE, NOT_VIEWABLE].includes(data.responsibilityName)
                    }
                />
            </Col>
        </Row>
    )
}

export default memo(NotificationPreference)
