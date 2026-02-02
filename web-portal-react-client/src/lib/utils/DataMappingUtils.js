import {
    map,
    find
} from 'underscore'

function mapClientToContact(data) {
    if (!data) return

    const {
        id,
        phone,
        cellPhone,
        address,
        firstName,
        lastName,
        community,
        communityId,
        organization,
        organizationId
    } = data

    return {
        phone,
        address,
        lastName,
        firstName,
        communityId,
        organizationId,
        mobilePhone: cellPhone,
        communityName: community,
        associatedClientIds: [id],
        organizationName: organization
    }
}

function mapToTextValue(data, { textProps = ['label', 'title'], valueProps = ['id'] } = {}) {
    return map(data, o => ({
        text: find(o, (v, k) => textProps.includes(k)),
        value: find(o, (v, k) => valueProps.includes(k))
    }))
}

export const domain = {
    mapClientToContact
}

export const custom = {
    mapToTextValue
}