const { Record } = require('immutable')

const ClientExpense = Record({
	date: null,
	typeName: null,
	cost: null,
	comment: null
})

export default ClientExpense