import { useMemo } from 'react'

import { map } from 'underscore'

import { useRefCurrent } from 'hooks/common/index'

export default function useSelectOptions(
    data, { textProp = 'title', valueProp = 'id' } = {}
) {
    const mapping = useRefCurrent({
        textProp, valueProp
    })

    return useMemo(() => map(data, o => ({
        text: o[mapping.textProp],
        value: o[mapping.valueProp]
    })), [ data, mapping ])
}