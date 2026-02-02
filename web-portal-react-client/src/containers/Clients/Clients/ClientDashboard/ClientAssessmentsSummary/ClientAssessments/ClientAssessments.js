import React, {
    useState,
    useEffect,
    useCallback
} from 'react'

import cn from 'classnames'

import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import {
    Badge,
    ListGroupItem as ListItem
} from 'reactstrap'

import { useResponse } from 'hooks/common'
import { useListDataFetch } from 'hooks/common/redux'
import { useAssessmentTypesQuery } from 'hooks/business/client'

import { List, Loader, ErrorViewer } from 'components'

import AssessmentViewer from 'containers/Clients/Clients/Assessments/AssessmentViewer/AssessmentViewer'

import actions from 'redux/client/dashboard/assessment/list/clientAssessmentListActions'

import { PAGINATION } from 'lib/Constants'

import { DateUtils as DU } from 'lib/utils/Utils'

import './ClientAssessments.scss'

const { FIRST_PAGE } = PAGINATION

const { format, formats } = DU

const DATE_FORMAT = formats.americanMediumDate

const TIME_FORMAT = formats.time

function getPointsBadgeType(points) {
    if (points < 4) return 'success'
    if (points < 10) return 'warning'
    if (points > 9) return 'danger'
}

function mapStateToProps(state) {
    return { state: state.client.dashboard.assessment.list }
}

function mapDispatchToProps(dispatch) {
    return { actions: bindActionCreators(actions, dispatch) }
}

function ClientAssessments({ data, isLoading, clientId, className, onRefresh }) {
    const [selected, setSelected] = useState(null)
    const [isViewerOpen, toggleViewer] = useState(false)

    const [selectedArchived, setSelectedArchived] = useState(null)
    const [isArchiveViewerOpen, toggleArchiveViewer] = useState(false)

    const onSelect = useCallback(o => {
        setSelected(o)
        toggleViewer(true)
    }, [])

    const onCloseViewer = useCallback(() => {
        setSelected(null)
        toggleViewer(false)
    }, [])

    const onViewArchived = useCallback(o => {
        setSelectedArchived(o)
        toggleArchiveViewer(true)
    }, [])

    const onCloseArchiveViewer = useCallback(() => {
        setSelectedArchived(null)
        toggleArchiveViewer(false)
    }, [])

    useAssessmentTypesQuery({ clientId })

    return (
        <div className={cn('ClientAssessments', className)}>
            <List
                length={data.length}
                onEndReached={onRefresh}
                renderItem={i => (
                    <ListItem
                        key={i}
                        style={(i % 2 === 0) ? { backgroundColor: '#f9f9f9' } : null}
                        className="ClientAssessmentList-Item ClientAssessment"
                    >
                        {isLoading && i === data.length ? (
                            <Loader />
                        ) : (
                                <>
                                    <div className='d-flex justify-content-between'>
                                        <div
                                            onClick={() => onSelect(data[i])}
                                            className='ClientAssessment-Type'>
                                            {data[i].typeTitle}
                                        </div>
                                        <span className='ClientAssessment-Date'>
                                            {format(data[i].dateStarted, DATE_FORMAT)}
                                        </span>
                                    </div>
                                    <div className='d-flex justify-content-between'>
                                        <div>
                                            <span className="ClientAssessment-Status">
                                                {data[i].status.title}
                                            </span>
                                            {data[i].points > 0 && (
                                                <Badge
                                                    color={getPointsBadgeType(data[i].points)}
                                                    className="ClientAssessment-Points"
                                                >
                                                    {data[i].points} points
                                                </Badge>
                                            )}
                                        </div>
                                        <div className="ClientAssessment-Time">
                                            {format(data[i].dateStarted, TIME_FORMAT)}
                                        </div>
                                    </div>
                                </>
                            )}
                    </ListItem>
                )}
                className="ClientAssessmentList"
            />

            {isViewerOpen && (
                <AssessmentViewer
                    isOpen
                    assessmentId={selected?.id}
                    assessmentTypeId={selected?.typeId}
                    onView={onViewArchived}
                    onClose={onCloseViewer}
                />
            )}
            {isArchiveViewerOpen && (
                <AssessmentViewer
                    isOpen
                    isAssessmentArchived
                    assessmentId={selectedArchived && selectedArchived.id}
                    assessmentTypeId={selectedArchived && selectedArchived.typeId}
                    onClose={onCloseArchiveViewer}
                />
            )}
        </div>
    )
}

export default connect(mapStateToProps, mapDispatchToProps)(ClientAssessments)