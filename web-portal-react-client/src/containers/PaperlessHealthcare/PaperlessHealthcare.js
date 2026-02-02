import React, { memo } from 'react'

import { useRouteMatch } from 'react-router-dom'

import { MapAllowedRoutes } from 'routes'

import { getAllowedRoutes } from 'lib/utils/UrlUtils'

function PaperlessHealthcare({ children: routes }) {
    const match = useRouteMatch()

    return (
        <MapAllowedRoutes
            routes={getAllowedRoutes(routes)}
            basePath={match.path}
        />
    )
}

export default memo(PaperlessHealthcare)
