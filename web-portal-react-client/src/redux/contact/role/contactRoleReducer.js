import qaUnavailableReducer from './qa-unavailable/contactQAUnavailableRoleReducer'

import InitialState from './ContactRoleInitialState'

const initialState = InitialState()

export default function contactQAUnavailableRoleReducer(state = initialState, action) {
	let nextState = state

	const qaUnavailable = qaUnavailableReducer(state.qaUnavailable, action)
	if (qaUnavailable !== state.qaUnavailable) nextState = nextState.setIn(['qaUnavailable'], qaUnavailable)

	return nextState
}