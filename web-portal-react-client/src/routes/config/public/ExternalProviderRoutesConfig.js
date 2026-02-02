import LoginRoutesConfig from './LoginRoutesConfig'
import InvitationRoutesConfig from './InvitationRoutesConfig'
import NewPasswordRoutesConfig from './NewPasswordRoutesConfig'
import ResetPasswordRoutesConfig from './ResetPasswordRoutesConfig'

export default [
    {
        ...LoginRoutesConfig,
        path: '/external-provider/home'
    },
    {
        ...InvitationRoutesConfig,
        path: '/external-provider/invitation'
    },
    {
        ...NewPasswordRoutesConfig,
        path: '/external-provider/reset-password'
    },
    {
        ...NewPasswordRoutesConfig,
        path: '/external-provider/create-password'
    },
    {
        ...ResetPasswordRoutesConfig,
        path: '/external-provider/reset-password-request'
    },
]
