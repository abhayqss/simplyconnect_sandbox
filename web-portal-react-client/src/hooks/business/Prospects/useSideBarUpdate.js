import {
	useMemo,
	useState,
	useEffect,
	useCallback
} from 'react'

import {
	compact
} from 'underscore'

import {
	useRefCurrent,
    useQueryInvalidation
} from 'hooks/common'

import {
    useSideBarUpdate
} from 'hooks/common/redux'

import {
	useCareTeamMemberCountQuery,
	useCanViewCareTeamMemberQuery
} from 'hooks/business/care-team'

import {
	useProspectQuery
} from 'hooks/business/Prospects'

import {
	useProspectDocumentCountQuery
} from 'hooks/business/Prospects/Documents'

import {
	useEventNoteComposedCountQuery
} from 'hooks/business/event'

import {
	PROSPECT_SECTIONS,
	CARE_TEAM_AFFILIATION_TYPES
} from 'lib/Constants'

import {
	isInteger
} from 'lib/utils/Utils'

import { getSideBarItems } from 'containers/Prospects/SideBarItems'

const { CALL_HISTORY } = PROSPECT_SECTIONS
const { BOTH } = CARE_TEAM_AFFILIATION_TYPES

const DEFAULT_OPTIONS = {
	isHidden: false
}

export default function useProspectSideBarUpdate(params, options) {
	const [changes, setChanges] = useState({})
	const [shouldUpdate, setShouldUpdate] = useState(false)

    const invalidate = useQueryInvalidation()

	const {
		prospectId
	} = params ?? {}

	const {
		isHidden
	} = options ?? DEFAULT_OPTIONS

	const {
		data: prospect
	} = useProspectQuery({ prospectId }, {
		enabled: isInteger(prospectId)
	})

	const {
		canRequestRide,
		associatedContact,
		canViewRideHistory
	} = prospect ?? {}

	const canViewCallHistory = associatedContact?.canViewCallHistory

	const {
		data: canViewCareTeamMembers
	} = useCanViewCareTeamMemberQuery({ prospectId }, {
		staleTime: 0,
		enabled: isInteger(prospectId)
	})

    const {
        data: careTeamMemberCount = 0
    } = useCareTeamMemberCountQuery({
        prospectId, affiliation: BOTH
    }, {
        staleTime: 0,
        enabled: isInteger(prospectId)
    })

    const {
        data: documentCount = 0
    } = useProspectDocumentCountQuery({
        prospectId,
        includeDeleted: false
    }, {
        staleTime: 0,
        enabled: isInteger(prospectId)
    })

	const {
		data: eventNoteComposedCount = 0
	} = useEventNoteComposedCountQuery({ prospectId }, {
		staleTime: 0,
		enabled: isInteger(prospectId)
	})

	const counts = useMemo(() => ({
		documentCount,
		careTeamMemberCount,
		eventNoteComposedCount
	}), [
		documentCount,
        careTeamMemberCount,
		eventNoteComposedCount
	])

	const permissions = useMemo(() => ({
		canRequestRide,
		canViewCallHistory,
		canViewRideHistory,
		canViewCareTeamMembers
	}), [
		canRequestRide,
		canViewCallHistory,
		canViewRideHistory,
		canViewCareTeamMembers
	])

	const excluded = useMemo(() => compact(
		[!canViewCallHistory ? CALL_HISTORY : null]
	), [canViewCallHistory])

	const update = useSideBarUpdate()

    useEffect(() => {
        if (shouldUpdate) {
            const options = { isPartialKeyMatch: true }
            invalidate('CareTeamMember.Count', {}, options)
            invalidate('ProspectDocumentCount', {}, options)
            invalidate('EventNoteComposedCount', {}, options)
            setShouldUpdate(false)
        }
    }, [invalidate, shouldUpdate])

	useEffect(() => {
		update({
			isHidden,
			items: getSideBarItems({
				excluded,
				...counts,
                prospectId,
				permissions
			}),
			...changes
		})
	}, [
		update,
		counts,
		changes,
		options,
		excluded,
		isHidden,
        prospectId,
		permissions
	])

	return useCallback((changes = {}) => {
		setChanges(changes)
		setShouldUpdate(true)
	}, [])
}