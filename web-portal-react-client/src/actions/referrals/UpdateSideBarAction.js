import React, { memo, useMemo } from 'react'

import { useSelector } from 'react-redux'

import { getSideBarItems } from 'containers/Referrals/SideBarItems'

import {
    SYSTEM_ROLES,
} from 'lib/Constants'

import UpdateSideBarAction from '../sidebar/UpdateSideBarAction'

const { EXTERNAL_PROVIDER } = SYSTEM_ROLES

const selectUser = state => state.auth.login.user.data

function UpdateSideBar({ params }) {
    const user = useSelector(selectUser)

    const isExternalProviderRole = (
        user.roleName === EXTERNAL_PROVIDER
    )

    const changes = useMemo(() => {
        return {
            isHidden: isExternalProviderRole,
            items: getSideBarItems(),
            ...params?.changes
        }
    }, [params, isExternalProviderRole])

    return (
        <UpdateSideBarAction params={{ changes }} />
    )
}

export default memo(UpdateSideBar)