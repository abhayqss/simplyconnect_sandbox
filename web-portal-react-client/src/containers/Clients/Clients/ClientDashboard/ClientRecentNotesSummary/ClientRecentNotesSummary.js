import React, { useState, useCallback } from 'react'

import cn from 'classnames'

import { useHistory } from 'react-router-dom'

import { Button } from 'reactstrap'

import { useNotesQuery } from 'hooks/business/client/dashboard'

import { ErrorViewer } from 'components'

import { path } from 'lib/utils/ContextUtils'

import ClientRecentNotes from './ClientRecentNotes/ClientRecentNotes'
import ClientSummaryFallback from '../ClientSummaryFallback/ClientSummaryFallback'

import './ClientRecentNotesSummary.scss'

export default function ClientRecentNotesSummary({ clientId, className }) {
    const history = useHistory()

    const [isErrorViewerOpen, toggleErrorViewer] = useState(false)

    const {
        data,
        error,
        isFetching
    } = useNotesQuery({ clientId, limit: 4 }, {
        onError: () => toggleErrorViewer(true)
    })

    const onViewMore = useCallback(() => {
        history.push(path(`/clients/${clientId}/events`), { tab: 2 })
    }, [clientId, history])

    const onCreateNote = () => {
        history.push(path(`/clients/${clientId}/events`), { shouldCreateNote: true })
    }

    return (
        <>
            <ClientSummaryFallback
                title="Recent Notes"
                isShown={!data?.length}
                isLoading={isFetching}
                className="ClientRecentNotesSummary-Fallback"
                noDataMessage={(
                    <>
                        <span>No data.</span>
                        <span
                            onClick={onCreateNote}
                            className="ClientDashboard-Link"
                        >
                            Create a Note
                        </span>
                    </>
                )}
            >
                <div className={cn('ClientRecentNotesSummary', className)}>
                    <div className='ClientRecentNotesSummary-Title'>
                        Recent Notes
                    </div>

                    <ClientRecentNotes
                        data={data}
                        clientId={clientId}
                    />

                    <div className='text-right'>
                        <Button
                            color="success"
                            className="ClientRecentNotesSummary-ViewMoreBtn"
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