import React, {
    memo,
    useMemo,
    useEffect,
    useCallback
} from 'react'

import { Col } from 'reactstrap'

import { first } from 'underscore'

import { SelectField } from 'components/Form'

import {
    useSelectOptions,
    useCustomFormFieldChange
} from 'hooks/common'

import {
    useCommunitiesQuery,
    useOrganizationsQuery
} from 'hooks/business/conversations'

function BaseSection({
    name,
    fields,
    params = {},
    errors = {},
    onChangeField,

    colGrid = ['4', '8'],
}) {
    const organizationIds = useMemo(
        () => fields.organizationIds.toJS(),
        [fields.organizationIds]
    )

    const communityIds = useMemo(
        () => fields.communityIds.toJS(),
        [fields.communityIds]
    )

    const {
        data: organizations,
        isFetching: isFetchingOrganizations
    } = useOrganizationsQuery(params)

    const {
        changeSelectField
    } = useCustomFormFieldChange(onChangeField)

    const organizationOptions = useSelectOptions(organizations)

    const {
        data: communities,
        isFetching: isFetchingCommunities
    } = useCommunitiesQuery(
        { organizationIds: organizationIds, ...params },
        { enabled: !fields.organizationIds.isEmpty() }
    )

    const communityOptions = useSelectOptions(communities)

    const setDefaultOption = useCallback((options, field) => {
        if (options.length === 1) {
            onChangeField(field, [first(options).value])
        }
    }, [onChangeField])

    function setDefaultOrganization() {
        setDefaultOption(organizationOptions, `${name}.organizationIds`)
    }

    function setDefaultCommunity() {
        setDefaultOption(communityOptions, `${name}.communityIds`)
    }

    useEffect(setDefaultOrganization, [organizationOptions, setDefaultOption])
    useEffect(setDefaultCommunity, [communityOptions, setDefaultOption])

    return (
        <>
            <Col md={colGrid[0]}>
                <SelectField
                    isMultiple
                    name={`${name}.organizationIds`}
                    value={organizationIds}
                    options={organizationOptions}
                    isDisabled={isFetchingOrganizations}
                    label="Organization"
                    className="LabOrderForm-SelectField"
                    onChange={changeSelectField}
                    errorText={errors.organizationIds}
                />
            </Col>

            <Col md={colGrid[1]}>
                <SelectField
                    isMultiple
                    name={`${name}.communityIds`}
                    value={communityIds}
                    options={communityOptions}
                    hasKeyboardSearch
                    hasKeyboardSearchText
                    label="Community"
                    className="LabOrderForm-SelectField"
                    isDisabled={
                        fields.organizationIds.isEmpty()
                        || isFetchingCommunities
                    }
                    onChange={changeSelectField}
                    errorText={errors.communityIds}
                />
            </Col>
        </>
    )
}

export default memo(BaseSection)