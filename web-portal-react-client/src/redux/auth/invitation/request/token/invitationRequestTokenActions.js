import authService from 'services/AuthService'

export function validate(token, params) {
    return () => {
        return authService.validateInvitationRequestToken(token, params)
    }
}