import React, { memo } from 'react'

import { useRouteMatch } from 'react-router-dom'

import Footer from 'components/Footer/Footer'

import SideBar from 'containers/SideBar/SideBar'

import {
    useClientRouteSaver,
    useAllowedClientRoutes
} from 'hooks/business/client'

import { MapAllowedRoutes } from 'routes'

import './Clients.scss'

function Clients({ children: routes }) {
    const match = useRouteMatch()

    const allowedRoutes = useAllowedClientRoutes(routes)

    useClientRouteSaver()

    return (
        <div className="Clients">
            <SideBar>
                <MapAllowedRoutes
                    routes={allowedRoutes}
                    basePath={match.path}
                />

                <Footer theme="gray"/>
            </SideBar>
        </div>
    )
}

export default memo(Clients)
