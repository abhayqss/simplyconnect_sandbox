import Entity from 'entities/CommunityFilter'
import { useCustomFilter } from 'hooks/common/filter'

function useCommunityFilter(name, options) {
    return useCustomFilter(name, Entity, options)
}

export default useCommunityFilter