import { useMemo } from 'react'

import { useLocation, matchPath } from 'react-router-dom'

import { useAuthUser } from 'hooks/common'

import { SYSTEM_ROLES } from 'lib/Constants'
import { path } from 'lib/utils/ContextUtils'
import { getAllowedRoutes } from 'lib/utils/UrlUtils'

const {
    HOME_CARE_ASSISTANT,
} = SYSTEM_ROLES

const RoleRedirectTarget = {
    [HOME_CARE_ASSISTANT]: {
        path: path('/clients/:clientId'),
        exact: true
    }
}

const RedirectRoutesCreators = {
    [HOME_CARE_ASSISTANT]: ({ clientId }) => {
        return [
            {
                redirect: {
                    from: path(`/clients/${clientId}`),
                    to: path(`/clients/${clientId}/assessments`)
                }
            }
        ]
    }
}

function useAllowedClientRoutes(routes) {
    const location = useLocation()
    const user = useAuthUser()

    const config = useMemo(() => {
        const allowedRoutes = getAllowedRoutes(routes)

        const match = matchPath(location.pathname, RoleRedirectTarget[user?.roleName])

        if (match) {
            const createRoutes = RedirectRoutesCreators[user.roleName]
            const routes = createRoutes(match.params)

            allowedRoutes.push(...routes)
        }

        return allowedRoutes
    }, [user, routes, location.pathname])

    return config
}

export default useAllowedClientRoutes