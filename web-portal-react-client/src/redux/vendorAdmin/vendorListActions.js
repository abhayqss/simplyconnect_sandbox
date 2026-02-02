import adminVendorService from "services/AdminVendorService";
import actionTypes from "./actionTypes";

export function getAdminVendorList(params) {
  return (dispatch) => {
    dispatch({
      type: actionTypes.LOAD_REQUEST,
    });
    return adminVendorService
      .findVendorList(params)
      .then((res) => {
        dispatch({ type: actionTypes.LOAD_SUCCESS, payload: res });
        return res;
      })
      .catch((e) => {
        dispatch({ type: actionTypes.LOAD_ERROR, payload: e });
      });
  };
}

// add or edit vendor
export function submit(params) {
  return (dispatch) => {
    dispatch({
      type: actionTypes.SAVE_REQUEST,
    });
    return adminVendorService
      .saveVendorForm(params)
      .then((res) => {
        dispatch({ type: actionTypes.SAVE_SUCCESS, payload: res });
        return res;
      })
      .catch((e) => {
        dispatch({ type: actionTypes.CHANGE_ERROR, payload: e });
      });
  };
}

// find vendor company type
export function findVendorCompanyType(params) {
  return (dispatch) => {
    dispatch({
      type: actionTypes.LOAD_COMPANY_TYPE_REQUEST,
    });
    return adminVendorService
      .findVendorCompanyType(params)
      .then((res) => {
        dispatch({ type: actionTypes.LOAD_COMPANY_TYPE_SUCCESS, payload: res });
        return res;
      })
      .catch((e) => {
        dispatch({ type: actionTypes.LOAD_COMPANY_TYPE_ERROR, payload: e });
      });
  };
}
// find vendor company type
export function findVendorCategoryType(params) {
  return (dispatch) => {
    dispatch({
      type: actionTypes.LOAD_CATEGORY_TYPE_REQUEST,
    });
    return adminVendorService
      .findVendorCategoryType(params)
      .then((res) => {
        dispatch({ type: actionTypes.LOAD_CATEGORY_TYPE_SUCCESS, payload: res });
        return res;
      })
      .catch((e) => {
        dispatch({ type: actionTypes.LOAD_CATEGORY_TYPE_ERROR, payload: e });
      });
  };
}
// find vendor detail type
export function findVendorDetail(params) {
  return (dispatch) => {
    dispatch({
      type: actionTypes.VENDOR_DETAIL_QUERY,
    });
    return adminVendorService
      .findById(params)
      .then((res) => {
        dispatch({ type: actionTypes.VENDOR_DETAIL_SUCCESS, payload: res });
        return res;
      })
      .catch((e) => {
        dispatch({ type: actionTypes.VENDOR_DETAIL_ERROR, payload: e });
      });
  };
}
export function findVendorAssociateCommunities(params) {
  return (dispatch) => {
    dispatch({
      type: actionTypes.VENDOR_ASSOCIATE_COMMUNITIES_QUERY,
    });
    return adminVendorService
      .viewVendorAssociateCommunities(params)
      .then((res) => {
        dispatch({ type: actionTypes.VENDOR_ASSOCIATE_COMMUNITIES_SUCCESS, payload: res });
        return res;
      })
      .catch((e) => {
        dispatch({ type: actionTypes.VENDOR_ASSOCIATE_COMMUNITIES_ERROR, payload: e });
      });
  };
}
export function findVendorAssociateOrganizations(params) {
  return (dispatch) => {
    dispatch({
      type: actionTypes.VENDOR_ASSOCIATE_ORGANIZATION_QUERY,
    });
    return adminVendorService
      .viewVendorAssociateOrganizations(params)
      .then((res) => {
        dispatch({ type: actionTypes.VENDOR_ASSOCIATE_ORGANIZATION_SUCCESS, payload: res });
        return res;
      })
      .catch((e) => {
        dispatch({ type: actionTypes.VENDOR_ASSOCIATE_ORGANIZATION_ERROR, payload: e });
      });
  };
}
export function findVendorContactData(params) {
  return (dispatch) => {
    dispatch({
      type: actionTypes.VENDOR_CONTACT_QUERY,
    });
    return adminVendorService
      .viewVendorContactData(params)
      .then((res) => {
        dispatch({ type: actionTypes.VENDOR_CONTACT_SUCCESS, payload: res });
        return res;
      })
      .catch((e) => {
        dispatch({ type: actionTypes.VENDOR_CONTACT_ERROR, payload: e });
      });
  };
}
