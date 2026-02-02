import React, {
    memo,
    useMemo,
    useCallback
} from 'react'

import cn from 'classnames'

import {
    compact,
    without,
    contains
} from 'underscore'

import {
    Row,
    Col,
    Button
} from 'reactstrap'

import { useQueryClient } from '@tanstack/react-query'

import {
    SelectField,
    CheckboxField
} from 'components/Form'

import {
    useAuthUser
} from 'hooks/common/redux'

import {
    useAppointmentFilterDirectory,
    useAppointmentParticipationQuery,
    useAppointmentFilterInitialization,
    useAppointmentFilterDefaultDataCache
} from 'hooks/business/appointments'

import {
    SYSTEM_ROLES,
    CLIENT_STATUSES,
    CONTACT_STATUSES
} from 'lib/Constants'

import {
    isEmpty,
    isInteger
} from 'lib/utils/Utils'

import {
    concatIf
} from 'lib/utils/StringUtils'

import {
    map, unshiftIf
} from 'lib/utils/ArrayUtils'

import "./AppointmentFilter.scss"

const { PENDING, DECLINED } = CLIENT_STATUSES

const {
    PARENT_GUARDIAN,
    PERSON_RECEIVING_SERVICES
} = SYSTEM_ROLES

const { INACTIVE } = CONTACT_STATUSES

const NON_PROFESSIONALS = [PARENT_GUARDIAN]

function valueTextMapper({ id, name, label, title, value }) {
    return { value: id || value || name, text: label || title || name }
}

function AppointmentFilter(
    {
        data,
        save,
        blur,
        focus,
        reset,
        apply,
        errors,
        isSaved,
        canAutoSave,

        communityIds,
        organizationId,

        changeField,
        changeFields,
        className
    }
) {
    const user = useAuthUser()

    const queryClient = useQueryClient()

    const communities = queryClient.getQueryData(
        ['Directory.Communities', { organizationId }]
    )

    const onChangeField = useCallback((name, value) => {
        changeField(name, value, false)
    }, [changeField])

    const {
        update: updateDefaultData
    } = useAppointmentFilterDefaultDataCache({ organizationId })

    const {
        types = [],
        clients = [],
        statuses = [],
        creators = [],
        clientStatuses = [],
        serviceProviders = [],
        isFetchingClients
    } = useAppointmentFilterDirectory(
        { organizationId, communityIds, ...data }
    )

    useAppointmentFilterInitialization({
        isSaved, organizationId, changeFields, updateDefaultData
    })

    const mappedClients = useMemo(() =>
        map(clients, o => ({ text: o.fullName, value: o.id })), [clients]
    )

    const filteredClientStatuses = useMemo(
        () => clientStatuses?.filter(
            s => ![PENDING, DECLINED].includes(s.name)
        ), 
        [clientStatuses]
    )

    const mappedClientStatuses = useMemo(
        () => map(filteredClientStatuses, valueTextMapper), [filteredClientStatuses]
    )

    const {
        data: {
            hasNoProviders,
            hasExternalProvider
        } = {}
    } = useAppointmentParticipationQuery({
        organizationId
    }, {
        staleTime: 0,
        enabled: isInteger(organizationId)
    })

    const mappedServiceProviders = useMemo(() =>
        map(
            compact([
                hasExternalProvider && {
                    id: 'EXTERNAL_PROVIDER',
                    name: 'External Provider'
                },
                hasNoProviders && {
                    id: 'NO_PROVIDERS',
                    name: 'No Service Provider'
                },
                ...serviceProviders,
            ]),
            o => ({
                value: o.id,
                text: o.name,
                ...o.status === INACTIVE && {
                    tooltip: 'User is no longer active',
                    className: 'AppointmentFilter-InactiveCreator'
                }
            })
        ), [serviceProviders, hasNoProviders, hasExternalProvider])

    const mappedCreators = useMemo(() =>
        map(creators, o => ({
            value: o.id,
            text: concatIf(o.name, ` - ${o.communityTitle}`, !!o.communityTitle),
            ...o.status === INACTIVE && {
                tooltip: 'User is no longer active',
                className: 'AppointmentFilter-InactiveCreator'
            }
        })), [creators])

    const mappedStatuses = useMemo(() =>
        map(statuses, valueTextMapper), [statuses]
    )

    const mappedTypes = useMemo(() =>
        map(types, valueTextMapper), [types]
    )

    const onChangeServiceProviderField = useCallback((name, value) => {
        const mockValues = ['NO_PROVIDERS', 'EXTERNAL_PROVIDER']
        changeField('serviceProviderIds', without(value, ...mockValues))
        changeField('hasNoServiceProviders', contains(value, 'NO_PROVIDERS'))
        changeField('isExternalProviderServiceProvider', contains(value, 'EXTERNAL_PROVIDER'))
    }, [changeField])

    const onChangeCreatorSearchText = useCallback((name, value) => {
        onChangeField('creatorSearchText', value)
    }, [onChangeField])

    const onClearCreatorSearchText = useCallback(() => {
        onChangeField('creatorSearchText', null)
    }, [onChangeField])

    const onChangeClientSearchText = useCallback((name, value) => {
        onChangeField('clientSearchText', value)
    }, [onChangeField])

    const onClearClientSearchText = useCallback(() => {
        onChangeField('clientSearchText', null)
    }, [onChangeField])

    const onChangeServiceProviderSearchText = useCallback((name, value) => {
        onChangeField('serviceProviderSearchText', value)
    }, [onChangeField])

    const onClearServiceProviderSearchText = useCallback(() => {
        onChangeField('serviceProviderSearchText', null)
    }, [onChangeField])

    return (
        <div
            data-testid="appointmentFilter"
            className={cn('AppointmentFilter', className)}
        >
            <Row>
                <Col lg={3} md={6}>
                    <SelectField
                        label="Creator"

                        name="creatorIds"
                        value={data.creatorIds}
                        options={mappedCreators}

                        hasValueTooltip
                        hasAllOption
                        hasSearchBox
                        hasKeyboardSearch
                        hasKeyboardSearchText
                        isMultiple

                        isDisabled={(communityIds === null || (
                            [...NON_PROFESSIONALS, PERSON_RECEIVING_SERVICES].includes(user?.roleName)
                        ))}

                        className="AppointmentFilter-SelectField"

                        onChange={onChangeField}
                        onClearSearchText={onClearCreatorSearchText}
                        onChangeSearchText={onChangeCreatorSearchText}
                    />
                </Col>
                <Col lg={3} md={6}>
                    <SelectField
                        name="serviceProviderIds"
                        value={compact([
                            data.isExternalProviderServiceProvider && 'EXTERNAL_PROVIDER',
                            data.hasNoServiceProviders && 'NO_PROVIDERS',
                            ...data.serviceProviderIds
                        ])}
                        options={mappedServiceProviders}
                        hasValueTooltip
                        hasAllOption
                        hasKeyboardSearch
                        hasKeyboardSearchText
                        hasSearchBox
                        isMultiple
                        label="Service Provider"
                        className="AppointFilter-SelectField"
                        isDisabled={isEmpty(communities)}
                        onChange={onChangeServiceProviderField}
                        onClearSearchText={onClearServiceProviderSearchText}
                        onChangeSearchText={onChangeServiceProviderSearchText}
                    />
                </Col>
                <Col lg={3} md={6}>
                    <SelectField
                        name="clientIds"
                        value={data.clientIds}
                        options={mappedClients}
                        label="Client"
                        isDisabled={
                            communityIds === null
                            || mappedClients.length === 0
                        }
                        renderDropdownHeader={() => (
                            <div className="AppointmentFilter-ClientsDropdownHeader">
                                <CheckboxField
                                    name="clientsWithAccessibleAppointments"
                                    label="Only show clients with appointments"
                                    value={data.clientsWithAccessibleAppointments}
                                    className="AppointmentFilter-CheckboxField pt-0"
                                    onChange={changeField}
                                />
                            </div>
                        )}
                        isMultiple
                        isFetchingOptions={isFetchingClients}
                        hasKeyboardSearch
                        hasKeyboardSearchText
                        hasSearchBox
                        hasValueTooltip
                        hasAllOption
                        hasDropdownHeader
                        placeholder={mappedClients.length === 0 ? 'No clients' : 'Select'}

                        className="AppointmentFilter-SelectField"
                        onChange={onChangeField}
                        onClearSearchText={onClearClientSearchText}
                        onChangeSearchText={onChangeClientSearchText}
                    />
                </Col>
                <Col lg={3} md={6}>
                    <SelectField
                        hasAllOption
                        isMultiple={true}
                        name="clientStatuses"
                        options={mappedClientStatuses}
                        value={data.clientStatuses}
                        label="Client Status"
                        placeholder="Client Status"
                        className="AppointmentFilter-SelectField"
                        onChange={onChangeField}
                    />
                </Col>
            </Row>
            <Row>
                <Col lg={3} md={6}>
                    <SelectField
                        label="Type"

                        name="types"
                        value={data.types}
                        options={mappedTypes}

                        hasValueTooltip
                        hasAllOption
                        isMultiple

                        placeholder="Select Type"
                        className="AppointmentFilter-SelectField"

                        onChange={onChangeField}
                    />
                </Col>
                <Col lg={3} md={6}>
                    <SelectField
                        label="Status"

                        name="statuses"
                        value={data.statuses}
                        options={mappedStatuses}

                        isMultiple
                        hasValueTooltip
                        hasAllOption

                        placeholder="Select Status"
                        className="AppointmentFilter-SelectField"

                        onChange={onChangeField}
                    />
                </Col>

                <Col lg={3} md={12}>
                    <div className="AppointmentFilter-Buttons">
                        <Button
                            outline
                            color='success'
                            className="AppointmentFilter-Btn"
                            onClick={() => reset()}
                            disabled={isFetchingClients}
                        >
                            Clear
                        </Button>
                        <Button
                            color='success'
                            onClick={apply}
                            className="AppointmentFilter-Btn"
                            disabled={isFetchingClients}
                        >
                            Apply
                        </Button>
                    </div>
                </Col>
            </Row>
        </div>
    )
}

export default memo(AppointmentFilter)