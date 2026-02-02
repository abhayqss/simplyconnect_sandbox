import React, {} from 'react'

import cn from 'classnames'

import { Checkable } from 'components'

import Avatar from 'containers/Avatar/Avatar'

import './CTMember.scss'

export default function ({ data, isChecked, isDisabled, onCheck }) {
    const fullName = `${data.firstName} ${data.lastName}`

    return (
        <Checkable
            data={data}
            isControlled
            onCheck={onCheck}
            isChecked={isChecked}
            isDisabled={isDisabled}
            className="CTMemberSummary"
        >
            <div className="flex-1 h-flexbox">
                <Avatar
                    size={44}
                    name={fullName}
                    id={data.avatarId}
                    className={cn(
                        'CTMemberSummary-Avatar',
                        { 'black-white-filter': isDisabled }
                    )}
                />
                <div className="flex-1 v-flexbox justify-content-center">
                    <div className="CTMemberSummary-Initials">
                        {fullName}
                    </div>
                    <div className="CTMemberSummary-Role">
                        {data.role}
                    </div>
                </div>
            </div>
        </Checkable>
    )
}