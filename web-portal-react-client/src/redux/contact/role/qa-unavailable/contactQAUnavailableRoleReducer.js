import listReducer from './list/contactQAUnavailableRoleListReducer'

import InitialState from './ContactQAUnavailableRoleInitialState'

const initialState = InitialState()

export default function contactQAUnavailableRoleReducer(state = initialState, action) {
	let nextState = state

	const list = listReducer(state.list, action)
	if (list !== state.list) nextState = nextState.setIn(['list'], list)

	return nextState
}