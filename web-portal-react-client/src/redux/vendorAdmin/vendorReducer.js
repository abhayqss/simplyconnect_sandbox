import  actions from './actionTypes';
import {initialState} from "./vendorState";
import {findVendorContactData} from "./vendorListActions";
const  adminVendor =(state = initialState,action) =>{
    switch (action.type){
        case actions.LOAD_REQUEST:
            return {
                ...state,
                isTableFetching:true
            };
        case actions.LOAD_SUCCESS:
            return {
                ...state,
                isTableFetching: false,
                data:action.payload.data,
                totalCount:action.payload.totalCount
            }
        case actions.LOAD_ERROR:
            return {
                ...state,
                isTableFetching: false,
                error: action.payload
            }

        case actions.SAVE_REQUEST:
            return {
                ...state,
                isFetching:true
            };
        case actions.SAVE_SUCCESS:
            return {
                ...state,
                isFetching: false,
                saveStatus:true
            }
        case actions.SAVE_ERROR:
            return {
                ...state,
                isFetching: false,
                error: action.payload,
                aveStatus:false
            }

        case actions.LOAD_COMPANY_TYPE_REQUEST:
            return {
                ...state,
                isFetching: true,
            }
            case actions.LOAD_COMPANY_TYPE_ERROR:
            return {
                ...state,
                isFetching: false,
                error: action.payload,
                companyTypes:[]
            }
            case actions.LOAD_COMPANY_TYPE_SUCCESS:
            return {
                ...state,
                isFetching: true,
                companyTypes: action.payload.data,
            }
            case actions.LOAD_CATEGORY_TYPE_SUCCESS:
            return {
                ...state,
                isFetching: true,
                categoryTypes: action.payload.data,

            }
            case actions.LOAD_CATEGORY_TYPE_REQUEST:
            return {
                ...state,
                isFetching: true,
            }
            case actions.LOAD_CATEGORY_TYPE_ERROR:
            return {
                ...state,
                isFetching: true,
                categoryTypes:[],
                error:action.payload,
            }

        case actions.VENDOR_DETAIL_QUERY:
            return {
                ...state,
                isDetailFetching: true,
            }
        case actions.VENDOR_DETAIL_SUCCESS:
            return {
                ...state,
                isDetailFetching: false,
                detailData: action.payload.data
            }
        case actions.VENDOR_DETAIL_ERROR:
            return {
                ...state,
                isDetailFetching: false,
                detailData: [],
                error: action.payload
            }

            case actions.VENDOR_ASSOCIATE_COMMUNITIES_QUERY:
            return {
                ...state,
                isAssociateModalFetching: true
            }
            case actions.VENDOR_ASSOCIATE_COMMUNITIES_SUCCESS:
            return {
                ...state,
                isAssociateModalFetching :false,
                allAssociateCommunities: action.payload.data,
                allAssociateCommunitiesTotal: action.payload.totalCount,

            }
            case actions.VENDOR_ASSOCIATE_COMMUNITIES_ERROR:
            return {
                ...state,
                isAssociateModalFetching:false,
                allAssociateCommunities: [],
                allAssociateCommunitiesTotal:0,
                error:action.payload
            }
         case actions.VENDOR_CONTACT_QUERY:
            return {
                ...state,
                isVendorContactFetching:true,
                allVendorContact: [],
                error:action.payload
            }
            case actions.VENDOR_CONTACT_ERROR:
            return {
                ...state,
                isVendorContactFetching:false,
                allVendorContact: [],
                error:action.payload
            }
            case actions.VENDOR_CONTACT_SUCCESS:
            return {
                ...state,
                isVendorContactFetching:false,
                allVendorContact: action.payload.data,
            }


            case actions.VENDOR_ASSOCIATE_ORGANIZATION_QUERY:
            return {
                ...state,
                isAssociateModalFetching:true,
                allAssociateOrganizations: [],
                allAssociateOrganizationsTotal: 0,

            }
            case actions.VENDOR_ASSOCIATE_ORGANIZATION_SUCCESS:
            return {
                ...state,
                isAssociateModalFetching:false,
                allAssociateOrganizations: action.payload.data,
                allAssociateOrganizationsTotal:action.payload.totalCount
            }
            case actions.VENDOR_ASSOCIATE_ORGANIZATION_ERROR:
            return {
                ...state,
                isAssociateModalFetching:false,
                allAssociateOrganizations: [],
                allAssociateOrganizationsTotal: 0,
                error: action.payload
            }

        default:
        return state
    }
}
export default adminVendor