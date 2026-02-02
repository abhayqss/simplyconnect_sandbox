import { useEffect, useCallback } from 'react'

import { noop, isNull } from 'underscore'

import { useLocation } from 'react-router-dom'

import { useRefCurrent } from 'hooks/common/index'
import { useFilter } from 'hooks/common/filter'
import { useAuthUser } from 'hooks/common/redux/index'
import { useOrganizationsQuery } from 'hooks/business/directory'

export default function usePrimaryFilter(
    name,
    fields,
    actions,
    {
        onRestored = noop,
        isAutoReload = true,
        getInitialData = data => data,
        organizations: { query = { params: null } } = {}
    } = {}
) {
    const { organizationId } = fields

    const user = useAuthUser()
    const location = useLocation()

    const options = useRefCurrent({ onRestored, getInitialData })

    const {
        save,
        remove,
        restore,
        isSaved
    } = useFilter(name, null, {
            onClear: () => {
                clear({}, false)
            },
            onRestore: data => {
                data = getInitialData(data)

                if (isSaved()) {
                    change(data, false).then(options.onRestored(data))
                }
            }
        }
    )

    const clear = useCallback((changes, shouldReload = true, shouldSave = false, shouldRemove = false) => {
        shouldSave && save(changes)
        shouldRemove && remove()
        return actions.clearFilter(changes, shouldReload, true)
    }, [actions, save, remove])

    const change = useCallback((changes = {}, shouldReload = true, shouldSave = true) => {
        shouldSave && save(changes)
        return actions.changeFilter(changes, shouldReload, true)
    }, [ actions, save ])

    const changeField = useCallback((name, value, shouldReload, shouldSave = true) => {
        return clear({
            communityIds: [],
            organizationId: fields.organizationId,
            ...{ [name]: value }
        }, shouldReload, shouldSave)
    }, [ clear, fields.organizationId ])

    useOrganizationsQuery(query.params, query.options)

    useEffect(() => {
        if (user && isNull(organizationId) && !(
            isSaved() || location.state?.selected
        )) {
            change(
                options.getInitialData({
                    organizationId: user.organizationId
                }),
                isAutoReload,
                false
            )
        }
    }, [user, isSaved, change, options, location, isAutoReload, organizationId])

    return {
        save,
        remove,
        restore,
        isSaved,
        clear,
        change,
        changeField
    }
}