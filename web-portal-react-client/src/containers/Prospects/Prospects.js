import React, { memo } from 'react'

import { useRouteMatch } from 'react-router-dom'

import Footer from 'components/Footer/Footer'

import SideBar from 'containers/SideBar/SideBar'

import { MapAllowedRoutes } from 'routes'

import {
    useProspectRouteSaving
} from 'hooks/business/Prospects'

import { getAllowedRoutes } from 'lib/utils/UrlUtils'

import './Prospects.scss'

function Prospects({ children: routes }) {
    const match = useRouteMatch()

    useProspectRouteSaving()

    return (
        <div className="Prospects">
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

export default memo(Prospects)
