import React from 'react'

import { Logo } from 'components'

export default function NewPasswordSuccess ({ companyId }) {
    return (
        <div className="NewPasswordSuccess">
            <Logo
                iconSize={76}
                className="NewPasswordSuccess-LogoImage"
            />
            <div className="d-flex flex-column">
                <span className="NewPasswordSuccess-Title">
                    Create New Password
                </span>
                <span className="NewPasswordSuccess-InfoText">
                    Thank you for changing your password.
                    Please use your email as login and {companyId} as company ID.
                </span>
            </div>
        </div>
    )
}