import { lazy } from "react";

const AssociateLogin = lazy(() => import("containers/AfterCodeScanning/associateLogin/AssociateLogin"));
export default {
  component: AssociateLogin,
  path: "/associate/:id/:type/:name/login",
  exact: true,
};
