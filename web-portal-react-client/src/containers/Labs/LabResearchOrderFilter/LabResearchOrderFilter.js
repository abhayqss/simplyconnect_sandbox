import React, {
    useMemo,
    useState,
    useCallback
} from 'react'

import cn from 'classnames'
import { map } from 'underscore'

import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import { Row, Col, Button } from 'reactstrap'

import { useDirectoryData } from 'hooks/common'
import { useFilter } from 'hooks/common/filter'
import { useCustomFilter } from 'hooks/common/redux'

import {
    useClientsQuery,
    useLabResearchReasonsQuery,
    useLabResearchOrderStatusesQuery
} from 'hooks/business/directory'

import { TextField, SelectField } from 'components/Form'

import listActions from 'redux/lab/research/order/list/labOrderListActions'

import { isInteger } from 'lib/utils/Utils'

import { NAME as PRIMARY_FILTER_NAME } from '../LabOrderPrimaryFilter/LabOrderPrimaryFilter'

import './LabResearchOrderFilter.scss'

export const NAME = 'LAB_ORDER_FILTER'

const DEFAULT_DATA = {
    clientId: null,
    requisitionNumber: null,
    reasons: [],
    statuses: []
}

function nameMapper({ name } = {}) {
    return name
}

function valueTextMapper({ id, name, title, fullName } = {}) {
    return { value: id || name, text: title || fullName }
}

function mapStateToProps(state) {
    const { list } = state.lab.research.order

    return {
        isFetching: list.isFetching,
        fields: list.dataSource.filter,
        isChanged: list.isFilterChanged()
    }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(listActions, dispatch)
    }
}

function LabResearchOrderFilter({ fields, isChanged, actions, className }) {
    const {
        communityIds,
        organizationId
    } = fields

    const [defaultData, setDefaultData] = useState(DEFAULT_DATA)

    function updateDefaultData(data) {
        setDefaultData(s => ({ ...s, ...data }))
    }

    const { save: savePrimary } = useFilter(PRIMARY_FILTER_NAME)

    const {
        blur,
        focus,
        reset,
        apply,
        isSaved,
        changeField
    } = useCustomFilter(NAME, fields, actions, {
        isChanged,
        defaultData,
        onApplied: () => {
            savePrimary({ organizationId, communityIds })
        }
    })

    const {
        clients,
        reasons,
        statuses
    } = useDirectoryData({
        clients: [ 'client' ],
        reasons: [ 'lab', 'research', 'reason' ],
        statuses: [ 'lab', 'research', 'order', 'status' ]
    })

    useClientsQuery({ organizationId, communityIds }, {
            condition: prevParams => (
                isInteger(organizationId) && (
                    communityIds !== prevParams.communityIds
                )
            )
        }
    )

    useLabResearchReasonsQuery({ organizationId }, {
        onSuccess: ({ data }) => {
            const reasons = map(data, nameMapper)
            updateDefaultData({ reasons })
            !isSaved() && changeField('reasons', reasons, false, true)
        }
    })

    useLabResearchOrderStatusesQuery({ organizationId }, {
        onSuccess: ({ data }) => {
            const statuses = map(data, nameMapper)
            updateDefaultData({ statuses })
            !isSaved() && changeField('statuses', statuses, false, true)
        }
    })

    const mappedClients = useMemo(
        () => map(clients, valueTextMapper), [ clients ]
    )

    const mappedReasons = useMemo(
        () => map(reasons, valueTextMapper), [ reasons ]
    )

    const mappedStatuses = useMemo(
        () => map(statuses, valueTextMapper), [ statuses ]
    )

    const onChangeField = useCallback((name, value) => {
        changeField(name, value, false)
    }, [ changeField ])

    return (
        <div className={cn('LabResearchOrderFilter', className)}>
            <Row>
                <Col lg={3} md={6}>
                    <SelectField
                        label="Client Name"

                        name="clientId"
                        value={fields.clientId}
                        options={mappedClients}

                        hasKeyboardSearch
                        hasKeyboardSearchText
                        placeholder="Select Client"

                        onChange={onChangeField}
                    />
                </Col>
                <Col lg={3} md={6}>
                    <TextField
                        label="Requisition #"

                        type="text"
                        name="requisitionNumber"
                        value={fields.requisitionNumber}

                        className="LabResearchOrderFilter-TextField"

                        hasError={false}
                        errorText={''}

                        onBlur={blur}
                        onFocus={focus}
                        onChange={onChangeField}
                    />
                </Col>
                <Col lg={3} md={6}>
                    <SelectField
                        label="Reason for testing"

                        name="reasons"
                        value={fields.reasons}
                        options={mappedReasons}

                        isMultiple
                        placeholder="Gender"
                        className="LabResearchOrderFilter-SelectField"

                        hasError={false}
                        errorText={''}

                        onChange={onChangeField}
                    />
                </Col>
                <Col lg={3} md={6}>
                    <SelectField
                        label="Status"

                        name="statuses"
                        options={mappedStatuses}
                        value={fields.statuses}

                        isMultiple
                        placeholder="Status"
                        className="LabResearchOrderFilter-SelectField"

                        hasError={false}
                        errorText={''}

                        onChange={onChangeField}
                    />
                </Col>
            </Row>
            <Row>
                <Col md={4}>
                    <Button
                        outline
                        color='success'
                        className="margin-right-25"
                        onClick={reset}>
                        Clear
                    </Button>
                    <Button
                        color='success'
                        onClick={apply}>
                        Apply
                    </Button>
                </Col>
            </Row>
        </div>
    )
}

export default connect(mapStateToProps, mapDispatchToProps)(LabResearchOrderFilter)