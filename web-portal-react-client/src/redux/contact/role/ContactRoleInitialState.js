import QAUnavailable from './qa-unavailable/ContactQAUnavailableRoleInitialState'

const { Record } = require('immutable')

export default Record({
	qaUnavailable: QAUnavailable()
})