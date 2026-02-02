import { useCallback } from 'react'

import { useRefCurrent } from 'hooks/common/index'
import { BaseService } from 'services'

const service = new BaseService()

export default function useFetch(options = {}) {
    options = useRefCurrent(options)

    return useCallback(
        (o = {}) => service.request({ ...o, ...options }),
        [options]
    )
}