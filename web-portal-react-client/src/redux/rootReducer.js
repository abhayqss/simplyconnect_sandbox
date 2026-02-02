import { combineReducers } from "redux";
import { connectRouter } from "connected-react-router";

import lab from "./lab/labReducer";
import auth from "./auth/authReducer";
import help from "./help/helpReducer";
import care from "./care/careReducer";
import note from "./note/noteReducer";
import audit from "./audit/auditReducer";
import error from "./error/errorReducer";
import event from "./event/eventReducer";
import login from "./login/loginReducer";
import report from "./report/reportReducer";
import notify from "./notify/notifyReducer";
import client from "./client/clientReducer";
import sidebar from "./sidebar/sideBarReducer";
import contact from "./contact/contactReducer";
import document from "./document/documentReducer";
import referral from "./referral/referralReducer";
import incident from "./incident/incidentReducer";
import prospect from "./prospect/prospectReducer";
import insurance from "./insurance/insuranceReducer";
import community from "./community/communityReducer";
import directory from "./directory/directoryReducer";
import dashboard from "./dashboard/dashboardReducer";
import groupNote from "./group-note/groupNoteReducer";
import videoChat from "./video-chat/videoChatReducer";
import marketplace from "./marketplace/marketplaceReducer";
import appointment from "./appointment/appointmentReducer";
import organization from "./organization/organizationReducer";
import conversations from "./conversations/conversationsReducer";
import transportation from "./transportation/transportationReducer";
import adminVendor from "./vendorAdmin/vendorReducer";

import building from "./marketplace/Building/BuildingReducer";
import vendor from "./marketplace/Vendor/VendorReducer";
import Associations from "./Associations/AssociationsReducer";
import Qrcode from "./QrCode/QrcodeReducer";
import Vendor from "./Vendor/vendorReducer";
import ReferData from "./ReferralData/tableDataReducer";

const rootReducer = (history) =>
  combineReducers({
    router: connectRouter(history),
    lab,
    auth,
    help,
    care,
    note,
    audit,
    error,
    event,
    login,
    report,
    notify,
    client,
    sidebar,
    contact,
    document,
    referral,
    incident,
    prospect,
    groupNote,
    videoChat,
    insurance,
    community,
    dashboard,
    directory,
    appointment,
    marketplace,
    organization,
    conversations,
    adminVendor,
    transportation,
    building,
    vendor,
    Associations,
    Qrcode,
    Vendor,
    ReferData,
  });

export default rootReducer;
