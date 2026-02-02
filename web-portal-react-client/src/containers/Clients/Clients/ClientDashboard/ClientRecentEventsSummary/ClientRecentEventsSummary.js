import React, { useState, useCallback } from 'react'

import cn from 'classnames'

import { useHistory } from 'react-router-dom'

import { Button } from 'reactstrap'

import { useEventsQuery } from 'hooks/business/client/dashboard'

import { ErrorViewer } from 'components'

import { path } from 'lib/utils/ContextUtils'

import ClientRecentEvents from './ClientRecentEvents/ClientRecentEvents'
import ClientSummaryFallback from '../ClientSummaryFallback/ClientSummaryFallback'

import './ClientRecentEventsSummary.scss'

export default function ClientRecentEventsSummary({ clientId, className }) {
    const history = useHistory()

    const [isErrorViewerOpen, toggleErrorViewer] = useState(false)

    const {
        data,
        error,
        isFetching,
    } = useEventsQuery({ clientId, limit: 4 }, {
        onError: () => toggleErrorViewer(true)
    })

    const onViewMore = useCallback(() => {
        history.push(path(`/clients/${clientId}/events`), { tab: 1 })
    }, [clientId, history])

    const onCreateEvent = () => {
        history.push(path(`/clients/${clientId}/events`), { shouldCreateEvent: true })
    }

    return (
        <>
            <ClientSummaryFallback
                title="Recent Events"
                isShown={!data?.length}
                isLoading={isFetching}
                className="ClientRecentEventsSummary-Fallback"
                noDataMessage={(
                    <>
                        <span>No data.</span>
                        <span
                            onClick={onCreateEvent}
                            className="ClientDashboard-Link"
                        >
                            Create an Event
                        </span>
                    </>
                )}
            >
                <div className={cn('ClientRecentEventsSummary', className)}>
                    <div className='ClientRecentEventsSummary-Title'>
                        Recent Events
                    </div>

                    <ClientRecentEvents
                        data={data}
                        clientId={clientId}
                    />

                    <div className='text-right'>
                        <Button
                            color="success"
                            className="ClientRecentEventsSummary-ViewMoreBtn"
                            onClick={onViewMore}
                        >
                            View More
                </Button>
                    </div>
                </div>
            </ClientSummaryFallback>

            {isErrorViewerOpen && (
                <ErrorViewer
                    isOpen
                    error={error}
                    onClose={() => toggleErrorViewer(false)}
                />
            )}
        </>
    )
}