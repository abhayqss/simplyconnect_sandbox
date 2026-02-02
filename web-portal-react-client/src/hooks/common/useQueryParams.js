import { useMemo } from 'react'
import { useLocation } from 'react-router-dom'

import { useRefCurrent } from 'hooks/common/index'

import { getQueryParams } from 'lib/utils/UrlUtils'

function useQueryParams(options, keys) {
    options = useRefCurrent(options ?? {})
    keys = useRefCurrent(keys ?? [])

    const location = useLocation()

    return useMemo(() => getQueryParams(
        location.search, options, keys
    ), [options, keys, location.search])
}

export default useQueryParams
