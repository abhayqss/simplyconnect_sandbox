import React, {
    useMemo,
    useCallback
} from 'react'

import cn from 'classnames'

import {
    map,
    sortBy,
    groupBy,
    flatten,
    findWhere
} from 'underscore'

import Truncate from 'react-truncate'
import ShowMore from 'react-show-more'

import { connect } from 'react-redux'

import { useHistory } from 'react-router-dom'

import { Tooltip } from 'react-tooltip'
import { Button, Progress } from 'reactstrap'

import ClientSummaryFallback from '../ClientSummaryFallback/ClientSummaryFallback'

import { useInDevelopmentServicePlanQuery } from 'hooks/business/client'

import { path } from 'lib/utils/ContextUtils'

import { isInteger, isNotEmpty } from 'lib/utils/Utils'

import { ReactComponent as Info } from 'images/info.svg'

import ScoringInfo from 'containers/Clients/Clients/ServicePlans/ServicePlanEditor/ScoringInfo/ScoringInfo'

import './ClientServicePlanSummary.scss'

const COLORS = ['green', 'yellow', 'red']

function mapStateToProps(state) {
    return {
        state: state.client.dashboard.servicePlan.details,
        canAddServicePlan: state.client.servicePlan.can.add.value,
        canViewServicePlans: state.client.servicePlan.can.view.value
    }
}

function ClientServicePlanSummary(
    {
        state,
        clientId,
        className,
        canAddServicePlan,
        canViewServicePlans
    }
) {
    const { data, isFetching } = state

    const history = useHistory()

    const needs = useMemo(() => flatten(map(
        groupBy(data?.needs, o => o.priorityId),
        needs => sortBy(needs, 'domainTitle')
    ).reverse()), [data])

    useInDevelopmentServicePlanQuery({ clientId })

    const onViewPlans = useCallback(() => {
        history.push(path(`/clients/${clientId}/service-plans`))
    }, [clientId, history])

    const onCreatePlan = useCallback(() => {
        history.push(path(`/clients/${clientId}/service-plans`), { shouldCreate: true })
    }, [clientId, history])

    let content = (
        <div className="ClientServicePlanSummary-Needs">
            {map(needs, need => {
                const { score } = findWhere(
                    data.scoring, { domainId: need.domainId }
                ) || {}

                return (
                    <div
                        key={`${need.title}.${need.domainId}`}
                        className="ClientServicePlanSummary-Need"
                    >

                        <div
                            className={cn(
                                'ClientServicePlanSummary-NeedIndicator',
                                `ClientServicePlanSummary-NeedIndicator_color_${COLORS[need.priorityId - 1]}`
                            )}
                        />
                        <div className='ClientServicePlanSummary-NeedSummary flex-1'>
                            <div className="d-flex justify-content-between">
                                <span className="ClientServicePlanSummary-NeedDomain">
                                    {need.domainTitle}
                                </span>
                                {isInteger(score) && score > 0 && (
                                    <div className="ClientServicePlanSummary-NeedDomainScoring">
                                        <span className="ClientServicePlanSummary-NeedDomainScoringValue">
                                            {score}
                                        </span>
                                        <div className="d-inline-block position-relative">
                                            <Info
                                                data-tooltip-id={`${need.domainId}`}
                                                className="ClientServicePlanSummary-ScoringInfoIcon"
                                            />
                                            <Tooltip
                                                variant="light"
                                                id={`${need.domainId}`}
                                                backgroundColor="white"
                                                className="ClientServicePlanSummary-ScoringInfoHint"
                                                classNameArrow="ClientServicePlanSummary-ScoringInfoHintArrow"
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
                                                <ScoringInfo/>
                                            </Tooltip>
                                        </div>
                                    </div>
                                )}
                            </div>
                            <div className="ClientServicePlanSummary-NeedTitle">
                                <ShowMore
                                    lines={5}
                                    more='more'
                                    less='less'
                                    anchorClass='ShowMoreBtn'>
                                    {need.title}
                                </ShowMore>
                            </div>
                            {isNotEmpty(need.goals) && (
                                <div className="ClientServicePlanSummary-Goals">
                                    {map(need.goals, o => {
                                        let completion = o.completion ?? 0

                                        return (
                                            <div
                                                key={`${o.title}.${completion}`}
                                                className='ClientServicePlanSummary-Goal'
                                            >
                                                <div className="ClientServicePlanSummary-GoalTitle">
                                                    <div
                                                        title={o.title}
                                                        className="ClientServicePlanSummary-GoalTitleText"
                                                    >
                                                        <Truncate lines={1}>
                                                            {o.title}
                                                        </Truncate>
                                                    </div>
                                                    <span className="ClientServicePlanSummary-GoalProgressPercentage">
                                                        {completion + '%'}
                                                    </span>
                                                </div>
                                                <Progress
                                                    value={completion}
                                                    className="ClientServicePlanSummary-GoalProgressBar"
                                                />
                                            </div>
                                        )
                                    })}
                                </div>
                            )}
                        </div>
                    </div>
                )
            })}
        </div>
    )

    return (
        <div className={cn("ClientServicePlanSummary", className)}>
            <div className="ClientServicePlanSummary-Header">
                <span className="ClientServicePlanSummary-Title">
                    Plan
                </span>
                {canViewServicePlans && (
                    <Button
                        color="success"
                        className="ClientServicePlanSummary-ViewAllBtn"
                        onClick={onViewPlans}>
                        View All Plans
                    </Button>
                )}
            </div>

            <ClientSummaryFallback
                isShown={!data}
                isLoading={isFetching}
                noDataMessage={(
                    <>
                        <span>No data.</span>
                        {canAddServicePlan && (
                            <span
                                onClick={onCreatePlan}
                                className="ClientDashboard-Link"
                            >
                                Create a Plan
                            </span>
                        )}
                    </>
                )}
            >
                {content}
            </ClientSummaryFallback>
        </div>
    )
}

export default connect(mapStateToProps)(ClientServicePlanSummary)