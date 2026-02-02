import { useEffect } from 'react'

import {
    useProspectQuery
} from 'hooks/business/Prospects'

import {
    useDocumentCategoriesQuery,
    useDocumentSignatureStatusesQuery
} from 'hooks/business/directory/query'

import {
    defer,
    DateUtils as DU
} from 'lib/utils/Utils'

import {
    noop
} from 'lib/utils/FuncUtils'

import {
    map
} from 'lib/utils/ArrayUtils'

import { useProspectDocumentOldestDateQuery } from './'

export default function useProspectDocumentFilterDirectory(
    { prospectId } = {},
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
        data: prospect
    } = useProspectQuery({ prospectId })

    const { organizationId } = prospect ?? {}

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
                    categoryIds: ['NO', ...map(data, o => o.id)]
                })

                if (!isFilterSaved()) {
                    changeFilterFields({
                        includeNotCategorized: true,
                        categoryIds: ['NO', ...map(data, o => o.id)]
                    })
                }
            }
        }
    )

    const {
        data: signatureStatuses
    } = useDocumentSignatureStatusesQuery(
        { organizationId },
        {
            enabled: !!organizationId,
            staleTime: 0,
            onSuccess: data => {
                updateFilterDefaultData({
                    includeWithoutSignature: true,
                    signatureStatusNames: ['NO', ...map(data, o => o.name)]
                })

                if (!isFilterSaved()) {
                    changeFilterFields({
                        includeWithoutSignature: true,
                        signatureStatusNames: ['NO', ...map(data, o => o.name)]
                    })
                }
            }
        }
    )

    useProspectDocumentOldestDateQuery(
        { prospectId, organizationId },
        {
            staleTime: 0,
            enabled: Boolean(prospectId) && Boolean(prospect),
            onSuccess: data => {
                const date = data ?? prospect.createdDate

                if (date) {
                    updateFilterDefaultData({ fromDate: date })
                }

                if (date && !isFilterSaved()) {
                    changeFilterField('fromDate', date, true)
                }
            }
        }
    )

    useEffect(() => {
        const time = DU.getTodayEndOfDayTime()

        defer(500).then(() => {
            updateFilterDefaultData({ toDate: time })

            if (!isFilterSaved()) {
                changeFilterField('toDate', time, true)
            }
        })
    }, [
        isFilterSaved,
        changeFilterField,
        updateFilterDefaultData
    ])

    return { categories, signatureStatuses }
}