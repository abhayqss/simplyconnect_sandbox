import React, {
    memo,
    useMemo,
    useCallback
} from 'react'

import cn from 'classnames'

import moment from 'moment'

import {
    Row,
    Col,
    Button
} from 'reactstrap'

import {
    DateField,
    TextField,
    SelectField
} from 'components/Form'

import {
    useProspectFilterDirectory,
    useProspectFilterInitialization,
    useProspectFilterDefaultDataCache
} from 'hooks/business/Prospects'

import {
    DateUtils as DU
} from 'lib/utils/Utils'

import {
    getDateTime
} from 'lib/utils/DateUtils'

import {
    map
} from 'lib/utils/ArrayUtils'

import "./ProspectFilter.scss"

const { format, formats } = DU
const DATE_FORMAT = formats.americanMediumDate

function valueTextMapper({ id, name, label, title, value }) {
    return { value: id || value || name, text: label || title || name }
}

function ProspectFilter(
    {
        data,
        reset,
        apply,
        isSaved,

        blur,
        focus,

        organizationId,

        changeField,
        changeFields,
        className
    }
) {
    const onChangeField = useCallback((name, value) => {
        changeField(name, value, false)
    }, [changeField])

    const onChangeDateField = useCallback((name, value) => {
        changeField(name, value ? format(value, DATE_FORMAT) : null, false)
    }, [changeField])

    const {
        update: updateDefaultData
    } = useProspectFilterDefaultDataCache({ organizationId })

    const {
        genders = [],
        statuses = []
    } = useProspectFilterDirectory()

    useProspectFilterInitialization({
        isSaved, organizationId, changeFields, updateDefaultData
    })

    const mappedStatuses = useMemo(
        () => map(statuses, valueTextMapper), [statuses]
    )

    const mappedGenders = useMemo(
        () => map(genders, valueTextMapper), [genders]
    )

    return (
        <div
            data-testid="prospectFilter"
            className={cn('ProspectFilter', className)}
        >
            <Row>
                <Col md={3}>
                    <TextField
                        type="text"
                        name="firstName"
                        value={data.firstName}
                        label="First Name"
                        className="ProspectFilter-TextField"
                        onBlur={blur}
                        onFocus={focus}
                        onChange={onChangeField}
                        maxLength={256}
                    />
                </Col>
                <Col md={3}>
                    <TextField
                        type="text"
                        name="lastName"
                        value={data.lastName}
                        label="Last Name"
                        className="ClientFilter-TextField"
                        onBlur={blur}
                        onFocus={focus}
                        onChange={onChangeField}
                        maxLength={256}
                    />
                </Col>
                <Col md={2}>
                    <SelectField
                        name="genderId"
                        value={data.genderId}
                        options={mappedGenders}
                        label="Gender"
                        className="ProspectFilter-SelectField"
                        onChange={onChangeField}
                    />
                </Col>
                <Col md={2}>
                    <DateField
                        name="birthDate"
                        value={data.birthDate ? getDateTime(data.birthDate) : null}
                        dateFormat="MM/dd/yyyy"
                        label="Date of Birth"
                        placeholder="Select date"
                        className="ProspectFilter-DateField"
                        onChange={onChangeDateField}
                        isFutureDisabled
                    />
                </Col>
                <Col md={2}>
                    <SelectField
                        name="prospectStatus"
                        value={data.prospectStatus}
                        options={mappedStatuses}
                        label="Prospect status"
                        placeholder="Select"
                        className="ProspectFilter-SelectField"
                        onChange={onChangeField}
                    />
                </Col>
            </Row>
            <Row>
                <Col md={12}>
                    <Button
                        outline
                        color='success'
                        onClick={() => reset()}>
                        Clear
                    </Button>
                    <Button
                        color='success'
                        onClick={() => apply()}>
                        Apply
                    </Button>
                </Col>
            </Row>
        </div>
    )
}

export default memo(ProspectFilter)