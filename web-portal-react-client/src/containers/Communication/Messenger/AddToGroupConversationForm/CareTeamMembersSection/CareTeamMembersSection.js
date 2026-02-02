import React, { memo, useMemo, useEffect, useCallback } from 'react'

import { Col, Row } from 'reactstrap'

import { first, isEmpty } from 'underscore'

import { SelectField } from 'components/Form'

import {
    useSelectOptions,
    useCustomFormFieldChange
} from 'hooks/common'

import {
    useCommunityCareTeamMembersQuery
} from 'hooks/business/conversations'

import BaseSection from '../BaseSection/BaseSection'

function CareTeamMembersSection({
    name,
    fields,
    excludedIds,
    errors = {},
    onChangeField,
}) {
    const ids = useMemo(() => fields.ids.toJS(), [fields.ids])
    const communityIds = useMemo(() => fields.communityIds.toJS(), [fields.communityIds])

    const {
        data: careTeamMembers,
        isFetching: isFetchingCareTeamMembers
    } = useCommunityCareTeamMembersQuery(
        { communityIds },
        { enabled: !fields.communityIds.isEmpty() }
    )

    const {
        changeSelectField
    } = useCustomFormFieldChange(onChangeField)

    const filteredCareTeamMembers = useMemo(() => {
        return careTeamMembers?.filter(o => !excludedIds.includes(o.id))
    }, [careTeamMembers, excludedIds])

    const careTeamOptions = useSelectOptions(filteredCareTeamMembers, { textProp: 'fullName' })

    const onChangeBaseField = useCallback((...args) => {
        onChangeField(...args)
        onChangeField(`${name}.ids`, [])
    }, [name, onChangeField])

    useEffect(() => {
        if (filteredCareTeamMembers?.length === 1 && fields.ids.isEmpty()) {
            onChangeField(`${name}.ids`, [first(filteredCareTeamMembers).id])
        }
    }, [name,  fields.ids, filteredCareTeamMembers, onChangeField])

    return (
        <div className="AddToGroupConversationForm-Section">
            <div className="AddToGroupConversationForm-SectionTitle">
                Community Care Team
            </div>

            <Row>
                <BaseSection
                    name={name}
                    fields={fields}
                    errors={errors}
                    params={{
                        withAccessibleCommunityCareTeamMembers: true
                    }}
                    onChangeField={onChangeBaseField}
                />
            </Row>

            <Row>
                <Col>
                    <SelectField
                        name={`${name}.ids`}
                        value={ids}
                        hasTags
                        isMultiple
                        hasKeyboardSearch
                        hasKeyboardSearchText
                        options={careTeamOptions}
                        label="Care Team Members"
                        placeholder="Select team members"
                        className="AddToGroupConversationForm-SelectField"
                        isDisabled={
                            fields.communityIds.isEmpty()
                            || isFetchingCareTeamMembers
                            || isEmpty(careTeamOptions)
                        }
                        onChange={changeSelectField}
                        errorText={errors.ids}
                    />
                </Col>
            </Row>
        </div>
    )
}

export default memo(CareTeamMembersSection)