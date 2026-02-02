import React, { useMemo, useState, useEffect, useCallback } from 'react'

import cn from 'classnames'
import moment from 'moment'
import { range } from 'underscore'

import { Form, Col, Row, Button } from 'reactstrap'

import {
    useForm,
    useScrollable,
    useSelectOptions,
    useScrollToFormError
} from 'hooks/common'

import DateField from 'components/Form/DateField/DateField'
import SelectField from 'components/Form/SelectField/SelectField'
import CheckboxField from 'components/Form/CheckboxField/CheckboxField'

import EventRepeatEntity from 'entities/EventRepeat'
import EventRepeatValidator from 'validators/EventRepeatFormValidator'

import { isEmpty } from 'lib/utils/Utils'

import './EventRepeatForm.scss'

const scrollableStyles = { flex: 1, overflow: 'initial' }

const toggleListItem = (list, item) => list.includes(item)
    ? list.filter(s => s !== item)
    : [...list, item]

const [NEVER, EVERY_DAY, EVERY_WEEK, EVERY_MONTH] = ['NEVER', 'EVERY_DAY', 'EVERY_WEEK', 'EVERY_MONTH']
const WEEKDAYS = ['SUNDAY', 'MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY']

const formatWeekday = weekday => `${weekday[0]}${weekday[1].toLowerCase()}`

const FREQUENCY_RANGE = range(1, 99)

const TIME_UNIT_OPTIONS = [
    { text: 'Never', value: NEVER },
    { text: 'Every day', value: EVERY_DAY },
    { text: 'Every week', value: EVERY_WEEK },
    { text: 'Every month', value: EVERY_MONTH },
]

const FREQUENCY_RANGE_OPTIONS = FREQUENCY_RANGE.map(number => ({ text: number, value: number }))

const getData = fields => {
    let data = fields.toJS()

    return {
        ...data,
        weekdays: data.periodUnitName === EVERY_WEEK ? data.weekdays: null,
        until: data.noEndDate ? null : data.until,
    }
}

const getLastWeekday = weekdays => {
    let index = Math.max(
        ...weekdays.map(weekday => WEEKDAYS.findIndex(w => w === weekday))
    )

    return WEEKDAYS[index]
}

const WeekdayCheckboxes = ({ value = [], onChange }) => (
    <div className="EventRepeatForm-Weekdays">
        {WEEKDAYS.map((weekday, i) => (
            <div
                key={i}
                id={weekday}
                onClick={(event) => onChange(event.target.id)}
                className={cn(
                    'EventRepeatForm-Weekday',
                    'margin-right-16',
                    'margin-bottom-16',
                    { checked: value.includes(weekday) }
                )}
            >
                {formatWeekday(weekday)}
            </div>
        ))}
    </div>
)

function EventRepeatForm({
    data,
    onClose,
    defaults,
    onSubmitSuccess,
}) {
    const {
        fields,
        errors,
        validate,
        isChanged,
        clearField: onClearField,
        changeField: onChangeField,
        changeFields: onChangeFields,
        changeDateField: onChangeDateField
    } = useForm('RecurringAppointments', EventRepeatEntity, EventRepeatValidator)

    const [needValidation, setNeedValidation] = useState(false)

    const weekdays = useMemo(() => fields.weekdays.toArray(), [fields.weekdays])

    const validationOptions = useMemo(() => ({
        included: {}
    }), [])

    const { Scrollable, scroll } = useScrollable()

    function cancel() {
        onClose(isChanged)
    }

    function submit() {
        validate(validationOptions)
            .then(async () => {
                onSubmitSuccess(getData(fields))
                setNeedValidation(false)
            })
            .catch(() => {
                onScroll()
                setNeedValidation(true)
            })
    }

    function validateIf() {
        if (needValidation) {
            validate(validationOptions)
                .then(() => setNeedValidation(false))
                .catch(() => setNeedValidation(true))
        }
    }

    function setFormData() {
        let changes = isEmpty(data) ? {
            periodFrequency: 1,
            periodUnitName: NEVER,
            ...defaults
        } : data

        onChangeFields(changes, true)
    }

    function changeFieldsByPeriodName() {
        if (fields.periodUnitName === EVERY_WEEK) {
            onEveryWeekChoice()
        } else {
            onClearField('weekdays')
        }

        changeUntilFieldByPeriodUnit(fields.periodUnitName)
    }

    function changeFieldsByWeekdays() {
        if (weekdays?.length > 0 && fields.startDate) {
            let lastWeekday = getLastWeekday(weekdays)
            let until = moment(fields.startDate).clone().day(lastWeekday).add(9, 'w').toDate()

            onChangeDateField('until', until)
        }
    }

    const onScroll = useScrollToFormError('.EventRepeatForm', scroll)
    const onSubmit = useCallback(submit, [validate, fields, validationOptions])
    const onCancel = useCallback(cancel, [onClose, isChanged])

    const onEveryWeekChoice = useCallback(() => {
        if (fields.startDate) {
            let weekday = moment(fields.startDate).day()

            onChangeField('weekdays', [WEEKDAYS[weekday]])
        }
    }, [fields.startDate, onChangeField])

    const changeUntilFieldByPeriodUnit = useCallback((periodUnit) => {
        if (!fields.startDate) return

        let until = moment(fields.startDate).clone()

        switch (periodUnit) {
            case EVERY_DAY:
                until = until.add(9, 'd').toDate()
                break;

            case EVERY_WEEK: {
                until = until.add(9, 'w').toDate()
                break;
            }

            case EVERY_MONTH:
                until = until.add(9, 'M').toDate()
                break;

            default:
                until = null
                break;
        }

        onChangeDateField('until', until)
    }, [fields.startDate, onChangeDateField])

    const onChangeWeekdays = useCallback(id => {
        onChangeField('weekdays', toggleListItem(fields.weekdays, id))
    }, [
        onChangeField,
        fields.weekdays,
    ])

    useEffect(changeFieldsByPeriodName, [
        onClearField,
        onEveryWeekChoice,
        fields.periodUnitName,
        changeUntilFieldByPeriodUnit
    ])

    useEffect(changeFieldsByWeekdays, [
        weekdays,
        fields.startDate,
        onChangeDateField,
    ])

    useEffect(validateIf, [needValidation, onScroll, validate, validationOptions])

    useEffect(setFormData, [data, defaults, onChangeFields])

    return (
        <Form className="EventRepeatForm">
            <Scrollable style={scrollableStyles}>
                <div className="EventRepeatForm-Section">
                    <Row>
                        <Col md="8">
                            <DateField
                                name="startDate"
                                value={fields.startDate}
                                label="Start date"
                                className="EventRepeatForm-TextField"
                                errorText={errors.startDate}
                                onChange={onChangeDateField}
                            />
                        </Col>
                    </Row>

                    <Row>
                        <Col md="8">
                            <Row>
                                <Col sm="6">
                                    <SelectField
                                        name="periodFrequency"
                                        value={fields.periodFrequency}
                                        options={FREQUENCY_RANGE_OPTIONS}
                                        label="Repeat"
                                        className="EventRepeatForm-SelectField"
                                        errorText={errors.periodFrequency}
                                        onChange={onChangeField}
                                    />
                                </Col>
                                <Col sm="6">
                                    <SelectField
                                        name="periodUnitName"
                                        value={fields.periodUnitName}
                                        label="Every"
                                        options={TIME_UNIT_OPTIONS}
                                        className="EventRepeatForm-SelectField"
                                        errorText={errors.periodUnitName}
                                        onChange={onChangeField}
                                    />
                                </Col>
                            </Row>
                        </Col>
                    </Row>

                    <Row>
                        <Col>
                            {fields.periodUnitName === EVERY_WEEK && (
                                <WeekdayCheckboxes
                                    value={weekdays}
                                    onChange={onChangeWeekdays}
                                />
                            )}
                        </Col>
                    </Row>

                    <Row className="align-items-end">
                        <Col md="8">
                            <DateField
                                name="until"
                                value={fields.until}
                                label="Occurs every day until"
                                className="EventRepeatForm-TextField"
                                errorText={errors.until}
                                isDisabled={fields.noEndDate}
                                onChange={onChangeDateField}
                            />
                        </Col>

                        <Col md="4">
                            <CheckboxField
                                name="noEndDate"
                                label="No end date"
                                value={fields.noEndDate}
                                className="EventRepeatForm-CheckboxField"
                                onChange={onChangeField}
                            />
                        </Col>
                    </Row>
                </div>
            </Scrollable>

            <div className="EventRepeatForm-Buttons">
                <Button
                    color="success"
                    onClick={onSubmit}
                >
                    Save
                </Button>

                <Button
                    outline
                    color="success"
                    onClick={onCancel}
                >
                    Discard
                </Button>
            </div>
        </Form>
    )
}

export default EventRepeatForm
