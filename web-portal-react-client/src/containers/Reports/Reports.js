import React, { memo } from 'react'

import { connect } from 'react-redux'

import { useRouteMatch } from 'react-router-dom'

import { Footer } from 'components'

import { MapAllowedRoutes } from 'routes'

import { getAllowedRoutes } from 'lib/utils/UrlUtils'

import SideBar from '../SideBar/SideBar'

import './Reports.scss'

function Reports({ children: routes }) {
    const match = useRouteMatch()

    return (
        <div className="Reports">
            <SideBar>
                <MapAllowedRoutes
                    routes={getAllowedRoutes(routes)}
                    basePath={match.path}
                />
                <Footer theme="gray"/>
            </SideBar>
        </div>
    )
}

export default connect(null, null)(memo(Reports))