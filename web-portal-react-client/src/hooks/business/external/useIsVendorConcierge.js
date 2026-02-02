import { SYSTEM_ROLES } from "lib/Constants";
import authUserStore from "lib/stores/AuthUserStore";

const { VENDOR_CONCIERGE } = SYSTEM_ROLES;

export default function userIsVendorConcierge() {
  const user = authUserStore.get();
  return user?.roleName === VENDOR_CONCIERGE;
}
