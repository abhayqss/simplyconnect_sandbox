import * as MU from "../Utils";

import {
    Network,
    Community,
    Organization,
    ReferralStatus
} from '../DB'

export default function ReferralResponseListItem() {
    const organization = MU.getRandomArrayElement(Organization)
    const network = MU.getRandomArrayElement(Network)
    const status = MU.getRandomArrayElement(ReferralStatus)
    const community = MU.getRandomArrayElement(Community)

    return {
        id: 0,
        organization: organization.title,
        community: community.title,
        network,
        statusName: status.name,
        statusTitle: status.title,
        date: MU.getRandomDate(Date.now() - 999999, Date.now()).getTime()
    }
}