import React, {
    memo,
    useMemo,
    useState,
    useCallback
} from 'react'

import cn from 'classnames'

import { Badge } from 'reactstrap'

import { useProblemStatisticsQuery } from 'hooks/business/client/dashboard'

import { ErrorViewer } from 'components'

import ClientProblems from './ClientProblems/ClientProblems'
import ProblemsBarChart from './ProblemsBarChart/ProblemsBarChart'
import ClientSummaryFallback from '../ClientSummaryFallback/ClientSummaryFallback'

import { PROBLEM_STATUSES } from 'lib/Constants'

import './ClientProblemsSummary.scss'

const { ACTIVE } = PROBLEM_STATUSES

function ClientProblemsSummary({ clientId, className }) {
    let [status, setStatus] = useState(ACTIVE)
    let [isErrorViewerOpen, toggleErrorViewer] = useState(false)

    const {
        data,
        error,
        isFetching,
    } = useProblemStatisticsQuery({ clientId }, {
        onError: () => toggleErrorViewer(true)
    })

    let count = useMemo(() => {
        return data?.reduce((accum, o) => {
            return accum += o.value
        }, 0)
    }, [data])

    let onPickStatus = useCallback(status => (
        setStatus(PROBLEM_STATUSES[status])
    ), [])

    return (
        <div className={cn('ClientProblemsSummary', className)}>
            <div className="ClientProblemsSummary-Title">
                <span className="ClientProblemsSummary-TitleText">Problems</span>

                <Badge
                    color='info'
                    className="ClientProblemsSummary-ProblemsCount"
                >
                    {count}
                </Badge>
            </div>

            <ClientSummaryFallback isShown={!count} isLoading={isFetching}>
                <div className="ClientProblemsSummary-Body">
                    <ProblemsBarChart
                        data={data}
                        status={status}
                        onPickBar={onPickStatus}
                        className="ClientProblemsSummary-Chart"
                    />
                    <ClientProblems
                        status={status}
                        clientId={clientId}
                        onChangeStatus={onPickStatus}
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

export default memo(ClientProblemsSummary)