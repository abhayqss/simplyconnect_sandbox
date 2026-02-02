import * as MU from "../Utils";

import {
    Gender,
    Address,
    Network,
    LastName,
    FirstName,
    Diagnosis,
    Community,
    Organization,
    ReferralStatus,
    ReferralReason,
    ReferralIntent,
    ReferralCategory,
    ReferralPriority,
    TreatmentService
} from '../DB'

export default function Referral() {
    function randFullName() {
        const firstName = MU.getRandomArrayElement(FirstName)
        const lastName = MU.getRandomArrayElement(LastName)
        return `${firstName} ${lastName}`
    }

    function randPhone(delimiter = ' ') {
        return [
            MU.getRandomInt(200, 300),
            MU.getRandomInt(100, 800),
            MU.getRandomInt(1000, 9999)
        ].join(delimiter)
    }

    const count = MU.getRandomInt(1, 4)

    const date = MU.getRandomDate(new Date(2019, 12, 25), new Date(2020, 12, 25))
    const organization = MU.getRandomArrayElement(Organization)
    const network = MU.getRandomArrayElement(Network)
    const priority = MU.getRandomArrayElement(ReferralPriority)
    const status = MU.getRandomArrayElement(ReferralStatus)
    const category = MU.getRandomArrayElement(ReferralCategory)
    const reason = MU.getRandomArrayElement(ReferralReason)
    const intent = MU.getRandomArrayElement(ReferralIntent)
    const service = MU.getRandomArrayElement(TreatmentService)
    const gender = MU.getRandomArrayElement(Gender)
    const birthDate = MU.getRandomDate(new Date(1950, 12, 25), new Date(1980, 12, 25))
    const diagnoses = MU.getRandomArrayElements(Diagnosis, count)
    const community = MU.getRandomArrayElement(Community)
    const address = MU.getRandomArrayElement(Address)
    const authorFullName = randFullName()

    return {
        id: 0,
        request: {
            id: 451252367832,
            date: date.getTime(),
            statusId: 0,
            statusName: status.name,
            statusTitle: status.title,
            priorityId: 0,
            priorityName: priority.title,
            priorityTitle: priority.title,
            intentId: 0,
            intentName: 0,
            intentTitle: intent.title,
            categoryId: 0,
            categoryName: 0,
            categoryTitle: category.title,
            serviceId: service.id,
            serviceName: service.id,
            serviceTitle: service.label,
            assigneeId: 0,
            assigneeName: randFullName(),
            senderId: 0,
            senderName: randFullName(),
            reason,
            instructions: 'Some Instructions'
        },
        client: {
            fullName: randFullName(),
            gender: gender.title,
            birthDate: birthDate.getTime(),
            diagnoses: diagnoses,
            location: community.title,
            locationPhone: randPhone('-'),
            address: address,
            insuranceNetwork: network,
        },
        requester: {
            fullName: authorFullName,
            organizationId: organization.id,
            organizationName: organization.title,
            phone: randPhone(),
            email: `${authorFullName.toLowerCase().replace(' ', '_')}@simplyconnect.me`
        }
    }
}