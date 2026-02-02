import React, { memo } from 'react'

import { useRouteMatch } from 'react-router-dom'

import { MapAllowedRoutes } from 'routes'

import { getAllowedRoutes } from 'lib/utils/UrlUtils'

import './Components/Marketplace.scss'

function Marketplace({ children: routes }) {
  const match = useRouteMatch()

  return (
    <div className="Marketplace">
      <MapAllowedRoutes
        routes={getAllowedRoutes(routes)}
        basePath={match.path}
      />
    </div>
  )
}

export default memo(Marketplace)
