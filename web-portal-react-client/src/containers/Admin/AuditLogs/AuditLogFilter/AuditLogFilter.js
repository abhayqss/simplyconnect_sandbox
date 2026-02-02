import React, {
    useMemo,
    useState,
    useEffect,
    useCallback
} from 'react'

import {
    map,
    noop,
} from 'underscore'

import cn from 'classnames'

import {
    Row,
    Col,
    Button
} from 'reactstrap'

import { useLocalCache } from 'hooks/common'
import { useAuthUser } from 'hooks/common/redux'
import { useCustomFilter } from 'hooks/common/filter'

import {
    useAuditLogFilterDirectory
} from 'hooks/business/admin/audit'

import {
    DateField,
    SelectField
} from 'components/Form'

import Entity from 'entities/AuditLogFilter'

import { AuditLogFilterValidator as Validator } from 'validators'

import { isInteger } from 'lib/utils/Utils'

import './AuditLogFilter.scss'

export const NAME = 'AUDIT_LOG_FILTER'

const INITIAL_DATA = Entity().toJS()

function valueTextMapper({ id, name, title, label, fullName }) {
    return { value: id || name, text: title || label || fullName || name || '' }
}

export default function AuditLogFilter(
    {
        canReApply = false,
        canReReset = false,
        className,
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
        reset,
        apply,
        errors,
        remove,
        isSaved,
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
            Validator
        }
    )

    const {
        organizationId,
        communityIds,
        employeeIds,
        activityIds,
        clientIds,
        fromDate,
        toDate
    } = data

    const {
        get: getDefaultData,
        update: updateDefaultData
    } = useLocalCache(organizationId)

    const {
        organizations,
        communities,
        contacts,
        clients,
        activityTypes,
        isFetchingOldestAuditLogDate
    } = useAuditLogFilterDirectory(
        { organizationId, communityIds },
        {
            actions: {
                isFilterSaved: isSaved,
                changeFilterField: changeField,
                updateFilterDefaultData: updateDefaultData
            }
        }
    )

    const mappedOrganizations = useMemo(() => map(organizations, valueTextMapper), [organizations])
    const mappedCommunities = useMemo(() => map(communities, valueTextMapper), [communities])
    const mappedContacts = useMemo(() => map(contacts, valueTextMapper), [contacts])
    const mappedActivityTypes = useMemo(() => map(activityTypes, (group, i) => ({
            id: i,
            name: group.name,
            title: group.title,
            options: map(
                group.activities,
                o => ({ text: o.title, value: o.id })
            )
        })), [activityTypes])
    const mappedClients = useMemo(() => map(clients, valueTextMapper), [clients])

    function validateIf() {
        if (isValidationNeed) {
            validate()
                .then(() => setValidationNeed(false))
                .catch(() => setValidationNeed(true))
        }
    }

    const onChangeOrganizationField = useCallback((name, value) => {
        remove()
        changeFields({
            ...INITIAL_DATA,
            [name]: value,
            ...getDefaultData(value)
        })
    }, [
        remove,
        changeFields,
        getDefaultData
    ])

    const onChangeCommunityField = useCallback((name, value) => {
        remove()
        changeFields({
            ...INITIAL_DATA,
            organizationId,
            activityIds,
            fromDate,
            toDate,
            [name]: value
        })
    }, [
        organizationId,
        activityIds,
        fromDate,
        toDate,
        remove,
        changeFields
    ])

    const onChangeField = useCallback((name, value) => {
        changeField(name, value)
    }, [changeField])

    const onChangeDateField = useCallback((name, value) => {
        changeField(name, value ? value.getTime() : null, false)
    }, [changeField])

    const onClear = useCallback(() => {
        const { organizationId } = user

        reset({
            ...getDefaultData(organizationId),
            organizationId,
            toDate
        })
    }, [
        user,
        reset,
        toDate,
        getDefaultData
    ])

    const applyIfValid = useCallback(() => {
        validate()
            .then(() => apply())
            .catch(() => setValidationNeed(true))
    }, [apply, validate])

    useEffect(() => {
        if (user && !isSaved()) {
            changeField(
                'organizationId',
                user.organizationId,
                true
            )
        }
    }, [user, isSaved, changeField])

    useEffect(() => {
        if (user && isInteger(organizationId)) {
            changeField(
                'toDate',
                Date.now(),
                organizationId === user.organizationId
            )
        }
    }, [user, changeField, organizationId])

    useEffect(validateIf, [isValidationNeed, validate, data])

    return (
        <div className={cn('AuditLogFilter', className)}>
            <Row>
                <Col lg={4} md={6}>
                    <SelectField
                        label="Organization*"
                        name="organizationId"

                        hasKeyboardSearch
                        hasKeyboardSearchText
                        value={organizationId}
                        options={mappedOrganizations}

                        placeholder="Select Organization"
                        className="AuditLogFilter-SelectField"
                        errorText={errors?.organizationId}

                        onChange={onChangeOrganizationField}
                    />
                </Col>
                <Col lg={4} md={6}>
                    <SelectField
                        label="Community*"
                        name="communityIds"

                        isMultiple
                        hasKeyboardSearch
                        hasKeyboardSearchText
                        value={communityIds}
                        options={mappedCommunities}

                        placeholder="Select Community"
                        className="AuditLogFilter-SelectField"
                        errorText={errors?.communityIds}
                        isDisabled={!organizationId || mappedCommunities.length === 1}

                        onChange={onChangeCommunityField}
                    />
                </Col>
                <Col lg={4}>
                    <SelectField
                        label="User*"
                        name="employeeIds"

                        isMultiple
                        hasKeyboardSearch
                        hasKeyboardSearchText
                        value={employeeIds}
                        options={mappedContacts}

                        placeholder="Select User"
                        className="AuditLogFilter-SelectField"
                        errorText={errors?.employeeIds}
                        isDisabled={!organizationId || mappedContacts.length === 1}

                        onChange={onChangeField}
                    />
                </Col>
            </Row>
            <Row>
                <Col lg={4} md={6}>
                    <SelectField
                        label="Activity*"
                        name="activityIds"

                        isMultiple
                        isSectioned
                        hasSectionIndicator
                        hasSectionSeparator
                        hasKeyboardSearch
                        hasKeyboardSearchText

                        sections={mappedActivityTypes}
                        value={activityIds}

                        placeholder="Select Activity"
                        className="AuditLogFilter-SelectField"
                        errorText={errors?.activityIds}

                        onChange={onChangeField}
                    />
                </Col>
                <Col lg={4} md={6}>
                    <SelectField
                        label="Client*"
                        name="clientIds"

                        isMultiple
                        hasKeyboardSearch
                        hasKeyboardSearchText
                        value={clientIds}
                        options={mappedClients}

                        placeholder="Select Client"
                        className="AuditLogFilter-SelectField"
                        errorText={errors?.clientIds}

                        onChange={onChangeField}
                    />
                </Col>
                <Col lg={2} md={6}>
                    <DateField
                        name="fromDate"
                        value={fromDate}
                        dateFormat="MM/dd/yyyy"
                        label="Date From*"
                        placeholder="Select date"
                        className="AuditLogFilter-DateField"
                        errorText={errors?.fromDate}
                        onChange={onChangeDateField}
                    />
                </Col>
                <Col lg={2} md={6}>
                    <DateField
                        name="toDate"
                        value={toDate}
                        dateFormat="MM/dd/yyyy"
                        label="Date To*"
                        placeholder="Select date"
                        minDate={fromDate}
                        className="AuditLogFilter-DateField"
                        errorText={errors?.toDate}
                        onChange={onChangeDateField}
                    />
                </Col>
            </Row>
            <Row>
                <Col md={12} lg={4}>
                    <Button
                        outline
                        color='success'
                        className="margin-right-25"
                        onClick={onClear}
                        disabled={isFetchingOldestAuditLogDate}
                    >
                        Clear
                    </Button>
                    <Button
                        color='success'
                        onClick={applyIfValid}
                        disabled={isFetchingOldestAuditLogDate}
                    >
                        Apply
                    </Button>
                </Col>
            </Row>
        </div>
    )
}