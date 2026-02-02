import React from 'react'

import cn from 'classnames'

import { Badge } from 'reactstrap'

import { Loader } from 'components'

import { ReactComponent as Icon } from 'images/analysis.svg'

import './ClientSummaryFallback.scss'

function ClientSummaryFallback({
    title,
    isShown,
    children,
    className,
    isLoading,
    noDataMessage = 'No data.'
}) {
    if (!title && isLoading) return <Loader className={className} />

    return (isShown || isLoading) ? (
        <div className={cn('ClientSummaryFallback', className)}>
            {title && (
                <div className="ClientSummaryFallback-Title">
                    <span className="ClientSummaryFallback-TitleText">{title}</span>

                    <Badge
                        color='info'
                        className="ClientSummaryFallback-Count"
                    >
                        0
                    </Badge>
                </div>
            )}

            {!isLoading ? (
                <div className="ClientSummaryFallback-Content">
                    <Icon className="ClientSummaryFallback-Icon" />

                    <div className="ClientSummaryFallback-Message">{noDataMessage}</div>
                </div>
            ) : (<Loader className={className} />)}
        </div>
    ) : (
            <>
                {children}
            </>
        )
}

export default ClientSummaryFallback
