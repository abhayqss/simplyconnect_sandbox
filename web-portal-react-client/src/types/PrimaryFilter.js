import {
    bool,
    func,
    string,
    object,
    arrayOf
} from 'prop-types'

export default {
    organizations: arrayOf(object),
    communities: arrayOf(object),

    data: object,

    onChangeField: func,
    onChangeOrganizationField: func,
    onChangeCommunityField: func,

    className: string,

    hasCommunityField: bool,
    isCommunityMultiSelection: bool,

    getCommunityFieldOption: func,
    getOrganizationFieldOption: func
}