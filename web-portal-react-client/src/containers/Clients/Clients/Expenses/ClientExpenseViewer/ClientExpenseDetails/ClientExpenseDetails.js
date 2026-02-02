import React, {
	useState
} from 'react'

import PTypes from 'prop-types'

import {
	ErrorViewer,
	DataLoadable
} from 'components'

import {
	ClientExpenseDetails as Content
} from 'components/business/Clients'

import {
	useClientExpenseQuery
} from 'hooks/business/client/expences'

import { allAreInteger } from 'lib/utils/Utils'

import { SERVER_ERROR_CODES } from 'lib/Constants'

function isIgnoredError(e = {}) {
	return e.code === SERVER_ERROR_CODES.ACCOUNT_INACTIVE
}

function ClientExpenseDetails({ clientId, expenseId }) {
	const [error, setError] = useState(null)

	const {
		data,
		isFetching
	} = useClientExpenseQuery(
		{ clientId, expenseId },
		{
			onError: setError,
			enabled: allAreInteger(clientId, expenseId)
		}
	)

	return (
		<>
			<DataLoadable
				data={data}
				isLoading={isFetching}
			>
				{data => <Content data={data}/>}
			</DataLoadable>

			{error && !isIgnoredError(error) && (
				<ErrorViewer
					isOpen
					error={error}
					onClose={() => setError(null)}
				/>
			)}
		</>
	)
}

ClientExpenseDetails.propTypes = {
	clientId: PTypes.number,
	expenseId: PTypes.number
}

export default ClientExpenseDetails