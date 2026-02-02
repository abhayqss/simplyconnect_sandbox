import React, {
    useState,
    useEffect,
    useCallback
} from 'react'

import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import { Badge } from 'reactstrap'

import { useListDataFetch } from 'hooks/common/redux'

import Table from 'components/Table/Table'
import Actions from 'components/Table/Actions/Actions'
import ErrorViewer from 'components/ErrorViewer/ErrorViewer'

import actions from 'redux/referral/request/list/referralRequestListActions'

import { SERVER_ERROR_CODES } from 'lib/Constants'

import { DateUtils as DU, isInteger } from 'lib/utils/Utils'

import './ReferralRequests.scss'

import RequestViewer from '../RequestViewer/RequestViewer'

const STATUS_COLORS = {
    PENDING: '#e0e0e0',
    ACCEPTED: '#d5f3b8',
    PRE_ADMIT: '#ffedc2',
    DECLINED: '#fde1d5',
    CANCELLED: '#fcccb8'
}

const DATE_FORMAT = DU.formats.americanMediumDate

function isIgnoredError (e = {}) {
    return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE
}

function mapStateToProps(state) {
    return { state: state.referral.request.list }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(actions, dispatch)
    }
}

function ReferralRequests({
    state,
    actions,
    clientId,
    requestId,
    referralId
}) {
    const {
        error,
        isFetching,
        shouldReload,
        dataSource: ds
    } = state

    const [selected, setSelected] = useState(false)
    const [isViewerOpen, setViewerOpen] = useState(false)
    const [shouldRefresh, setShouldRefresh] = useState(true)

    const { fetch, fetchIf } = useListDataFetch(
        state, actions, { referralId }
    )

    const onView = useCallback(data => {
        setSelected(data)
        setViewerOpen(true)
    }, [])

    const onCloseViewer = useCallback(() => {
        setSelected(null)
        setViewerOpen(false)
    }, [])

    useEffect(() => {
        setShouldRefresh(false)
        fetchIf(shouldRefresh || shouldReload)
    }, [ fetchIf, shouldReload, shouldRefresh ])

    return (
        <div className="ReferralRequests">
            <Table
                hasHover
                hasOptions
                hasPagination
                keyField='id'
                title="Shared with"
                noDataText="No responses."
                isLoading={isFetching}
                className='ReferralRequestList'
                containerClass='ReferralRequestListContainer'
                data={ds.data}
                pagination={ds.pagination}
                onRefresh={fetch}
                columns={[
                    {
                        dataField: 'organization',
                        text: 'Organization',
                        sort: true,
                        onSort: actions.sort,
                    },
                    {
                        dataField: 'community',
                        text: 'Service Provider',
                        sort: true,
                        onSort: actions.sort,
                    },
                    {
                        dataField: 'statusTitle',
                        text: 'Status',
                        sort: true,
                        onSort: actions.sort,
                        formatter: (v, row) => (
                            <div
                                className="ReferralRequest-Status"
                                style={{ backgroundColor: STATUS_COLORS[row.statusName] }}
                            >
                                {v}
                            </div>
                        )
                    },
                    {
                        dataField: 'date',
                        text: 'Date',
                        sort: true,
                        headerAlign: 'right',
                        align: 'right',
                        onSort: actions.sort,
                        formatter: v => v ? DU.format(v, DATE_FORMAT) : ''
                    },
                    {
                        dataField: '@actions',
                        text: '',
                        align: 'right',
                        formatter: (v, row) => (
                            <span
                                className="ReferralRequest-ViewBtn"
                                onClick={() => { onView(row) }}
                            >
                                View Details
                            </span>
                        )
                    }
                ]}
                columnsMobile={['organization', 'network']}
                renderCaption={title => {
                    return (
                        <>
                            <div className='ReferralRequestList-Caption'>
                                <div className="flex-2">
                                    <div className="ReferralRequests-Title Table-Title">
                                        {title}
                                    </div>
                                    {ds.pagination.totalCount > 0 && (
                                        <Badge color='info' className="ReferralRequests-Count">
                                            {ds.pagination.totalCount}
                                        </Badge>
                                    )}
                                </div>
                            </div>
                        </>
                    )
                }}
            />
            {isViewerOpen && (
                <RequestViewer
                    isOpen
                    clientId={clientId}
                    referralId={referralId}
                    requestId={requestId || selected?.id}
                    onClose={onCloseViewer}
                />
            )}
            {error && !isIgnoredError(error) && (
                <ErrorViewer
                    isOpen
                    error={error}
                    onClose={actions.clearError}
                />
            )}
        </div>
    )
}

export default connect(mapStateToProps, mapDispatchToProps)(ReferralRequests)