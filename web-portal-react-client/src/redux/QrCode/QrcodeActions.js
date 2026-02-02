import actionTypes from "./actionTypes";
import service from "services/OrganizationService";

import qrService from "services/QrCodeService";

export function featAssociationsList(organizationId) {
  return (dispatch) => {
    service.featQrCode(organizationId).then((res) => {
      dispatch({ type: actionTypes.LOAD_QRCODE_SUCCESS, payload: res });
    });
  };
}

export function featOrgDetail(organizationId) {
  return (dispatch) => {
    service.featOrgDetail(organizationId).then((res) => {
      dispatch({ type: actionTypes.LOAD_ORG_DETAIL, payload: res });
    });
  };
}

export function featCategories(organizationId) {
  return (dispatch) => {
    qrService.featCategories(organizationId).then((res) => {
      dispatch({ type: actionTypes.LOAD_CATEGORIES, payload: res });
    });
  };
}

export function featCommunities(organizationId) {
  return (dispatch) => {
    qrService.featCommunities(organizationId).then((res) => {
      dispatch({ type: actionTypes.LOAD_COMMUNITIES, payload: res });
    });
  };
}

export function associatedOrg(vendorId, referIds) {
  return (dispatch) => {
    qrService.associatedOrg(vendorId, referIds).then((res) => {
      dispatch({ type: actionTypes.ASSOCIATEDORG_SUCCESS, payload: res });
    });
  };
}

export function featBuildingQrCode(communityId) {
  return (dispatch) => {
    qrService.featBuildingQrCode(communityId).then((res) => {
      dispatch({ type: actionTypes.LOAD_BUILDING_QRCODE, payload: res });
    });
  };
}

export function featBuildingQrDetail(communityId) {
  return async (dispatch) => {
    try {
      const res = await qrService.featQrBuildingDetail(communityId);

      const fetchLogos = res.data.pictures.map(async (item) => {
        if (item.id) {
          const response = await service.getQrCodePic(communityId, item.id);
          item.logo = response.data;
        }
      });

      await Promise.all(fetchLogos);

      dispatch({ type: actionTypes.LOAD_QR_BUILDING_DETAIL, payload: res });
    } catch (e) {
      console.error("Error fetching building QR details", e);
      // 可以在这里处理错误
    }
  };
}

export function associatedBuilding(vendorId, referIds) {
  return (dispatch) => {
    qrService.associatedBuilding(vendorId, referIds).then((res) => {
      dispatch({ type: actionTypes.ASSOCIATEDORG_BUILDING_SUCCESS, payload: res });
    });
  };
}

export function downOrgQrCode(communityId) {
  return (dispatch) => {
    qrService.downOrgQrCode(communityId).then((res) => {
      dispatch({ type: actionTypes.DOWN_ORG_QRCODE, payload: res });
    });
  };
}

export function saveVendorFormData(data) {
  return (dispatch) => {
    dispatch({ type: actionTypes.VENDOR_FORMDATA, payload: data });
  };
}

export function qrCreate(data) {
  return (dispatch) => {
    qrService.qrVendorCreate(data).then((res) => {
      dispatch({ type: actionTypes.QR_CREATE, payload: res });
    });
  };
}
