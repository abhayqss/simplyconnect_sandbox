import React, {
    memo,
    useEffect,
    useCallback
} from 'react'

import {
    Col,
    Row
} from 'reactstrap'

import {
    first,
    reject,
    findWhere
} from 'underscore'

import { SelectField } from 'components/Form'

import { useSelectOptions } from 'hooks/common'

import {
    useClientsQuery,
    useCommunitiesQuery,
    useOrganizationsQuery
} from 'hooks/business/conversations'

import { isEmpty, isInteger } from 'lib/utils/Utils'

function ClientSection(
    {
        fields,
        errors = {},

        excludedContactIds = [],
        includeNonAssociated = false,

        onClearField,
        onChangeField: onChangeFieldCb,
    }
) {
    const {
        data: organizations,
        isFetching: isFetchingOrganizations
    } = useOrganizationsQuery({
        withAccessibleClients: true,
        withAccessibleNonAssociatedClients: includeNonAssociated
    })

    const organizationOptions = useSelectOptions(organizations)

    const {
        data: communities,
        isFetching: isFetchingCommunities
    } = useCommunitiesQuery(
        {
            organizationIds: [fields.organizationId],
            withAccessibleClients: true,
            withAccessibleNonAssociatedClients: includeNonAssociated
        },
        { enabled: isInteger(fields.organizationId) }
    )

    const communityOptions = useSelectOptions(communities)

    const {
        data: clients,
        isFetching: isFetchingClients
    } = useClientsQuery(
        {
            communityIds: [fields.communityId],
            includeNonAssociatedClients: includeNonAssociated
        },
        { enabled: isInteger(fields.communityId) }
    )

    const filteredClients = reject(clients, o => (
        excludedContactIds.includes(o.associatedContactId)
    ))

    const clientOptions = useSelectOptions(filteredClients, { textProp: 'fullName' })

    const setDefaultOption = useCallback((options, field) => {
        if (options.length === 1) {
            onChangeFieldCb(field, [first(options).value])
        }
    }, [onChangeFieldCb])

    function setDefaultOrganization() {
        setDefaultOption(organizationOptions, 'client.organizationId')
    }

    function setDefaultCommunity() {
        setDefaultOption(communityOptions, 'client.communityId')
    }

    const onChangeField = useCallback((name, value) => {
        onChangeFieldCb(name, value)

        onChangeFieldCb('client.id', null)
        onClearField('client.careTeamMemberIds')
        onChangeFieldCb('client.associatedContactId', null)
    }, [onClearField, onChangeFieldCb])

    const onChangeClientField = useCallback((name, value) => {
        onChangeFieldCb(name, value)

        const {
            associatedContactId
        } = findWhere(filteredClients, { id: value }) || {}

        onClearField('client.careTeamMemberIds')
        onChangeFieldCb('client.associatedContactId', associatedContactId)
    }, [filteredClients, onClearField, onChangeFieldCb])

    useEffect(setDefaultOrganization, [organizationOptions, setDefaultOption])
    useEffect(setDefaultCommunity, [communityOptions, setDefaultOption])

    return (
        <div className="AddToGroupConversationForm-Section">
            <div className="AddToGroupConversationForm-SectionTitle">
                Client
            </div>

            <Row>
                <Col md={4}>
                    <SelectField
                        name="client.organizationId"
                        value={fields.organizationId}
                        options={organizationOptions}
                        isDisabled={isFetchingOrganizations}
                        label="Organization"
                        className="AddToGroupConversationForm-SelectField"
                        onChange={onChangeField}
                        errorText={errors.organizationId}
                    />
                </Col>

                <Col md={4}>
                    <SelectField
                        name="client.communityId"
                        value={fields.communityId}
                        options={communityOptions}
                        hasKeyboardSearch
                        hasKeyboardSearchText
                        label="Community"
                        className="AddToGroupConversationForm-SelectField"
                        isDisabled={
                            !isInteger(fields.organizationId)
                            || isFetchingCommunities
                        }
                        onChange={onChangeField}
                        errorText={errors.communityId}
                    />
                </Col>

                <Col md="4">
                    <SelectField
                        name="client.id"
                        value={fields.id}
                        options={clientOptions}
                        hasKeyboardSearch
                        hasKeyboardSearchText
                        label="Client"
                        className="AddToGroupConversationForm-SelectField"
                        isDisabled={(
                            isFetchingClients
                            || isEmpty(filteredClients)
                            || !isInteger(fields.communityId)
                        )}
                        onChange={onChangeClientField}
                        errorText={errors.id}
                    />
                </Col>
            </Row>
        </div>
    )
}

export default memo(ClientSection)