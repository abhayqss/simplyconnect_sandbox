const { Record, Set } = require('immutable')

const Types = Record({
    client: false,
    clientCareTeam: false,
    communityCareTeam: false,
    other: false
})

const Client = Record({
    id: null,
    organizationId: null,
    communityId: null,
    careTeamMemberIds: Set(),
    associatedContactId: null
})

const CareTeamMembers = Record({
    organizationIds: Set(),
    communityIds: Set(),
    ids: Set(),
})

const Contacts = Record({
    organizationIds: Set(),
    communityIds: Set(),
    ids: Set()
})

const AddToGroupConversation = Record({
    groupName: '',
    types: Types(),
    client: Client(),
    contacts: Contacts(),
    careTeamMembers: CareTeamMembers()
})

export default AddToGroupConversation
