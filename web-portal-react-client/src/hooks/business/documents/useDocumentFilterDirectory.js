import { useEffect } from 'react'

import { map, noop } from 'underscore'

import {
    useDocumentCategoriesQuery
} from 'hooks/business/directory/query'

import {
    defer,
    DateUtils as DU
} from 'lib/utils/Utils'

import { useDocumentOldestDateQuery } from './'

function mapToIds(data) {
    return map(data, o => o.id)
}

export default function useDocumentFilterDirectory(
    { organizationId, communityId } = {},
    {
        actions: {
            isFilterSaved = noop(),
            changeFilterField = noop(),
            changeFilterFields = noop(),
            updateFilterDefaultData = noop()
        } = {}
    }
) {
    const {
        data: categories
    } = useDocumentCategoriesQuery(
        { organizationId },
        {
            enabled: !!organizationId,
            staleTime: 0,
            onSuccess: data => {
                updateFilterDefaultData({
                    includeNotCategorized: true,
                    categoryIds: ['NO', ...mapToIds(data)]
                })

                if (!isFilterSaved()) {
                    changeFilterFields({
                        includeNotCategorized: true,
                        categoryIds: ['NO', ...mapToIds(data)]
                    })
                }
            }
        }
    )

    useDocumentOldestDateQuery({ communityId }, {
        staleTime: 0,
        enabled: Boolean(communityId),
        onSuccess: data => {
            const date = data ?? DU.getTodayStartOfDayTime()

            updateFilterDefaultData({ fromDate: date })

            if (date && !isFilterSaved()) {
                changeFilterField('fromDate', date, true)
            }
        }
    })

    useEffect(() => {
        const time = DU.getTodayEndOfDayTime()

        defer(500).then(() => {
            updateFilterDefaultData({ toDate: time })

            if (!isFilterSaved()) {
                changeFilterField('toDate', time, true)
            }
        })
    }, [
        communityId,
        isFilterSaved,
        changeFilterField,
        updateFilterDefaultData
    ])

    return { categories }
}