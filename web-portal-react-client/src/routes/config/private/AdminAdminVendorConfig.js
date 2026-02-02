import { lazy } from "react";

import { VENDOR_ADMIN } from "../Roles";

const Admin = lazy(() => import("containers/Admin/Admin"));
const Vendors = lazy(() => import("containers/Admin/Vendors/Vendors"));
const VendorDetails = lazy(() => import("containers/Admin/Vendors/VendorDetails/VendorDetail"));
/*   admin role event */
export default {
  component: Admin,
  path: "/admin",
  permission: [VENDOR_ADMIN],
  children: [
    {
      component: Vendors,
      path: "/vendors",
      permission: [VENDOR_ADMIN],
      exact: true,
    },
    {
      component: VendorDetails,
      path: "/vendors/:vendorId/:canEdit",
      permission: [VENDOR_ADMIN],
      exact: true,
    },
  ],
};
