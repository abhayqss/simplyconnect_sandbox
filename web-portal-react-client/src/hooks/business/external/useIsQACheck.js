import { SYSTEM_ROLES } from "lib/Constants";
import authUserStore from "lib/stores/AuthUserStore";

const { QUALITY_ASSURANCE } = SYSTEM_ROLES;

export default function useIsQACheck() {
  const user = authUserStore.get();
  return user?.roleName === QUALITY_ASSURANCE;
}
