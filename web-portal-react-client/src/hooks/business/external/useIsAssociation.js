import { ONLY_VIEW_ROLES, SYSTEM_ROLES } from "lib/Constants";
import authUserStore from "lib/stores/AuthUserStore";

const { ASSOCIATION, NON_CLINICAL_STAFF } = SYSTEM_ROLES;

export default function useIsAssociation() {
  const user = authUserStore.get();
  return user?.roleName === ASSOCIATION || user?.roleName === NON_CLINICAL_STAFF;
}
