import { lazy } from "react";

const MedicationList = lazy(() => import("containers/Medication/MedicationList"));
export default {
  component: MedicationList,
  path: "/emailMedication",
  exact: true,
};
