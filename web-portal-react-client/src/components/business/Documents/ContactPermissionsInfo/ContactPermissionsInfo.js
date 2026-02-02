import React from 'react'

import cn from 'classnames'

import './ContactPermissionsInfo.scss'

const RoleDescription = {
    'Viewer': 'Can view/download folder content',
    'Uploader': 'Can view/add/edit content',
    'Admin': 'Can view/add/edit/delete folder content'
}

function ContactPermissionsInfo({ className }) {
    const entries = Object.entries(RoleDescription)

    return (
        <div className={cn('ContactPermissionsInfo', className)}>
            {entries.map(([title, description]) => (
                <div className="ContactPermissionsInfo-Row" key={title}>
                    <span className="ContactPermissionsInfo-InfoText ContactPermissionsInfo-InfoText_bold">
                        {title} —
                    </span>
                    <span className="ContactPermissionsInfo-InfoText"> {description}</span>
                </div>
            ))}
        </div>
    )
}

export default ContactPermissionsInfo
