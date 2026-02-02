import Entity from 'entities/ProviderFilter'
import { useCustomFilter } from 'hooks/common/filter'

function useProviderFilter(options) {
    return useCustomFilter('PROVIDER_FILTER', Entity, options)
}

export default useProviderFilter