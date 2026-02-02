import React, { memo, useState } from 'react'

import cn from 'classnames'

import { useSelector } from 'react-redux'

import { useHistory } from 'react-router-dom'

import { Badge } from 'reactstrap'

import { useAssessmentsQuery } from 'hooks/business/client/dashboard'

import { ErrorViewer } from 'components'

import ClientAssessments from './ClientAssessments/ClientAssessments'
import ClientSummaryFallback from '../ClientSummaryFallback/ClientSummaryFallback'
import ClientAssessmentPieChart from './ClientAssessmentPieChart/ClientAssessmentPieChart'

import { path } from 'lib/utils/ContextUtils'

import './ClientAssessmentsSummary.scss'

function ClientAssessmentsSummary({ clientId, className }) {
    const [isErrorViewerOpen, toggleErrorViewer] = useState(false)

    const history = useHistory()

    const canAddAssessment = useSelector(state => state.client.assessment.can.add.value)

    const {
        data,
        error,
        fetchMore,
        isFetching,
        pagination
    } = useAssessmentsQuery({ clientId, size: 10 }, {
        onError: () => toggleErrorViewer(true)
    })

    const onCreateAssessment = () => {
        history.push(path(`/clients/${clientId}/assessments`), { shouldCreate: true })
    }

    return (
        <div className={cn('ClientAssessmentsSummary', className)}>
            <div className='ClientAssessmentsSummary-Title'>
                <span className='ClientAssessmentsSummary-TitleText'>
                    Assessments
                </span>
                <Badge
                    color='info'
                    className='ClientAssessmentsSummary-AssessmentCount'
                >
                    {pagination.totalCount}
                </Badge>
            </div>
            <ClientSummaryFallback
                isShown={!data?.length}
                noDataMessage={(
                    <>
                        <span>No data.</span>
                        {canAddAssessment && (
                            <span
                                onClick={onCreateAssessment}
                                className="ClientDashboard-Link"
                            >
                                Create an Assessment
                            </span>
                        )}
                    </>
                )}
            >
                <div className="ClientAssessmentsSummary-Body">
                    <ClientAssessmentPieChart
                        clientId={clientId}
                        className="ClientAssessmentsSummary-Chart flex-1"
                    />
                    <ClientAssessments
                        data={data}
                        clientId={clientId}
                        isLoading={isFetching}
                        onRefresh={fetchMore}
                        className="flex-1"
                    />
                </div>
            </ClientSummaryFallback>

            {isErrorViewerOpen && (
                <ErrorViewer
                    isOpen
                    error={error}
                    onClose={() => toggleErrorViewer(false)}
                />
            )}
        </div>
    )
}

export default memo(ClientAssessmentsSummary)