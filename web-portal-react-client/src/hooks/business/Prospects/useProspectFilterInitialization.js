import {
	useEffect
} from 'react'

import {
	useQueryWatch
} from 'hooks/common'

import {
	PROSPECT_STATUSES
} from 'lib/Constants'

const { ACTIVE } = PROSPECT_STATUSES

export default function useProspectFilterInitialization(
	{
		isSaved,
		changeFields,
		updateDefaultData
	} = {}
) {
	useQueryWatch({
		queryKey: ['Directory.ProspectStatuses'],
		onSuccess: () => {
			const changes = { prospectStatus: ACTIVE }

			updateDefaultData(changes)

			if (!isSaved()) {
				changeFields(changes)
			}
		}
	})

	useEffect(() => {
		const changes = {
			prospectStatus: ACTIVE
		}

		updateDefaultData(changes)

		if (!isSaved()) {
			changeFields(changes)
		}
	}, [
		isSaved,
		changeFields,
		updateDefaultData
	])
}