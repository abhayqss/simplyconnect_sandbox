import { lazy } from "react";

const ReferralsReply = lazy(() => import("containers/Referrals/ReferralsReply/MobileReferralsReply"));
export default {
  component: ReferralsReply,
  path: "/mobile/medication/referrals/:clientId/:medicationId/:smsId",
  exact: true,
};
