import { Shape, date } from './types'

const ReferralRequestAcceptScheme = ({ serviceStartDate }) => Shape({
    serviceStartDate: date(),
    serviceEndDate: date().test(
        'isEarlierThanserviceEndDate',
        'The date you entered occurs before the start date',
        (value) => !value || !serviceStartDate || value >= serviceStartDate,
    ),
})

export default ReferralRequestAcceptScheme
