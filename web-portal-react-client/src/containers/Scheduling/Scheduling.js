import React from 'react'

import { Route, Switch, useHistory } from 'react-router-dom'
import { ConnectedRouter } from 'connected-react-router'

import Footer from 'components/Footer/Footer'

import './Scheduling.scss'

import SideBar from '../SideBar/SideBar'

import Appointments from '../Appointments/Appointments'

import { path } from 'lib/utils/ContextUtils'

function Scheduling() {
    return (
        <ConnectedRouter history={useHistory()}>
            <div className="Scheduling">
                <SideBar>
                    <Switch>
                        <Route
                            exact
                            path={path('/')}
                            component={Appointments}
                        />
                    </Switch>
                    <Footer
                        theme='gray'
                        hasLogo={false}
                        className='Scheduling-Footer'
                    />
                </SideBar>
            </div>
        </ConnectedRouter>
    )
}

export default Scheduling