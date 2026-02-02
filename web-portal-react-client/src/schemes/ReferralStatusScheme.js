import { Shape, string, integer } from './types'

const ReferralStatusScheme = Shape({
    status: string().required(),
    referralDeclineReasonId: integer().when(
        'status', (status, schema) => (
            status === 'DECLINED' ? schema.required() : schema 
        )
    )
})

export default ReferralStatusScheme
