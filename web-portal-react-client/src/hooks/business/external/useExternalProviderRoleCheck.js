import { SYSTEM_ROLES } from 'lib/Constants'
import authUserStore from 'lib/stores/AuthUserStore'

const { EXTERNAL_PROVIDER } = SYSTEM_ROLES

export default function useExternalProviderRoleCheck() {
    const user = authUserStore.get()
    return user?.roleName === EXTERNAL_PROVIDER
}