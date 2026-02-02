import {
    noop,
    isEqual
} from 'underscore'

import {
    useRefCurrent,
    useMutationWatch
} from 'hooks/common'

import {
    isInteger,
    isNotEqual
} from 'lib/utils/Utils'

import { isEmpty } from 'lib/utils/ArrayUtils'

import {
    useCustomFilter,
    usePrimaryFilter
} from './'

/**
 * Binds filters and list
 * */
export default function useFilterCombination(primary = {}, custom = {}) {
    const config = useRefCurrent()

    const primaryFilter = usePrimaryFilter(primary.name, {
        /** Fetch a list data first time after
         * the restoring a primary filter if a
         * custom filter not saved (that is clear).
         * */
        onRestore: () => {
            if (!customFilter.isSaved()) {
                primaryFilter.apply()
            }
        },
        ...primary
    })

    const customFilter = useCustomFilter(custom.name, custom.entity, {
        ...custom,
        /**
         * Save a primary filter data by applying a custom filter
         * */
        onRestore: () => {
            customFilter.apply()
        },
        onApply: () => {
            if (!primaryFilter.isSaved()) {
                primaryFilter.save()
            }

            custom?.onApply() || noop()
        }
    })

    /**@base-requirement
     * Removing custom filter fields when
     * an organization changed
     * */

    /**@base-requirement
     * Removing custom filter fields when
     * communities changed
     * */

    const {
        organizationId, communityId, communityIds
    } = primaryFilter.data

    useMutationWatch(
        { organizationId, communityId, communityIds },
        prev => {
            if (organizationId !== prev.organizationId) {
                // if changed manually
                if (prev.organizationId) {
                    primaryFilter.apply()
                    customFilter.reset({}, true)
                } else if (!primaryFilter.isSaved()) {
                    primaryFilter.apply()
                }

                // waiting for communityId or communityIds change
                config.isPending = (
                    !isInteger(communityId)
                    && isEmpty(communityIds)
                )
            } else if (
                communityId !== prev.communityId
                || !isEqual(communityIds, prev.communityIds)
            ) {
                // cancel of waiting for communityId or communityIds change
                if (config.isPending) {
                    config.isPending = false
                } else { // if changed manually
                    primaryFilter.apply()
                    customFilter.reset({}, true)
                }
            }
        },
        prev => (
            isInteger(organizationId)
            && isNotEqual({ organizationId, communityId, communityIds }, prev)
        )
    )

    return {
        primary: primaryFilter,
        custom: customFilter
    }
}