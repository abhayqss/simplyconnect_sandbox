import React, {
    memo,
    useState,
    useEffect,
    useCallback
} from 'react'

import cn from 'classnames'

import { first } from 'underscore'

import { connect } from 'react-redux'
import { compose, bindActionCreators } from 'redux'

import { Link } from 'react-router-dom'

import {
    Badge,
    Collapse,
    UncontrolledTooltip as Tooltip
} from 'reactstrap'

import DocumentTitle from 'react-document-title'

import {
    Table,
    Footer,
    ErrorViewer
} from 'components'

import {
    Button
} from 'components/buttons'

import {
    WarningDialog
} from 'components/dialogs'

import Avatar from 'containers/Avatar/Avatar'

import {
    useRefCurrent
} from 'hooks/common'

import {
    useListDataFetch
} from 'hooks/common/redux'

import {
    useLabResearchOrderCount,
    useCanAddLabResearchOrder,
    useCanReviewLabResearchOrder
} from 'hooks/business/labs'

import listActions from 'redux/lab/research/order/list/labOrderListActions'

import { path } from 'lib/utils/ContextUtils'

import {
    LAB_RESEARCH_ORDER_STATUSES,
    LAB_RESEARCH_ORDER_STATUS_COLORS
} from 'lib/Constants'

import {
    isInteger,
    allAreInteger,
    DateUtils as DU
} from 'lib/utils/Utils'

import { ReactComponent as Filter } from 'images/filters.svg'

import LabOrderEditor from '../LabOrderEditor/LabOrderEditor'
import LabOrderFilter from '../LabResearchOrderFilter/LabResearchOrderFilter'
import LabOrderPrimaryFilter from '../LabOrderPrimaryFilter/LabOrderPrimaryFilter'
import LabResultsReviewEditor from '../LabResultsReviewEditor/LabResultsReviewEditor'

import './LabResearchOrders.scss'

const DATE_FORMAT = DU.formats.americanMediumDate

const { PENDING_REVIEW } = LAB_RESEARCH_ORDER_STATUSES

function mapStateToProps(state) {
    return {
        state: state.lab.research.order.list,
        count: state.lab.research.order.count,
        canAdd: state.lab.research.order.can.add.value,
        canReview: state.lab.research.order.can.review.value
    }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(listActions, dispatch)
    }
}

function LabResearchOrders({ state, actions, count, canAdd, canReview }) {
    const {
        error,
        isFetching,
        shouldReload,
        isFilterOpen,
        dataSource: ds
    } = state

    const { organizationId, communityIds } = ds.filter

    const isSingeCommunity = communityIds.length === 1

    const communityId = isSingeCommunity && first(communityIds)

    const defaultState = useRefCurrent({ isFilterOpen })

    const [isEditorOpen, setIsEditorOpen] = useState(false)
    const [shouldRefresh, setShouldRefresh] = useState(false)
    const [isReviewEditorOpen, setIsReviewEditorOpen] = useState(false)
    const [isReviewWarningOpen, setIsReviewWarningOpen] = useState(false)
    const [organizationIdParameter, setOrganizationIdParameter] = useState(null)

    const onToggleFilter = useCallback(() => {
        actions.toggleFilter(!isFilterOpen)
    }, [actions, isFilterOpen])

    const onReview = useCallback(() => setIsReviewEditorOpen(true), [])

    const onCloseEditor = useCallback(() => {
        setIsEditorOpen(false)
    }, [])

    const onSaveSuccess = useCallback(refresh, [])

    const { fetch, fetchIf } = useListDataFetch(state, actions, { organizationId })

    function refresh() {
        setShouldRefresh(true)
    }

    function refreshIfNeed() {
        setShouldRefresh(false)
        fetchIf(
            isInteger(organizationId)
            && (shouldRefresh || shouldReload)
        )
    }

    function reviewIfNeed() {
        if (allAreInteger(
            organizationId,
            organizationIdParameter
        ) && organizationId === organizationIdParameter
        && count.fetchCount > 0) {
            if (count.value === 0) {
                setIsReviewWarningOpen(true)
            } else {
                setIsReviewEditorOpen(true)
            }

            setOrganizationIdParameter(null)
        }
    }

    useLabResearchOrderCount({
        communityIds,
        organizationId,
        statuses: [PENDING_REVIEW]
    })

    useCanAddLabResearchOrder(
        { communityId: first(communityIds) }
    )

    useCanReviewLabResearchOrder({ organizationId })

    useEffect(
        refreshIfNeed,
        [
            fetchIf,
            communityIds,
            shouldRefresh,
            shouldReload
        ]
    )

    useEffect(
        reviewIfNeed,
        [
            organizationId,
            organizationIdParameter,
            count.fetchCount,
            count.value
        ]
    )

    useEffect(() => () => {
        actions.clear(defaultState)
    }, [defaultState])

    return (
        <DocumentTitle title="Simply Connect | Labs">
            <>
                <div className="LabResearchOrders">
                    <LabOrderPrimaryFilter
                        className="margin-bottom-30"
                        onHandledQueryParams={
                            useCallback(params => setOrganizationIdParameter(params.organizationId), [])
                        }
                    />
                    <div className="LabResearchOrders-Header">
                        <div className="LabResearchOrders-HeaderItem">
                            <div className="LabResearchOrders-Title">
                                <div className="LabResearchOrders-TitleText">
                                    Labs
                                </div>
                                {(ds.pagination.totalCount > 0) ? (
                                    <Badge color='info' className="Badge Badge_place_top-right">
                                        {ds.pagination.totalCount}
                                    </Badge>
                                ) : null}
                            </div>
                        </div>
                        <div className="LabResearchOrders-HeaderItem">
                            <div className="LabResearchOrders-Actions">
                                <Filter
                                    className={cn(
                                        'LabResearchOrderFilter-Icon',
                                        isFilterOpen
                                            ? 'LabResearchOrderFilter-Icon_rotated_90'
                                            : 'LabResearchOrderFilter-Icon_rotated_0',
                                    )}
                                    onClick={onToggleFilter}
                                />
                                {canReview && (
                                    <Button
                                        outline
                                        color="success"
                                        onClick={onReview}
                                        disabled={count.value === 0 || isFetching}
                                        className="ReviewLabResearchOrderBtn margin-left-30"
                                        tooltip={count.value === 0 && {
                                            placement: 'top',
                                            text: 'The are no lab results that require review.'
                                        }}
                                    >
                                        Review <span className="ReviewLabResearchOrderBtn-OptText">Results</span>
                                    </Button>
                                )}
                                <Button
                                    color="success"
                                    disabled={!(isSingeCommunity && canAdd)}
                                    onClick={() => setIsEditorOpen(true)}
                                    className="AddLabResearchOrderBtn margin-left-30"
                                    tooltip={!isSingeCommunity && {
                                        placement: 'top',
                                        text: 'Please choose one community in the filter.'
                                    }}
                                >
                                    Place <span className="AddLabResearchOrderBtn-OptText">Order</span>
                                </Button>
                            </div>
                        </div>
                    </div>
                    <Collapse isOpen={isFilterOpen}>
                        <LabOrderFilter className="margin-bottom-50"/>
                    </Collapse>
                    <Table
                        hasHover
                        hasOptions
                        hasPagination
                        keyField="id"
                        title="Labs"
                        noDataText="No lab orders."
                        isLoading={isFetching}
                        className='LabResearchOrderList'
                        containerClass='LabResearchOrderListContainer'
                        data={ds.data}
                        pagination={ds.pagination}
                        columns={[
                            {
                                dataField: 'clientName',
                                text: 'Client Name',
                                headerClasses: 'LabResearchOrderList-ClientHeader',
                                sort: true,
                                onSort: actions.sort,
                                formatter: (v, row, index, formatExtraData, isMobile) => (
                                    <div className="d-flex flex-row align-items-center">
                                        <Avatar
                                            id={row.clientAvatarId}
                                            name={row.clientName}
                                            className="LabResearchOrderList-ClientAvatar"
                                        />
                                        <div title={v} className='text-trim'>
                                            <Link
                                                id={`${isMobile ? 'm-' : ''}order-${row.id}`}
                                                className='LabResearchOrderList-Client cursor-pointer'
                                                to={path(`/labs/${row.id}`)}
                                                onClick={() => { }}
                                            >
                                                {v}
                                            </Link>
                                        </div>
                                        <Tooltip
                                            target={`${isMobile ? 'm-' : ''}order-${row.id}`}
                                            placement="top"
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
                                            View order details
                                        </Tooltip>
                                    </div>
                                )
                            },
                            {
                                dataField: 'community',
                                text: 'Community',
                                sort: true,
                                onSort: actions.sort
                            },
                            {
                                dataField: 'requisitionNumber',
                                text: 'Requisition #',
                                sort: true,
                                onSort: actions.sort
                            },
                            {
                                dataField: 'reason',
                                sort: true,
                                text: 'Reason for Testing',
                                onSort: actions.sort,
                                headerStyle: { width: '17%' },
                            },
                            {
                                dataField: 'statusName',
                                text: 'Status',
                                sort: true,
                                onSort: actions.sort,
                                headerStyle: { width: '10%' },
                                formatter: (v, row) => (
                                    <div
                                        className="LabResearchOrder-Status"
                                        style={{ backgroundColor: LAB_RESEARCH_ORDER_STATUS_COLORS[v] }}
                                    >
                                        {row.statusTitle}
                                    </div>
                                )
                            },
                            {
                                dataField: 'createdByName',
                                text: 'Created By',
                                sort: true,
                                onSort: actions.sort,
                                headerStyle: { width: '12%' },
                            },
                            {
                                dataField: 'createdDate',
                                text: 'Created',
                                sort: true,
                                headerAlign: 'right',
                                align: 'right',
                                onSort: actions.sort,
                                headerStyle: { width: '12%' },
                                formatter: v => v ? DU.format(v, DATE_FORMAT) : ''
                            }
                        ]}
                        hasCaption={false}
                        columnsMobile={['clientName', 'requisitionNumber']}
                        onRefresh={fetch}
                    />

                    <LabOrderEditor
                        communityId={communityId}
                        organizationId={organizationId}
                        isOpen={isEditorOpen}
                        onClose={onCloseEditor}
                        onSubmit={onSaveSuccess}
                    />

                    <LabResultsReviewEditor
                        communityIds={communityIds}
                        isOpen={isReviewEditorOpen}
                        onClose={useCallback(() => setIsReviewEditorOpen(false), [])}
                        onSubmit={onSaveSuccess}
                        organizationId={organizationId}
                    />

                    {isReviewWarningOpen && (
                        <WarningDialog
                            isOpen
                            title="The are no lab results that require review"
                            buttons={[
                                {
                                    text: 'Close',
                                    onClick: () => setIsReviewWarningOpen(false)
                                }
                            ]}
                        />
                    )}

                    {error && (
                        <ErrorViewer
                            isOpen
                            error={error}
                            onClose={actions.clearError}
                        />
                    )}
                </div>
                <Footer theme='gray'/>
            </>
        </DocumentTitle>
    )
}

export default compose(
    memo,
    connect(mapStateToProps, mapDispatchToProps)
)(LabResearchOrders)