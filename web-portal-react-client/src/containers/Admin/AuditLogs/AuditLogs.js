import React, {
    memo,
    useState,
    useEffect,
    useCallback
} from 'react'

import cn from 'classnames'

import { connect } from 'react-redux'
import { compose, bindActionCreators } from 'redux'

import {
    Button,
    Collapse
} from 'reactstrap'

import DocumentTitle from 'react-document-title'

import {
    useListState
} from 'hooks/common'

import {
    useAuditLogsQuery,
    useAuditLogsDownload,
    useReducedAuditLogFilterData
} from 'hooks/business/admin/audit'

import {
    ErrorViewer,
    Breadcrumbs
} from 'components'

import { UpdateSideBarAction } from 'actions/admin'

import { auditLogListActions } from 'redux/index'

import AuditLogList from './AuditLogList/AuditLogList'
import AuditLogFilter from './AuditLogFilter/AuditLogFilter'

import { isInteger } from 'lib/utils/Utils'

import { ReactComponent as Filter } from 'images/filters.svg'

import './AuditLogs.scss'

function mapStateToProps(state) {
    return {
        isFilterOpen: state.audit.log.list.isFilterOpen
    }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(auditLogListActions, dispatch)
    }
}

function AuditLogs({ isFilterOpen, actions }) {
    const {
        state,
        setError,
        clearError,
        changeFilter
    } = useListState()

    const isReadyToFetch = isInteger(state.filter.data?.organizationId) && state.filter.data?.fromDate

    const [shouldFetch, setShouldFetch] = useState(true)

    const filterData = useReducedAuditLogFilterData(state.filter.data)

    const {
        sort,
        refresh,
        refetch,
        isFetching,
        pagination,
        data: { data } = {}
    } = useAuditLogsQuery(filterData, {
        onError: setError,
        enabled: shouldFetch && isReadyToFetch
    })

    const onToggleFilter = useCallback(() => {
        actions.toggleFilter(!isFilterOpen)
    }, [actions, isFilterOpen])

    const onSort = useCallback((field, order) => {
        sort(field, order)
        setShouldFetch(true)
    }, [sort])

    const onRefresh = useCallback(page => {
        refresh(page)
        setShouldFetch(true)
    }, [refresh])

    const {
        fetch: download
    } = useAuditLogsDownload(filterData)

    useEffect(() => {
        if (shouldFetch && isReadyToFetch) {
            setShouldFetch(false)
        }
    }, [shouldFetch, isReadyToFetch])

    return (
        <DocumentTitle title="Simply Connect | Audit Logs">
            <>
                <div className="AuditLogs">
                    <Breadcrumbs
                        items={[
                            { title: 'Admin', href: '/admin' },
                            { title: 'Audit Logs', href: '/admin/audit-logs', isActive: true },
                        ]}
                    />
                    <div className="AuditLogs-Header">
                        <div className="AuditLogs-HeaderItem">
                            <div className="AuditLogs-Title">
                                <div className="page-title-main-text">
                                    Audit Logs
                                </div>
                            </div>
                        </div>
                        <div className="AuditLogs-HeaderItem">
                            <div className="AuditLogs-Actions">
                                <Filter
                                    className={cn(
                                        'AuditLogFilter-Icon',
                                        isFilterOpen
                                            ? 'AuditLogFilter-Icon_rotated_90'
                                            : 'AuditLogFilter-Icon_rotated_0',
                                    )}
                                    onClick={onToggleFilter}
                                />

                                <Button
                                    color="success"
                                    className="margin-left-20"
                                    onClick={download}
                                >
                                    Export
                                </Button>
                            </div>
                        </div>
                    </div>
                    <Collapse isOpen={isFilterOpen}>
                        <AuditLogFilter
                            canReApply
                            onApply={refetch}
                            onChange={changeFilter}
                            onReset={onRefresh}
                            className="AuditLogs-Filter margin-bottom-50"
                        />
                    </Collapse>
                    <AuditLogList
                        data={data}
                        isFetching={isFetching}
                        pagination={pagination}
                        onRefresh={onRefresh}
                        onSort={onSort}
                    />
                    {state.error && (
                        <ErrorViewer
                            isOpen
                            error={state.error}
                            onClose={clearError}
                        />
                    )}
                    <UpdateSideBarAction />
                </div>
            </>
        </DocumentTitle>
    )
}

export default compose(
    memo,
    connect(mapStateToProps, mapDispatchToProps)
)(AuditLogs)