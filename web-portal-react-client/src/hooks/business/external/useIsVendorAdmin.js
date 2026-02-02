import { SYSTEM_ROLES } from "lib/Constants";
import authUserStore from "lib/stores/AuthUserStore";

const { VENDOR_ADMIN } = SYSTEM_ROLES;

export default function useIsVendorAdmin() {
  const user = authUserStore.get();
  return user?.roleName === VENDOR_ADMIN;
}
