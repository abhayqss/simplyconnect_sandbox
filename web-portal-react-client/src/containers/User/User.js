import React, {
    useState,
    useCallback
} from 'react'

import cn from 'classnames'
import PTypes from 'prop-types'

import { Button } from 'reactstrap'

import { useHistory } from 'react-router-dom'

import { OutsideClickListener } from 'components'

import { useAuthUser, useBoundActions } from 'hooks/common/redux'
import { useExternalProviderRoleCheck } from 'hooks/business/external'

import { logout as logoutAction } from 'redux/auth/logout/logoutActions'

import { path } from 'lib/utils/ContextUtils'

import userImg from 'images/user.png'

import Avatar from '../Avatar/Avatar'
import UserProfile from './UserProfile/UserProfile'

import './User.scss'

function User({ children, className, avatarSize }) {
    const [isOpen, toggle] = useState(false)

    const user = useAuthUser()
    const history = useHistory()

    const logout = useBoundActions(logoutAction)

    const isExternalProvider = useExternalProviderRoleCheck()

    const onSignOut = () => {
        logout().then(() => {
            history.push(path(
                isExternalProvider ? '/external-provider/home' : '/home'
            ))
        })
    }

    const onPick = () => toggle(isOpen => !isOpen)
    const hide = useCallback(() => toggle(false), [])

    return user ? (
        <OutsideClickListener
            onClick={hide}
            className={cn('User', { 'is-expanded': isOpen }, className)}
        >
            {isOpen && (
                <UserProfile
                    user={user}
                    onSignOut={onSignOut}
                    className="User-UserProfile"
                />
            )}
            <div
                className="User-Container"
                onClick={onPick}
            >
                <Avatar
                    isRound
                    id={user.avatarId}
                    name={user.fullName}
                    defaultSrc={userImg}
                    size={avatarSize}
                    className="User-Avatar"
                />
                {children}
            </div>
        </OutsideClickListener>
    ) : null
}

User.propTypes = {
    className: PTypes.string,
    avatarSize: PTypes.number
}

export default User