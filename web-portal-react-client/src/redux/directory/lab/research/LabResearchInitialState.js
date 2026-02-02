import Order from './order/LabResearchOrderInitialState'
import Reason from './reason/LabResearchReasonInitialState'
import IcdCode from './icd-code/LabResearchIcdCodeInitialState'
import PolicyHolderRelation from './policy-holder-relation/LabResearchPolicyHolderRelationInitialState'

const { Record } = require('immutable')

export default Record({
    order: Order(),
    reason: Reason(),
    icdCode: IcdCode(),
    policyHolderRelation: PolicyHolderRelation()
})
