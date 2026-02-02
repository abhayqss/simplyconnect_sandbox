export const initialState ={
    error: null,
    isFetching:false,
    isTableFetching:false,
    shouldReload:false,
    data:[],
    totalCount:0,
    formData:[],
    saveStatus:false,
    companyTypes:[],
    categoryTypes:[],
    isDetailFetching:false,
    detailData:[],
    vendorPhotos:null,
    isPhotosFetching:false,
    vendorDetailCommunities:[],
    vendorDetailOrganizations:[],
    vendorDetailReferHistory:[],
    vendorDetailTeam:[],

    // associate
    allAssociateCommunities:[],
    allAssociateOrganizations:[],

    allAssociateCommunitiesTotal:0,
    allAssociateOrganizationsTotal:0,
    allAssociateReferHistory:[],

    isAssociateModalFetching:false,

    isVendorContactFetching:false,
    allVendorContact:[],


}