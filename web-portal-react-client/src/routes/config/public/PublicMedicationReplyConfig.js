import { lazy } from "react";

const ReferralsReply = lazy(() => import("containers/Referrals/ReferralsReply/ReferralsReply"));
export default {
  component: ReferralsReply,
  path: "/medication/referrals/:clientId/:medicationId/:intake/:smsId",
  exact: true,
};
