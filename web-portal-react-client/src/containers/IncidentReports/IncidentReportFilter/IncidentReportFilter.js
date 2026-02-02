import React, {
    useMemo,
    useState,
    useEffect,
    useCallback
} from 'react'

import cn from 'classnames'
import { map } from 'underscore'

import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import { Row, Col, Button } from 'reactstrap'

import {
    useDirectoryData,
    useLocationState
} from 'hooks/common'

import { useFilter } from 'hooks/common/filter'
import { useCustomFilter } from 'hooks/common/redux'

import {
    useOldestIncidentReportDateQuery,
    useLatestIncidentReportDateQuery
} from 'hooks/business/incident-report'

import {
    useClientsQuery,
    useIncidentReportStatusesQuery
} from 'hooks/business/directory'

import { DateField, SelectField } from 'components/Form'

import listActions from 'redux/incident/report/list/incidentReportListActions'

import {
    isInteger,
    isNotEmpty,
    DateUtils as DU
} from 'lib/utils/Utils'

import { NAME as PRIMARY_FILTER_NAME } from '../IncidentReportPrimaryFilter/IncidentReportPrimaryFilter'

import './IncidentReportFilter.scss'

export const NAME = 'INCIDENT_REPORT_FILTER'

const DEFAULT_DATA = {
    clientId: null,
    statuses: [],
    fromDate: null,
    toDate: null
}

function valueTextMapper({ id, name, title, fullName } = {}) {
    return { value: id || name, text: title || fullName }
}

function mapStateToProps(state) {
    const { list } = state.incident.report

    return {
        fields: list.dataSource.filter,
        isChanged: list.isFilterChanged()
    }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(listActions, dispatch)
    }
}

function IncidentReportFilter(
    {
        fields,
        actions,

        isChanged,

        className
    }
) {
    const {
        communityIds,
        organizationId
    } = fields

    const [isValidated, setValidated] = useState(false)

    const [defaultData, setDefaultData] = useState(DEFAULT_DATA)

    const [{ client } = {}, clearLocationState] = useLocationState(
        { isCached: false }
    )

    function updateDefaultData(data) {
        setDefaultData(s => ({ ...s, ...data }))
    }

    function isValid() {
        return fields.fromDate && fields.toDate
    }

    const { save: savePrimary } = useFilter(PRIMARY_FILTER_NAME)

    const {
        reset,
        apply,
        isSaved,
        changeField
    } = useCustomFilter(NAME, fields, actions, {
        isChanged,
        defaultData,
        canReReset: isNotEmpty(client),
        onReset: () => {
            if (isNotEmpty(client)) {
                clearLocationState()
            }
        },
        onApplied: () => {
            savePrimary({ organizationId, communityIds })
        }
    })

    const {
        clients
    } = useDirectoryData({
        clients: ['client'],
        statuses: ['incident', 'report', 'status']
    })

    useClientsQuery({ organizationId, communityIds }, {
            condition: prevParams => (
                isInteger(organizationId) && (
                    communityIds !== prevParams.communityIds
                )
            )
        }
    )

    const {
        data: statuses
    } = useIncidentReportStatusesQuery({ organizationId }, {
        enabled: isInteger(organizationId),
        onSuccess: data => {
            const statuses = map(data, o => o.name)
            updateDefaultData({ statuses })
            !isSaved() && changeField('statuses', statuses, false, true)
        }
    })

    useOldestIncidentReportDateQuery({ organizationId }, {
        onSuccess: ({ data: fromDate }) => {
            updateDefaultData({ fromDate })
            !isSaved() && changeField('fromDate', fromDate, false, true)
        }
    })

    useLatestIncidentReportDateQuery({ organizationId }, {
        onSuccess: ({ data: toDate }) => {
            updateDefaultData({ toDate })
            !isSaved() && changeField('toDate', toDate, false, true)
        }
    })

    const mappedClients = useMemo(
        () => map(clients, valueTextMapper), [clients]
    )

    const mappedStatuses = useMemo(
        () => map(statuses, valueTextMapper), [statuses]
    )

    const onChangeField = useCallback((name, value) => {
        changeField(name, value, false)
    }, [changeField])

    const onChangeDateField = useCallback((name, value) => {
        const f = name === 'fromDate' ? 'startOf' : 'endOf'

        changeField(name, value ? DU[f](value, 'day').getTime() : null, false)
    }, [changeField])

    const onApply = useCallback(() => {
        if (isValid()) apply()
        setValidated(true)
    }, [apply, isValid])

    const onReset = useCallback(() => {
        reset()
        setValidated(false)
    }, [apply, isValid])

    return (
        <div className={cn('IncidentReportFilter', className)}>
            <Row>
                <Col lg={3} md={6}>
                    <SelectField
                        label="Status"

                        name="statuses"
                        value={fields.statuses}
                        options={mappedStatuses}

                        isMultiple
                        placeholder="Select Status"
                        className="IncidentReportFilter-SelectField"

                        onChange={onChangeField}
                    />
                </Col>
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
                    <DateField
                        name="fromDate"
                        label="Date From*"
                        value={fields.fromDate}
                        maxDate={fields.toDate ?? undefined}
                        placeholder="Select Date"
                        errorText={isValidated && !fields.fromDate ? 'Please type in the Date from' : ''}
                        onChange={onChangeDateField}
                    />
                </Col>
                <Col lg={3} md={6}>
                    <DateField
                        name="toDate"
                        label="Date To*"
                        value={fields.toDate}
                        minDate={fields.fromDate ?? undefined}
                        placeholder="Select Date"
                        errorText={isValidated && !fields.toDate ? 'Please type in the Date to' : ''}
                        onChange={onChangeDateField}
                    />
                </Col>
            </Row>
            <Row>
                <Col md={12}>
                    <Button
                        outline
                        color='success'
                        className="margin-right-25"
                        onClick={onReset}>
                        Clear
                    </Button>
                    <Button
                        color='success'
                        onClick={onApply}>
                        Apply
                    </Button>
                </Col>
            </Row>
        </div>
    )
}

export default connect(mapStateToProps, mapDispatchToProps)(IncidentReportFilter)