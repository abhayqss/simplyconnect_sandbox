import BaseService from "./BaseService";
import { isNumber } from "underscore";

export class AssociationsService extends BaseService {
  canAddAssociation(params) {
    return super.request({
      url: `/association/canAdd`,
      params,
    });
  }

  AssociationsList(params) {
    return super.request({
      url: `/association/find`,
      params,
    });
  }

  AddAssociation(params) {
    return super.request({
      method: params.id ? "PUT" : "POST",
      url: `/association`,
      body: params,
      type: "multipart/form-data",
    });
  }

  FeatAssociationDetail(id) {
    return super.request({
      url: `/association?id=${id}`,
    });
  }

  FindAssociationsCommunity(params) {
    return super.request({
      url: `/association/findCommunity`,
      params,
    });
  }
  FindAssociationsOrganization(params) {
    return super.request({
      url: `/association/findOrganization`,
      params,
    });
  }
  FindAssociationsVendor(params) {
    return super.request({
      url: `/association/findVendor`,
      params,
    });
  }

  // 等待修改
  marketPlaceFindAssociationsVendor(params) {
    return super.request({
      url: `/communitybasic/findAssociationVendor`,
      params,
    });
  }

  FindAssociationsContact(params) {
    return super.request({
      url: `/association/findContact`,
      params,
    });
  }

  saveAssociationContactData(body) {
    return super.request({
      url: `/association/createContact`,
      body,
      method: "POST",
      type: "multipart/form-data",
    });
  }

  editAssociationTeamContactData(body) {
    return super.request({
      url: `/association/editContact`,
      body,
      method: "PUT",
      type: "multipart/form-data",
    });
  }

  getAssociationContactData(contactId) {
    return super.request({
      url: `/association/contact/${contactId} `,
    });
  }

  //  association/contact/{contactId}

  viewAssociationsVendorList(params) {
    return super.request({
      url: "/vendor/find",
      params,
    });
  }

  AddAssociationsCommunity(body) {
    return super.request({
      url: `/association/associateCommunities`,
      body,
      method: "POST",
    });
  }
  DisAddAssociationsCommunity(body) {
    return super.request({
      url: "/association/disAssociateCommunities",
      body,
      method: "POST",
    });
  }
  AddAssociationsVendor(body) {
    return super.request({
      url: `/association/associateVendor`,
      body,
      method: "POST",
    });
  }
  DisAddAssociationsVendor(body) {
    return super.request({
      url: "/association/disAssociateVendor",
      body,
      method: "POST",
    });
  }
  AddAssociationsOrg(body) {
    return super.request({
      url: `/association/associateOrganization`,
      body,
      method: "POST",
    });
  }
  DisAddAssociationsOrg(body) {
    return super.request({
      url: "/association/disAssociateOrganization",
      body,
      method: "POST",
    });
  }

  changePubicCurrentStatus(body) {
    return super.request({
      url: "/association/controlExternalShow",
      method: "POST",
      body,
    });
  }
}

const adminAssociationsService = new AssociationsService();
export default adminAssociationsService;
