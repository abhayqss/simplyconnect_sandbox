import React, { memo } from 'react'

import { useRouteMatch } from 'react-router-dom'

import { MapAllowedRoutes } from 'routes'

import { getAllowedRoutes } from 'lib/utils/UrlUtils'

import './ExternalProvider.scss'

function ExternalProvider({ children: routes }) {
    const match = useRouteMatch()

    return (
        <div className="ExternalProvider">
            <MapAllowedRoutes
                routes={getAllowedRoutes(routes)}
                basePath={match.path}
            />
        </div>
    )
}

export default memo(ExternalProvider)
