import React, { memo } from 'react'

import {
    Route,
    Switch,
    Redirect,
    useRouteMatch
} from 'react-router-dom'

function getRegularPath(path) {
    return path.replace(/\/{2,}/g, '/')
}

function MapAllowedRoutes({ routes, basePath }) {
    const match = useRouteMatch(basePath)

    return (
        <Switch>
            {routes.map((route) => {
                const {
                    path,
                    component: Component,
                    children,
                    title,
                    permission,
                    redirect,
                    ...rest
                } = route

                return (
                    redirect ? (
                        <Redirect
                            key={redirect.from + redirect.to}
                            from={redirect.from}
                            to={redirect.to}
                        />
                    ) : (
                        <Route
                            {...rest}
                            key={path}
                            path={getRegularPath(match.path + path)}
                        >
                            <Component children={children} />
                        </Route>
                    )
                )
            })}
        </Switch>
    )
}

export default memo(MapAllowedRoutes)
