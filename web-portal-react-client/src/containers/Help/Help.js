import React, { memo } from 'react'

import { useRouteMatch } from 'react-router-dom'

import Footer from 'components/Footer/Footer'

import SideBar from 'containers/SideBar/SideBar'

import { MapAllowedRoutes } from 'routes'

import { getAllowedRoutes } from 'lib/utils/UrlUtils'

import './Help.scss'

function Help({ children: routes }) {
    const match = useRouteMatch()

    return (
        <div className="Help">
            <SideBar>
                <MapAllowedRoutes
                    routes={getAllowedRoutes(routes)}
                    basePath={match.path}
                />

                <Footer
                    theme="gray"
                    className="Help-Footer"
                />
            </SideBar>
        </div>
    )    
}

export default memo(Help)
