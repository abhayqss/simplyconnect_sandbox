import React from 'react'

import cn from 'classnames'

import { Popover } from 'reactstrap'

import { OutsideClickListener } from 'components'

import Avatar from 'containers/Avatar/Avatar'
import UserProfile from 'containers/User/UserProfile/UserProfile'

import {
    useToggle
} from 'hooks/common'

import {
    useAuthUser,
    useBoundActions
} from 'hooks/common/redux'

import { logout as logoutAction } from 'redux/auth/logout/logoutActions'

import { stopImmediatePropagation } from 'lib/utils/Utils'

import userImgSrc from 'images/user.png'

import { ReactComponent as Chevron } from 'images/mini-chevron.svg'

import './UserNavItem.scss'

function UserNavItem() {
    const [isOpen, toggleOpen] = useToggle(false)

    const user = useAuthUser()
    const logout = useBoundActions(logoutAction)

    function close() {
        toggleOpen(false)
    }

    if (!user) return null

    return (
        <OutsideClickListener onClick={close}>
            <div
                id="UserNavItem"
                onClick={toggleOpen}
                className="UserNavItem"
            >
                <Avatar
                    isRound
                    size={32}
                    id={user.avatarId}
                    name={user.fullName}
                    defaultSrc={userImgSrc}
                    className="UserNavItem-Avatar"
                />

                <div className="UserNavItem-Menu">
                    <div className="UserNavItem-Label">{user.fullName}</div>

                    <Chevron
                        className={cn('UserNavItem-Chevron', {
                            'UserNavItem-Chevron_inverted': isOpen
                        })}
                    />
                </div>
            </div>

            <Popover
                isOpen={isOpen}
                toggle={toggleOpen}
                target="UserNavItem"
                onClick={stopImmediatePropagation}
                className="UserNavItem-Popover"
                placement="bottom-end"
            >
                <UserProfile
                    user={user}
                    onSignOut={logout}
                    className="UserNavItem-UserProfile"
                />
            </Popover>
        </OutsideClickListener>
    )
}

export default UserNavItem
