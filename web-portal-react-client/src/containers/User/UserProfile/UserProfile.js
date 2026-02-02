import React from 'react'

import cn from 'classnames'

import { Button } from 'reactstrap'

import Avatar from 'containers/Avatar/Avatar'

import userImgSrc from 'images/user.png'

import './UserProfile.scss'

function UserProfile({ user, className, onSignOut }) {
    return (
        <div className={cn('UserProfile', className)}>
            <div className="UserProfile-Avatar">
                <Avatar
                    isRound
                    size={90}
                    id={user.avatarId}
                    name={user.fullName}
                    defaultSrc={userImgSrc}
                />
            </div>

            <div className="UserProfile-Details">
                <div className="UserProfile-Name">{user.fullName}</div>
                <div className="UserProfile-Role">{user.roleTitle}</div>

                <Button
                    color="success"
                    onClick={onSignOut}
                    className="UserProfile-LogOutBtn"
                >
                    Sign Out
                </Button>
            </div>
        </div>
    )
}

export default UserProfile
