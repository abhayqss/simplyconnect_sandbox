import Add from './add/CanAddServicePlanInitialState'
import View from './view/CanViewServicePlanInitialState'
import ReviewByClinician from './review-by-clinician/CanReviewServicePlanByClinicianInitialState'

const { Record } = require('immutable')

export default Record({
	add: Add(),
	view: View(),
	reviewByClinician: ReviewByClinician()
})