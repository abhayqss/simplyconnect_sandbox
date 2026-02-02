import {
	useQueryWatch
} from 'hooks/common'

import {
	getEndOfDayTime,
	getStartOfDayTime
} from 'lib/utils/DateUtils'

export default function useProspectEventFilterInitialization(
	{
		isSaved,
		prospectId,
		changeFields,
		updateDefaultData
	} = {}
) {
	useQueryWatch({
		queryKey: ['OldestEventDate', { prospectId }],
		onSuccess: (data = []) => {
			if (data) {
				const changes = { fromDate: getEndOfDayTime(data) }

				updateDefaultData(changes)

				if (!isSaved()) {
					changeFields(changes)
				}
			}
		}
	})

	useQueryWatch({
		queryKey: ['NewestEventDate', { prospectId }],
		onSuccess: (data = []) => {
			if (data) {
				const changes = { fromDate: getStartOfDayTime(data) }

				updateDefaultData(changes)

				if (!isSaved()) {
					changeFields(changes)
				}
			}
		}
	})
}