import { useSelector } from 'react-redux'

export default function useAuthUser() {
    return useSelector(state => (
        state.auth.login.user.data
    ))
}