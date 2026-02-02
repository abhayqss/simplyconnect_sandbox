import React, {
    useMemo,
    useState,
    useEffect,
    useCallback
} from 'react'

import cn from 'classnames'
import moment from 'moment'
import { map, noop } from 'underscore'

import { Row, Col, Button } from 'reactstrap'

import { useDirectoryData } from 'hooks/common'

import { useCustomFilter } from 'hooks/common/filter'

import { useAuthUser } from 'hooks/common/redux'

import {
    useStatesQuery,
    useGendersQuery
} from 'hooks/business/directory'

import {
    TextField,
    DateField,
    SelectField
} from 'components/Form'

import { remote } from 'config'

import Entity from 'entities/ClientRecordFilter'
import { ClientRecordFilterValidator as Validator } from 'validators'

import { DateUtils as DU } from 'lib/utils/Utils'

import { testClient } from './Constants'

import './ClientRecordFilter.scss'

const { format, formats } = DU

export const NAME = 'CLIENT_RECORD_FILTER'

function formatDate(date) {
    return format(date, formats.americanMediumDate)
}

function stringToDate(str) {
    return str && moment(str, 'MM/DD/YYYY').toDate().getTime()
}

function valueTextMapper({ id, name, title, label }) {
    return { value: id || name, text: title || label || name }
}

export default function ClientRecordFilter(
    {
        canReApply = false,
        canReReset = false,
        className,
        canRequestAccess = false,
        onChange = noop,
        onApply = noop,
        onRestore = noop,
        onReset = noop
    }
) {
    const [isValidationNeed, setValidationNeed] = useState(false)

    const user = useAuthUser()

    const {
        data,
        blur,
        focus,
        reset,
        apply,
        errors,
        validate,
        changeField,
        changeFields
    } = useCustomFilter(
        NAME,
        Entity,
        {
            onChange,
            onApply,
            onReset,
            onRestore,
            canReReset,
            canReApply,
            Validator,
            onPressEnterKey: () => {
                applyIfValid()
            }
        }
    )

    useStatesQuery()
    useGendersQuery()

    const {
        states,
        genders
    } = useDirectoryData({
        states: ['state'],
        genders: ['gender']
    })

    const mappedStates = useMemo(
        () => map(states, valueTextMapper), [states]
    )

    const mappedGenders = useMemo(
        () => map(genders, valueTextMapper), [genders]
    )

    const validationOptions = useMemo(() => (
        {
            included: {
                canRequestAccess
            }
        }
    ), [canRequestAccess])

    function validateIf() {
        if (isValidationNeed) {
            validate(validationOptions)
                .then(() => setValidationNeed(false))
                .catch(() => setValidationNeed(true))
        }
    }

    const applyIfValid = useCallback(() => {
        validate(validationOptions)
            .then(() => apply())
            .catch(() => setValidationNeed(true))
    }, [apply, validate, validationOptions])

    const onChangeDateField = useCallback((name, value) => {
        changeField(name, value ? formatDate(value) : null, false)
    }, [changeField])

    /*
    * CCN-5931. Test Data for Craig's demo
    * */
    useEffect(() => {
        if (
            user
            && user.organizationId === 3023
            && remote.url.includes('dev.simplyconnect.me')
        ) {
            changeFields(testClient)
        }
    }, [user, changeFields])

    useEffect(validateIf, [isValidationNeed, validationOptions, validate, data])

    return (
        <div className={cn('ClientRecordFilter', className)}>
            <Row>
                <Col lg={6}>
                    <Row>
                        <Col lg={6} md={6}>
                            <TextField
                                type="text"
                                name="firstName"
                                value={data.firstName}
                                label="First Name*"
                                className="ClientRecordFilter-TextField"
                                errorText={errors?.firstName}
                                maxLength={256}
                                onBlur={blur}
                                onFocus={focus}
                                onChange={changeField}
                            />
                        </Col>
                        <Col lg={6} md={6}>
                            <TextField
                                type="text"
                                name="lastName"
                                value={data.lastName}
                                label="Last Name*"
                                className="ClientRecordFilter-TextField"
                                errorText={errors?.lastName}
                                maxLength={256}
                                onBlur={blur}
                                onFocus={focus}
                                onChange={changeField}
                            />
                        </Col>
                    </Row>
                </Col>
                <Col lg={6}>
                    <Row>
                        <Col lg={4} md={4}>
                            <SelectField
                                name="genderId"
                                value={data.genderId}
                                options={mappedGenders}
                                label="Gender*"
                                placeholder="Gender"
                                className="ClientRecordFilter-SelectField"
                                errorText={errors?.genderId}
                                isMultiple={false}
                                onChange={changeField}
                            />
                        </Col>
                        <Col lg={4} md={4}>
                            <DateField
                                name="birthDate"
                                value={stringToDate(data.birthDate)}
                                dateFormat="MM/dd/yyyy"
                                label="Date of Birth*"
                                placeholder="Select date"
                                className="ClientRecordFilter-DateField"
                                errorText={errors?.birthDate}
                                onChange={onChangeDateField}
                            />
                        </Col>
                        <Col lg={4} md={4}>
                            <TextField
                                type="text"
                                name="ssnLast4"
                                value={data.ssnLast4}
                                label={`SSN${canRequestAccess ? '' : '*'}`}
                                maxLength={4}
                                placeholder="Last 4 digits"
                                className="ClientRecordFilter-TextField"
                                errorText={errors?.ssnLast4}
                                onBlur={blur}
                                onFocus={focus}
                                onChange={changeField}
                            />
                        </Col>
                    </Row>
                </Col>
            </Row>
            {!canRequestAccess && (
                <Row>
                    <Col md={6}>
                        <Row>
                            <Col lg={6} md={6}>
                                <TextField
                                    type="text"
                                    name="middleName"
                                    value={data.middleName}
                                    label="Middle Name"
                                    className="ClientRecordFilter-TextField"
                                    maxLength={256}
                                    onBlur={blur}
                                    onFocus={focus}
                                    onChange={changeField}
                                />
                            </Col>
                            <Col lg={6} md={6}>
                                <TextField
                                    type="text"
                                    name="street"
                                    value={data.street}
                                    label="Street Address"
                                    maxLength={256}
                                    className="ClientRecordFilter-TextField"
                                    onBlur={blur}
                                    onFocus={focus}
                                    onChange={changeField}
                                />
                            </Col>
                        </Row>
                    </Col>
                    <Col md={6}>
                        <Row>
                            <Col md={4} lg={4}>
                                <TextField
                                    type="text"
                                    name="city"
                                    value={data.city}
                                    label="City"
                                    maxLength={256}
                                    className="ClientRecordFilter-TextField"
                                    onBlur={blur}
                                    onFocus={focus}
                                    onChange={changeField}
                                />
                            </Col>
                            <Col md={4} lg={4}>
                                <TextField
                                    type="text"
                                    name="zip"
                                    value={data.zip}
                                    label="Zip Code"
                                    maxLength={5}
                                    className="ClientRecordFilter-TextField"
                                    errorText={errors?.zip}
                                    onBlur={blur}
                                    onFocus={focus}
                                    onChange={changeField}
                                />
                            </Col>
                            <Col md={4} lg={4}>
                                <SelectField
                                    name="stateId"
                                    value={data.stateId}
                                    options={mappedStates}
                                    label="State"
                                    placeholder="State"
                                    className="ClientRecordFilter-SelectField"
                                    isMultiple={false}
                                    onChange={changeField}
                                />
                            </Col>
                        </Row>
                    </Col>
                </Row>
            )}
            <Row>
                <Col md={12}>
                    <Row>
                        {!canRequestAccess && (
                            <Col md={3}>
                                <TextField
                                    type="text"
                                    name="phone"
                                    value={data.phone}
                                    label="Phone"
                                    maxLength={16}
                                    className="ClientRecordFilter-TextField"
                                    onBlur={blur}
                                    onFocus={focus}
                                    onChange={changeField}
                                />
                            </Col>
                        )}
                        <Col md={9}>
                            <Button
                                outline
                                color='success'
                                className="margin-right-25"
                                onClick={() => reset()}>
                                Clear
                            </Button>
                            <Button
                                color='success'
                                onClick={applyIfValid}>
                                {canRequestAccess ? "Search" : "Apply"}
                            </Button>
                        </Col>
                    </Row>
                </Col>
            </Row>
        </div>
    )
}