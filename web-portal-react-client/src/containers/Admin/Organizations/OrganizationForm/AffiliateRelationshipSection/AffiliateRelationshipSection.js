import React, {
    memo,
    useMemo,
    useEffect
} from 'react'

import { sortBy, first, isNumber } from 'underscore'

import { FormLabel } from 'react-bootstrap'
import { Row, Col, UncontrolledTooltip as Tooltip } from 'reactstrap'

import {
    useSelectOptions
} from 'hooks/common'

import {
    useAuthUser
} from 'hooks/common/redux'

import {
    useCommunitiesQuery,
    useOrganizationsQuery,
} from 'hooks/business/directory/query'

import { SelectField, CheckboxField } from 'components/Form'

import { SYSTEM_ROLES } from 'lib/Constants'

import { ReactComponent as CrossIcon } from 'images/cross.svg'

const isItemNotFound = (item, target) => {
    return !target.find(option => option.id === item.id)
}

function mergeOrganizations(list, organizations = []) {
    const optionNotFound = item => isItemNotFound(item, list)

    if (list) {
        organizations.forEach(o => {
            if (optionNotFound(o)) {
                list.push({
                    id: o.id,
                    label: o.title,
                    hasCommunities: true
                })
            }
        })
    }

    return sortBy(list, 'label')
}

function mergeCommunities(list, communities = []) {
    const optionNotFound = item => isItemNotFound(item, list)

    if (list) {
        communities.forEach(o => {
            if (optionNotFound(o)) {
                list.push({
                    id: o.id,
                    name: o.title
                })
            }
        })
    }

    return sortBy(list, 'name')
}

function AffiliateRelationshipSection({
    name,
    title,
    value,
    onChange,
    onRemove,
    errors = {},
    organizationId,
}) {
    const id = name.replace('.', '_')

    const user = useAuthUser()

    let isSuperAdmin = user?.roleName === SYSTEM_ROLES.SUPER_ADMINISTRATOR

    const organization = useMemo(() => value.affiliatedOrganization.toJS(), [value.affiliatedOrganization])
    const primaryCommunities = useMemo(() => value.primaryCommunities.toJS(), [value.primaryCommunities])
    const affiliatedCommunities = useMemo(() => value.affiliatedCommunities.toJS(), [value.affiliatedCommunities])

    const {
        data: communities,
        isFetching: isFetchingCommunities
    } = useCommunitiesQuery(
        { organizationId },
        { enabled: isNumber(organizationId) }
    )

    const {
        data: selectedOrgCommunities,
        isFetching: isFetchingAffiliatedCommunities
    } = useCommunitiesQuery(
        { organizationId: organization?.id },
        { enabled: isNumber(organization?.id) }
    )

    const {
        data: organizations,
        isFetching: isFetchingOrganizations
    } = useOrganizationsQuery({
        checkCommunitiesExist: true
    })

    const directory = {
        communities: useMemo(() => (
            mergeCommunities(communities, primaryCommunities)
        ), [communities, primaryCommunities]),
        affiliatedCommunities: useMemo(() => (
            mergeCommunities(selectedOrgCommunities, affiliatedCommunities)
        ), [selectedOrgCommunities, affiliatedCommunities]),
    }

    const communityOptions = useSelectOptions(directory.communities, { textProp: 'name' })
    const communityInSelectedOrgOptions = useSelectOptions(directory.affiliatedCommunities, { textProp: 'name' })
    const organizationOptions = useMemo(() => {
        let mergeSource = isNumber(organization.id) ? [organization] : []

        return mergeOrganizations(organizations, mergeSource)
            .filter(o => o.id !== organizationId)
            .map(o => ({
                value: o.id,
                text: o.label,
                tooltip: !o.hasCommunities ? `The organization doesn't have a community. Please create community.` : '',
                isDisabled: !o.hasCommunities,
            }))
    }, [organizationId, organizations, organization])

    const onChangeOrganization = (...args) => {
        onChange(...args)
        onChange(`${name}.affiliatedCommunities`, value.affiliatedCommunities.clear())
    }

    const onChangeCommunity = (field, ids) => {
        let value = ids?.map(id => ({ id }))

        onChange(field, value)
    }

    function selectCommunityIf() {
        if (!value.areAllPrimaryCommunitiesSelected
            && communityOptions?.length === 1
            && !primaryCommunities.length
        ) {
            let val = [{ id: first(communityOptions).value }]

            onChange(`${name}.primaryCommunities`, val)
        }
    }

    function selectAffiliatedCommunityIf() {
        if (!value.areAllAffiliatedCommunitiesSelected
            && communityInSelectedOrgOptions?.length === 1
            && !affiliatedCommunities.length
        ) {
            let val = [{ id: first(communityInSelectedOrgOptions).value }]

            onChange(`${name}.affiliatedCommunities`, val)
        }
    }

    useEffect(selectCommunityIf, [
        name, onChange, communityOptions,
        value.areAllPrimaryCommunitiesSelected,
    ])


    useEffect(selectAffiliatedCommunityIf, [
        name, onChange, communityInSelectedOrgOptions,
        value.areAllAffiliatedCommunitiesSelected,
    ])

    return (
        <div className="OrganizationRelationshipSection">
            <Row>
                <Col className="OrganizationRelationshipSection-Title">
                    <FormLabel>
                        {title}
                    </FormLabel>

                    <CrossIcon
                        id={id}
                        onClick={onRemove}
                        className="OrganizationForm-CrossIcon"
                    />

                    <Tooltip
                        placement="right"
                        target={id}
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
                        Remove Relationship
                    </Tooltip>
                </Col>
            </Row>

            <Row>
                <Col lg={6}>
                    <SelectField
                        isMultiple
                        type="text"
                        hasValueTooltip
                        name={`${name}.primaryCommunities`}
                        value={primaryCommunities.map(o => o.id)}
                        label="I want to share information about the events coming to*"
                        placeholder="Select community"
                        className="OrganizationForm-SelectField"
                        isDisabled={value.areAllPrimaryCommunitiesSelected || isFetchingCommunities}
                        options={communityOptions}
                        errorText={errors.primaryCommunities}
                        onChange={onChangeCommunity}
                    />
                </Col>

                <Col lg={6} className="OrganizationRelationshipSection-Checkbox">
                    <CheckboxField
                        value={value.areAllPrimaryCommunitiesSelected}
                        name={`${name}.areAllPrimaryCommunitiesSelected`}
                        label="All communities, including those that will be created further"
                        className="OrganizationRelationshipSection-CheckboxField"
                        onChange={onChange}
                    />
                </Col>
            </Row>

            <Row>
                <Col>
                    <SelectField
                        type="text"
                        hasKeyboardSearch
                        hasKeyboardSearchText
                        name={`${name}.affiliatedOrganization.id`}
                        value={organization?.id}
                        label="Share with Organization*"
                        placeholder="Select organization"
                        className="OrganizationForm-SelectField"
                        isDisabled={isFetchingOrganizations || !isSuperAdmin}
                        options={organizationOptions}
                        errorText={errors.affiliatedOrganization?.id}
                        onChange={onChangeOrganization}
                    />
                </Col>
            </Row>

            <Row>
                <Col lg={6}>
                    <SelectField
                        isMultiple
                        type="text"
                        name={`${name}.affiliatedCommunities`}
                        value={affiliatedCommunities.map(o => o.id)}
                        label="Share with Community*"
                        placeholder="Select community"
                        className="OrganizationForm-SelectField"
                        isDisabled={
                            isFetchingOrganizations
                            || !isNumber(organization?.id)
                            || value.areAllAffiliatedCommunitiesSelected
                        }
                        options={communityInSelectedOrgOptions}
                        errorText={errors.affiliatedCommunities}
                        onChange={onChangeCommunity}
                    />
                </Col>

                <Col lg={6} className="OrganizationRelationshipSection-Checkbox">
                    <CheckboxField
                        value={value.areAllAffiliatedCommunitiesSelected}
                        name={`${name}.areAllAffiliatedCommunitiesSelected`}
                        isDisabled={
                            isFetchingOrganizations
                            || isFetchingAffiliatedCommunities
                            || !isNumber(organization?.id)
                        }
                        label="All communities, including those that will be created further"
                        className="OrganizationRelationshipSection-CheckboxField"
                        onChange={onChange}
                    />
                </Col>
            </Row>
        </div>
    )
}

export default memo(AffiliateRelationshipSection)
