import store from 'lib/stores/AuthUserStore'

export default function useAuthUser() {
    return store.get()
}