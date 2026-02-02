import { bool, Shape } from './types'

const OrganizationFeaturesScheme = Shape({
    isChatEnabled: bool(),
    isVideoEnabled: bool(),
    isSignatureEnabled: bool(),
    areAppointmentsEnabled: bool(),
    isPaperlessHealthcareEnabled: bool(),
    areComprehensiveAssessmentsEnabled: bool()
})

export default OrganizationFeaturesScheme