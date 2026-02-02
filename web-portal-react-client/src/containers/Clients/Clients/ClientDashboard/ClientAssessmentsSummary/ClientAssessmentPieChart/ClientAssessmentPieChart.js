import React, { useEffect } from 'react'

import cn from 'classnames'

import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import { useListDataFetch } from 'hooks/common/redux'

import { ErrorViewer } from 'components'
import { AssessmentPieChart } from 'components/charts'

import actions from 'redux/client/dashboard/assessment/statistics/clientAssessmentStatisticsActions'

import './ClientAssessmentPieChart.scss'

function mapStateToProps(state) {
    return { state: state.client.dashboard.assessment.statistics }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(actions, dispatch)
    }
}

function ClientAssessmentPieChart({ state, actions, clientId, className }) {
    const {
        error,
        dataSource: ds
    } = state

    const { fetch } = useListDataFetch(state, actions, { clientId })

    useEffect(() => {
        fetch()
        return () => { actions.clear() }
    }, [ fetch ])

    return (
        <>
            <AssessmentPieChart
                data={ds.data}
                className={cn('ClientAssessmentPieChart ClientDashboard-PieChart', className)}
            />
            {error && (
                <ErrorViewer
                    isOpen
                    error={error}
                    onClose={actions.clearError}
                />
            )}
        </>
    )
}

export default connect(mapStateToProps, mapDispatchToProps)(ClientAssessmentPieChart)