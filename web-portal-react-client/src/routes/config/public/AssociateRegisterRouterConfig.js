import { lazy } from "react";

const AssociateRegister = lazy(() => import("containers/AfterCodeScanning/AssociateRegister/AssociateRegister"));

export default {
  component: AssociateRegister,
  path: "/associate/:id/:type/:name/register",
  exact: true,
};
