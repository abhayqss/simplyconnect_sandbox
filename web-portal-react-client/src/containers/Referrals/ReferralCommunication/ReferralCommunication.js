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
import ErrorViewer from 'components/ErrorViewer/ErrorViewer'
import ReferralInfoRequestViewer from '../ReferralInfoRequestViewer/ReferralInfoRequestViewer'

import actions from 'redux/referral/info/request/list/referralInfoRequestListActions'

import { REFERRAL_TYPES, SERVER_ERROR_CODES } from 'lib/Constants'

import { anyIsInteger, DateUtils as DU } from 'lib/utils/Utils'

import './ReferralCommunication.scss'

const STATUS_COLORS = {
    PENDING: '#e0e0e0',
    REPLIED: '#d5f3b8'
}

const { INBOUND, OUTBOUND } = REFERRAL_TYPES

const DATE_FORMAT = DU.formats.longDateMediumTime12

function isIgnoredError(e = {}) {
    return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE
}

function mapStateToProps(state) {
    return { state: state.referral.info.request.list }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(actions, dispatch)
    }
}

function ReferralCommunication(
    {
        state,
        actions,
        requestId,
        referralId,
        referralType,
    }
) {
    const {
        error,
        isFetching,
        shouldReload,
        dataSource: ds
    } = state

    const { fetch, fetchIf } = useListDataFetch(
        state, actions, referralType === INBOUND ? { requestId } : { referralId }
    )

    const [ selected, setSelected ] = useState(null)
    const [ isViewerOpen, setIsViewerOpen ] = useState(false)
    const [ shouldRefresh, setShouldRefresh ] = useState(true)

    const onViewDetails = useCallback(data => {
        setSelected(data)
        setIsViewerOpen(true)
    }, [])

    const onViewerClose = useCallback(() => {
        setSelected(null)
        setIsViewerOpen(false)
    }, [])

    useEffect(() => {
        setShouldRefresh(false)
        fetchIf(shouldRefresh || shouldReload)
    }, [ fetchIf, shouldReload, shouldRefresh ])

    return (
        <div className="ReferralCommunication">
            <Table
                hasHover
                hasOptions
                hasPagination
                keyField='id'
                title="Requests for information"
                noDataText="No requests."
                isLoading={isFetching}
                className='ReferralInfoRequestList'
                containerClass='ReferralInfoRequestListContainer'
                data={ds.data}
                pagination={ds.pagination}
                onRefresh={fetch}
                columns={[
                    {
                        dataField: 'requestDate',
                        text: 'Request Date',
                        sort: true,
                        onSort: actions.sort,
                        formatter: v => v ? DU.format(v, DATE_FORMAT) : ''
                    },
                    {
                        dataField: 'author',
                        text: 'Author',
                        sort: true,
                        onSort: actions.sort,
                    },
                    {
                        dataField: 'subject',
                        text: 'Subject',
                        formatter: v => (
                            <div className="line-clamp-2_expandable">
                                {v}
                            </div>
                        )
                    },
                    {
                        dataField: 'statusTitle',
                        text: 'Status',
                        sort: true,
                        onSort: actions.sort,
                        formatter: (v, row) => (
                            <div
                                className="ReferralInfoRequest-Status"
                                style={{ backgroundColor: STATUS_COLORS[row.statusName] }}
                            >
                                {v}
                            </div>
                        )
                    },
                    {
                        dataField: '@actions',
                        text: '',
                        formatter: (v, row) => (
                            <span
                                className="ReferralInfoRequest-ViewBtn"
                                onClick={() => { onViewDetails(row) }}
                            >
                                View Details
                            </span>
                        )
                    },
                ]}
                columnsMobile={['requestDate', 'author']}
                renderCaption={title => {
                    return (
                        <>
                            <div className='ReferralInfoRequestList-Caption'>
                                <div className="flex-2">
                                    <div className="ReferralCommunication-Title Table-Title">
                                        {title}
                                    </div>
                                    {ds.pagination.totalCount > 0 && (
                                        <Badge color='info' className="ReferralCommunication-Count">
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
                <ReferralInfoRequestViewer
                    isOpen
                    requestId={requestId}
                    referralId={referralId}
                    referralType={referralType}
                    infoRequestId={selected?.id}
                    canReply={
                        selected?.canRespond 
                        && selected?.statusName === 'PENDING'
                        && referralType === OUTBOUND
                    }
                    requestAvailable={selected?.requestAvailable}
                    onClose={onViewerClose}
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

export default connect(mapStateToProps, mapDispatchToProps)(ReferralCommunication)