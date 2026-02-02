import BaseService from './BaseService'

export class AuthService extends BaseService {
    validateSession () {
        return super.request({
            method: 'GET',
            url: '/auth/session/validate'
        })
    }

    pulseSession() {
        return super.request({
            method: 'GET',
            url: '/session-pulse'
        })
    }

    validateInvitationRequestToken (token, { isExternalProvider } = {}) {
        return super.request({
            method: 'POST',
            url: `/auth${isExternalProvider ? '/external' : ''}/invitation-request/validate-token`,
            type: 'application/x-www-form-urlencoded',
            body: { token }
        })
    }

    validateResetPasswordRequestToken (token) {
        return super.request({
            method: 'POST',
            url: '/auth/reset-password-request/validate-token',
            type: 'application/x-www-form-urlencoded',
            body: { token }
        })
    }
  // page login
    login (data, { isExternalProvider } = {}) {
        return super.request({
            method: 'POST',
            url: `/auth${isExternalProvider ? '/external' : ''}/login`,
            body: data,
            type: 'json'
        })
    }

    loginFrom4d(data) {
        return super.request({
            method: 'POST',
            url: `/auth/sso4d`,
            body: data,
            type: 'json'
        })
    }

    logout () {
        return super.request({
            method: 'POST',
            url: '/auth/logout'
        })
    }

    declineInvitation (token) {
        return super.request({
            method: 'POST',
            url: '/auth/invitation-request/decline',
            type: 'application/x-www-form-urlencoded',
            body: { token }
        })
    }

    createPassword (data, { isExternalProvider } = {}) {
        return super.request({
            method: 'POST',
            url: `/auth${isExternalProvider ? '/external' : ''}/password/create`,
            body: data
        })
    }

    resetPassword (data) {
        return super.request({
            method: 'POST',
            url: '/auth/password/reset',
            body: data
        })
    }

    changePassword (data, { isExternalProvider }) {
        return super.request({
            method: 'POST',
            url: `/auth${isExternalProvider ? '/external' : ''}/password/change`,
            body: data
        })
    }

    requestPasswordReset (data, { isExternalProvider }) {
        return super.request({
            method: 'POST',
            url: `/auth${isExternalProvider ? '/external' : ''}/password/reset-request`,
            body: data
        })
    }

    findPasswordComplexityRules({ isExternalProvider, ...params }) {
        return super.request({
            method: 'GET',
            url: `/auth${isExternalProvider ? '/external' : ''}/password/complexity-rules`,
            params
        })
    }

    getAuthUser(params) {
        return super.request({
            url: '/auth/user',
            params
        })
    }
}

const service = new AuthService()
export default service