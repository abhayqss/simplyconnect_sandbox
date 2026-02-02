import { lazy } from 'react'

const SignatureNotification = lazy(() => import('containers/SignatureNotification/SignatureNotification'))

export default {
    component: SignatureNotification,
    path: '/signature-notification'
}
