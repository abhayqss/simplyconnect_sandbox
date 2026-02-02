import initialState from "./QrcodeState";
import actions from "./actionTypes";


const QrcodeReducer = (state = initialState, action) => {
  switch (action.type) {
    case actions.LOAD_QRCODE_SUCCESS:
      return {
        ...state,
        qrCode: action.payload.data,
      };

    case actions.LOAD_ORG_DETAIL:
      return {
        ...state,
        orgDtail: action.payload.data,
      };

    case actions.LOAD_CATEGORIES:

      return {
        ...state,
        categories: action.payload.data,
      };

    case actions.LOAD_COMMUNITIES:

      return {
        ...state,
        communities: action.payload.data,
      };

    case actions.ASSOCIATEDORG_SUCCESS:
      return {
        ...state,
        associatedOrgSuccess: action.payload.success,
      };

    case actions.LOAD_BUILDING_QRCODE:
      return {
        ...state,
        buildingQrCode: action.payload.data,
      };

    case actions.LOAD_QR_BUILDING_DETAIL:
      return {
        ...state,
        buildingQrDetail: action.payload.data,
      };


    case actions.ASSOCIATEDORG_BUILDING_SUCCESS:
      return {
        ...state,
        associatedBuildingSuccess: action.payload.success,
      };

    case actions.VENDOR_FORMDATA:
      return {
        ...state,
        vendorFormData: action.payload,
      };

    case actions.QR_CREATE:
      return {
        ...state,
        qrCreadeSuccess: action.payload.success,
      };




    default:
      return state;

  }
}

export default QrcodeReducer;
