import LoginRoutesConfig from "./LoginRoutesConfig";
import ESignRoutesConfig from "./ESignRoutesConfig";
import SignInRoutesConfig from "./SignInRoutesConfig";
import SelfSignUpConfig from "./SelfSignUpRoutesConfig";
import InvitationRoutesConfig from "./InvitationRoutesConfig";
import OldPasswordRoutesConfig from "./OldPasswordRoutesConfig";
import NewPasswordRoutesConfig from "./NewPasswordRoutesConfig";
import ResetPasswordRoutesConfig from "./ResetPasswordRoutesConfig";
import ExternalProviderRoutesConfig from "./ExternalProviderRoutesConfig";
import CareTeamInvitationRoutesConfig from "./CareTeamInvitationRoutesConfig";
import MobileEndRoutersConfig from "./MobileEndRoutersConfig";
import AssociatedRouterConfig from "./AssociatedRouterConfig";
import AssociatedRouterSuccessConfig from "./AssociatedRouterSuccessConfig";
import AssociateRegisterRouterConfig from "./AssociateRegisterRouterConfig";
import BuildingPublicRouterConfig from "./BuildingPublicRouterConfig";
import PublickBuildingDetailRouterConfig from "./PublickBuildingDetailRouterConfig";
import PublicMedicationReplyConfig from "./PublicMedicationReplyConfig";
import PublicMobileMedicationReplyConfig from "./PublicMobileMedicationReplyConfig";
import MedicationListConfig from "./MedicationListConfig";

export default [
  SelfSignUpConfig,
  LoginRoutesConfig,
  ESignRoutesConfig,
  SignInRoutesConfig,
  InvitationRoutesConfig,
  OldPasswordRoutesConfig,
  NewPasswordRoutesConfig,
  ResetPasswordRoutesConfig,
  CareTeamInvitationRoutesConfig,
  ...ExternalProviderRoutesConfig,
  MobileEndRoutersConfig,
  AssociatedRouterConfig,
  AssociatedRouterSuccessConfig,
  AssociateRegisterRouterConfig,
  BuildingPublicRouterConfig,
  PublickBuildingDetailRouterConfig,
  PublicMedicationReplyConfig,
  PublicMobileMedicationReplyConfig,
  MedicationListConfig,
];
