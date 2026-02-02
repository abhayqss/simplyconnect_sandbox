import React, {
	memo,
	useState
} from 'react'

import {
	useMedicalContactsQuery
} from 'hooks/business/Prospects'

import {
	ErrorViewer,
	DataLoadable
} from 'components'

import {
	MedicalContactList as List
} from 'components/business/common'

import {
	PAGINATION,
	SERVER_ERROR_CODES
} from 'lib/Constants'

const { MAX_SIZE } = PAGINATION

function isIgnoredError(e = {}) {
	return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE
}

function MedicalContactList({ prospectId }) {
	const [error, setError] = useState(null)

	const {
		data = [],
		isFetching
	} = useMedicalContactsQuery({
		prospectId, size: MAX_SIZE
	}, {
		staleTime: 0,
		onError: setError
	})

	return (
		<div className="MedicalContactListContainer">
			<DataLoadable
				data={data}
				isLoading={isFetching}
				noDataText="No medical contacts"
			>
				{data => <List data={data} />}
			</DataLoadable>
			{error && !isIgnoredError(error) && (
				<ErrorViewer
					isOpen
					error={error}
					onClose={() => setError(null)}
				/>
			)}
		</div>
	)
}

export default memo(MedicalContactList)