import React, { useCallback, useEffect, useState } from "react";

import { connect } from "react-redux";
import { bindActionCreators } from "redux";

import { useLocation, useParams } from "react-router-dom";

import DocumentTitle from "react-document-title";

import { Breadcrumbs, ErrorViewer } from "components";

import { useMutationWatch } from "hooks/common";

import { useExternalProviderRoleCheck } from "hooks/business/external";
import { useCanAddReferralRequestQuery } from "hooks/business/admin/referrals";

import { UpdateSideBarAction } from "actions/referrals";
import UpdateClientSideBarAction from "actions/clients/UpdateSideBarAction";

import listActions from "redux/referral/list/referralListActions";

import { REFERRAL_STATUSES, REFERRAL_TYPES } from "lib/Constants";

import { DateUtils as DU, isInteger } from "lib/utils/Utils";

import { first } from "lib/utils/ArrayUtils";

import ReferralPrimaryFilter from "../ReferralPrimaryFilter/ReferralPrimaryFilter";
import ReferralRequestEditor from "../ReferralRequestEditor/ReferralRequestEditor";

import "./Referrals.scss";
import InboundContent from "./InboundContent";
import OutboundContent from "./OutboundContent";

const { INBOUND, OUTBOUND } = REFERRAL_TYPES;

const { DONE, PENDING, PRE_ADMIT, ACCEPTED, DECLINED, CANCELED } = REFERRAL_STATUSES;

const REFERRAL_TYPE_TITLES = {
  [INBOUND]: "Inbound",
  [OUTBOUND]: "Outbound",
};

const STATUS_COLORS = {
  [PENDING]: "#e0e0e0",
  [ACCEPTED]: "#d5f3b8",
  [PRE_ADMIT]: "#ffedc2",
  [DECLINED]: "#fde1d5",
  [CANCELED]: "#fcccb8",
  [DONE]: "#d5f3b8",
};

const DATE_FORMAT = DU.formats.americanMediumDate;

function mapStateToProps(state) {
  const { list, request } = state.referral;

  return {
    state: list,
    user: state.auth.login.user.data,
    canAddRequest: request.can.add.value,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: bindActionCreators(listActions, dispatch),
  };
}

function Referrals({ state, actions, user, canAddRequest }) {
  const { error, isFetching, shouldReload, dataSource: ds } = state;

  let { clientId } = useParams();

  clientId = parseInt(clientId);

  const isClient = isInteger(clientId);

  const { organizationId, communityIds } = ds.filter;

  const isSingeCommunity = communityIds.length === 1;

  const communityId = isSingeCommunity && first(communityIds);

  const { pathname } = useLocation();

  const type = pathname.includes("inbound") ? INBOUND : OUTBOUND;

  const friendlyName = REFERRAL_TYPE_TITLES[type];

  const [isReferralRequestEditorOpen, setIsReferralRequestEditorOpen] = useState(false);

  const isExternalProvider = useExternalProviderRoleCheck();

  const onCloseReferralRequestEditor = useCallback(() => {
    setIsReferralRequestEditorOpen(false);
  }, []);

  // useCanAddReferralRequestQuery({ communityId: first(communityIds) });

  /*  useMutationWatch(type, () => {
    actions.clear();
  });
  useEffect(() => actions.clear, [actions]);*/

  return (
    <DocumentTitle title={`Simply Connect | ${friendlyName} referals${type === INBOUND ? " and inquiries" : ""}`}>
      <>
        {user && <>{isClient ? <UpdateClientSideBarAction params={{ clientId }} /> : <UpdateSideBarAction />}</>}
        <div className="Referrals">
          {!isExternalProvider && (
            <Breadcrumbs
              items={[
                {
                  title: "Referrals and inquiries",
                },
                {
                  title: friendlyName,
                  href: `/${type.toLowerCase()}-referrals`,
                  isActive: true,
                },
              ]}
              className="margin-bottom-10"
            />
          )}
          {!isClient && <ReferralPrimaryFilter className="margin-bottom-30" />}

          {type === INBOUND ? (
            <InboundContent
              friendlyName={friendlyName}
              type={type}
              isClient={isClient}
              STATUS_COLORS={STATUS_COLORS}
              DATE_FORMAT={DATE_FORMAT}
            />
          ) : (
            <OutboundContent
              friendlyName={friendlyName}
              type={type}
              isClient={isClient}
              STATUS_COLORS={STATUS_COLORS}
              DATE_FORMAT={DATE_FORMAT}
            />
          )}

          <ReferralRequestEditor
            isOpen={isReferralRequestEditorOpen}
            isOrganizationDisabled={false} // ???为什么赋值是true呢
            communityId={communityId}
            organizationId={organizationId}
            onClose={onCloseReferralRequestEditor}
            onSaveSuccess={useCallback(() => setShouldRefresh(true), [])}
          />

          {error && <ErrorViewer isOpen error={error} onClose={actions.clearError} />}
        </div>
      </>
    </DocumentTitle>
  );
}

export default connect(mapStateToProps, mapDispatchToProps)(Referrals);
