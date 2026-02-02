import { useState } from 'react'

import { noop, isEqual } from 'underscore'

import { useMutationWatch } from 'hooks/common/index'
import { useBaseCustomFilter } from 'hooks/common/filter'

import { isEmpty, isInteger } from 'lib/utils/Utils'

export default function useCustomFilter(
    name,
    fields,
    actions,
    {
        isChanged,
        defaultData = {},

        canReReset = false,
        canReApply = false,

        onReset = noop,
        onApplied = noop,
        onRestored = noop,

        ...otherOptions
    }
) {
    const {
        toggleFilter: toggle,
        changeFilter: change,
        changeFilterField: changeField
    } = actions

    const {
        communityIds,
        organizationId
    } = fields

    const [isPending, setIsPending] = useState(false)

    const {
        blur,
        focus,
        apply,
        save,
        reset,
        remove,
        restore,
        isSaved
    } = useBaseCustomFilter(name, fields.toJS(), {
        onClear: () => {
            const {
                communityIds, organizationId, ...data
            } = fields.clear().toJS()

            change(data, false, true)
        },
        onRestore: data => {
            isSaved() && change(data).then(onRestored(data))
        },
        onApply: () => {
            if (isChanged || canReApply) {
                save()
                change({}, true, true)
                onApplied()
            }
        },
        onReset: (data = {}, canReload = true) => {
            if (isSaved() || isChanged || canReReset) {
                change({ ...defaultData, ...data}, canReload && (isSaved() || canReReset), true).then(onReset())
                isSaved() && remove()
            }
        },
        ...otherOptions
    })

    const primary = { organizationId, communityIds }

    useMutationWatch(primary, prev => {
        if (isInteger(organizationId)) {
            if (organizationId !== prev.organizationId) {
                if (isInteger(prev.organizationId)) remove()
                if (isEmpty(communityIds)) setIsPending(true)
            }

            else if (!isEqual(communityIds, prev.communityIds)) {
                if (isPending) setIsPending(false)
                else {
                    remove()
                    change(defaultData, false, true)
                }
            }
        }
    }, prev => !isEqual(primary, prev))

    return {
        blur,
        focus,
        reset,
        apply,
        toggle,
        change,
        remove,
        restore,
        isSaved,
        changeField
    }
}