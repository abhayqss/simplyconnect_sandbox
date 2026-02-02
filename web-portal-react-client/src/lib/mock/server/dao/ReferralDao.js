import Referral from '../../db/factories/Referral'
import ReferralListItem from '../../db/factories/ReferralListItem'
import ReferralResponseListItem from '../../db/factories/ReferralResponseListItem'

class ReferralDao {
    total = 0
    responseTotal = 0

    find({ size }) {
        const referrals = []

        for (let i = 0; i < size; i++) {
            referrals.push({ id: i, ...ReferralListItem() })
        }

        this.total = referrals.length * 4

        return referrals
    }

    findById() {
        return Referral()
    }

    findResponses({ size }) {
        const responses = []

        for (let i = 0; i < size; i++) {
            responses.push({ ...ReferralResponseListItem(), id: i * 34 })
        }

        this.responseTotal = responses.length * 2

        return responses
    }

    responseCount() {
        return this.responseTotal
    }

    count() {
        return this.total
    }
}

export default new ReferralDao()