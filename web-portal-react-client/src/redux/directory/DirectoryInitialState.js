import Age from "./age/AgeInitialState";
import Lab from "./lab/LabInitialState";
import Note from "./note/NoteInitialState";
import Care from "./care/CareInitialState";
import Race from "./race/RaceInitialState";
import Event from "./event/EventInitialState";
import State from "./state/StateInitialState";
import Report from "./report/ReportInitialState";
import Gender from "./gender/GenderInitialState";
import Ethnicity from "./ethnicity/EthnicityInitialState";
import System from "./system/SystemInitialState";
import Client from "./client/ClientInitialState";
import Contact from "./contact/ContactInitialState";
import Marital from "./marital/MaritalInitialState";
import Service from "./service/ServiceInitialState";
import Incident from "./incident/IncidentInitialState";
import Referral from "./referral/ReferralInitialState";
import Language from "./language/LanguageInitialState";
import Emergency from "./emergency/EmergencyInitialState";
import Treatment from "./treatment/TreatmentInitialState";
import Insurance from "./insurance/InsuranceInitialState";
import Community from "./community/CommunityInitialState";
import Additional from "./additional/AdditionalInitialState";
import Assessment from "./assessment/AssessmentInitialState";
import ServicePlan from "./servicePlan/ServicePlanInitialState";
import Marketplace from "./marketplace/MarketplaceInitialState";
import Appointment from "./appointment/AppointmentInitialState";
import Organization from "./organization/OrganizationInitialState";
import PrimaryFocus from "./primaryFocus/PrimaryFocusInitialState";

const { Record } = require("immutable");

export default Record({
  age: Age(),
  lab: Lab(),
  note: Note(),
  care: Care(),
  race: Race(),
  event: Event(),
  state: State(),
  gender: Gender(),
  ethnicity: Ethnicity(),
  report: Report(),
  system: System(),
  client: Client(),
  contact: Contact(),
  marital: Marital(),
  service: Service(),
  incident: Incident(),
  referral: Referral(),
  language: Language(),
  emergency: Emergency(),
  treatment: Treatment(),
  insurance: Insurance(),
  community: Community(),
  additional: Additional(),
  assessment: Assessment(),
  marketplace: Marketplace(),
  appointment: Appointment(),
  servicePlan: ServicePlan(),
  organization: Organization(),
  primaryFocus: PrimaryFocus(),
});
