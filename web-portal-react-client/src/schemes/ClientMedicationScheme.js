import { Shape, string } from "./types";

const ClientMedicationScheme = Shape(
  {
    name: string().nullable().required(),
    ndc: string().nullable().required(),
    status: string().nullable().required(),
    frequency: string().nullable(),
    directions: string().nullable(),
    startedDate: string().nullable(),
    stoppedDate: string().nullable(),
    dosageQuantity: string().nullable().required(),
    indicatedFor: string().nullable().required(),
    comment: string().nullable(),
  },
  [["medicaidNumber", "medicareNumber", "hasNoMedicareNumber", "hasNoMedicaidNumber"]],
);

export default ClientMedicationScheme;
