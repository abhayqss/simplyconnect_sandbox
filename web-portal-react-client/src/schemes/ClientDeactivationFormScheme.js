import { integer, Shape, string } from "./types";

const ClientDeactivationFormScheme = Shape({
  exitDate: integer().nullable().required(),
  deactivationReason: string().nullable(),
  comment: string(),
});

export default ClientDeactivationFormScheme;
