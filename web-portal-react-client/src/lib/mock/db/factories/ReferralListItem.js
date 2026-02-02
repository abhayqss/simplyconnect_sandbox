import * as MU from "../Utils";

import {
    Network,
    LastName,
    FirstName,
    Organization,
    ReferralStatus,
    ReferralCategory,
    ReferralPriority
} from '../DB'

export default function Referral() {
    const firstName = MU.getRandomArrayElement(FirstName)
    const lastName = MU.getRandomArrayElement(LastName)

    const priority = MU.getRandomArrayElement(ReferralPriority)
    const status = MU.getRandomArrayElement(ReferralStatus)
    const category = MU.getRandomArrayElement(ReferralCategory)

    const organization = MU.getRandomArrayElement(Organization)

    const count = MU.getRandomInt(1, 4)
    const networks = MU.getRandomArrayElements(Network, count)

    return {
        clientId: MU.getRandomInt(0, 999999),
        clientName: `${firstName} ${lastName}`,
        priority: priority.title,
        statusName: status.name,
        statusTitle: status.title,
        category: category.title,
        referredBy: organization.name,
        referredTo: networks,
        date: MU.getRandomDate(Date.now() - 999999, Date.now()).getTime()
    }
}