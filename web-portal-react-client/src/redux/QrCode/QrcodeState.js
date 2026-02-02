import { associatedOrg } from "./QrcodeActions";

const initialState = {
  qrCode: '',
  orgDtail: {},
  categories: [],
  communities: [],
  associatedOrgSuccess: false,
  buildingQrCode:'',
  buildingQrDetail: {},
  associatedBuildingSuccess: false,
  vendorFormData: null,
  qrCreadeSuccess: false,
}

export default initialState;
