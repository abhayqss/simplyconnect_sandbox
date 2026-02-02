const { Record, List, hash, fromJS } = require('immutable')

const AffiliatedOrganization = Record({
    id: null,
    title: ''
})

const AffiliatedRelationshipRecord = () => {
    let hashCode = hash()

    return Record({
        primaryCommunities: List(),
        affiliatedOrganization: AffiliatedOrganization(),
        affiliatedCommunities: List(),

        areAllPrimaryCommunitiesSelected: false,
        areAllAffiliatedCommunitiesSelected: false,

        isChanged() {
            return this.hashCode() !== hashCode
        },
        updateHashCode() {
            hashCode = this.hashCode()

            return this
        },
    })
}

class AffiliatedRelationship extends AffiliatedRelationshipRecord() {
    constructor(data) {
        if (data) {
            data.affiliatedOrganization = AffiliatedOrganization(data.affiliatedOrganization)
        }

        super(fromJS(data))

        if (!data) {
            this.updateHashCode()
        }
    }
}

export default (data) => new AffiliatedRelationship(data)