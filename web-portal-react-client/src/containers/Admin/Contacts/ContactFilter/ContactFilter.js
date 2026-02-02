import React, {
    useMemo,
    useEffect,
    useCallback
} from 'react'

import cn from 'classnames'

import {
    map,
    reject,
    compact
} from 'underscore'

import {
    Row,
    Col,
    Button
} from 'reactstrap'

import {
    useContactFilterDirectory,
    useContactFilterDefaultDataCache
} from 'hooks/business/admin/contact'

import {
    TextField,
    SelectField
} from 'components/Form'

import {
    isNotEmpty
} from 'lib/utils/Utils'

import './ContactFilter.scss'

const NONE = 'NONE'

function mapToValueText(data) {
    return map(data, ({ id, name, title }) => ({
        value: id || name, text: title
    }))
}

export default function ContactFilter(
    {
        data,
        blur,
        focus,
        reset,
        apply,
        isSaved,
        changeField,
        changeFields,

        organizationId,

        className
    }
) {
    const {
        clear: clearDefaultData,
        update: updateDefaultData
    } = useContactFilterDefaultDataCache({ organizationId })

    const {
        statuses = [],
        systemRoles = []
    } = useContactFilterDirectory(
        { organizationId },
        {
            actions: {
                isFilterSaved: isSaved,
                changeFilterFields: changeFields,
                updateFilterDefaultData: updateDefaultData
            }
        }
    )

    const mappedRoles = useMemo(
        () => mapToValueText([
            ...systemRoles, { id: NONE, title: 'None' }
        ]), [systemRoles]
    )

    const mappedStatuses = useMemo(
        () => mapToValueText(statuses), [statuses]
    )

    const onChangeField = useCallback(
        (name, value) => changeField(name, value), [changeField]
    )

    const onChangeSystemRoleField = useCallback((name, value) => {
        changeFields({
            systemRoleIds: reject(value, v => v === NONE),
            includeWithoutSystemRole: (
                isNotEmpty(value) && value.includes(NONE)
            )
        }, false)
    }, [changeFields])

    useEffect(() => () => {
        clearDefaultData()
    }, [clearDefaultData])

    return (
        <div className={cn('ContactFilter', className)}>
            <Row>
                <Col lg={4} md={6} sm={6}>
                    <TextField
                        type="text"
                        name="firstName"
                        value={data.firstName}
                        label="First name"
                        onBlur={blur}
                        onFocus={focus}
                        onChange={onChangeField}
                    />
                </Col>
                <Col lg={4} md={6} sm={6}>
                    <TextField
                        type="text"
                        name="lastName"
                        value={data.lastName}
                        label="Last name"
                        onBlur={blur}
                        onFocus={focus}
                        onChange={onChangeField}
                    />
                </Col>
                <Col lg={4} md={12} sm={12}>
                    <TextField
                        type="email"
                        name="email"
                        value={data.email}
                        label="Login"
                        onBlur={blur}
                        onFocus={focus}
                        onChange={onChangeField}
                    />
                </Col>
            </Row>
            <Row>
                <Col lg={4} md={6} sm={6}>
                    <SelectField
                        isMultiple
                        type="text"
                        name="systemRoleIds"
                        value={compact([
                            ...data.systemRoleIds,
                            data.includeWithoutSystemRole && NONE
                        ])}
                        options={mappedRoles}
                        label="System role"
                        placeholder="Select System Role"
                        onChange={onChangeSystemRoleField}
                    />
                </Col>
                <Col lg={4} md={6} sm={6}>
                    <SelectField
                        isMultiple
                        type="text"
                        name="statuses"
                        options={mappedStatuses}
                        value={data.statuses}
                        label="Status"
                        placeholder="Select Status"
                        onChange={onChangeField}
                    />
                </Col>
                <Col
                    lg={4}
                    md={12}
                    sm={12}
                    className="padding-top-31"
                >
                    <Button
                        outline
                        color='success'
                        data-testid="clear-btn"
                        className="margin-right-16"
                        onClick={reset}
                    >
                        Clear
                    </Button>
                    <Button
                        data-testid="apply-btn"
                        color='success'
                        onClick={apply}
                    >
                        Apply
                    </Button>
                </Col>
            </Row>
            <div className="clearfix"/>
        </div>
    )
}