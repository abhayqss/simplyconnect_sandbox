import React, { memo, useCallback, useState } from "react";

import DocumentTitle from "react-document-title";

import { useHistory } from "react-router-dom";

import { Badge } from "reactstrap";

import { Loader } from "components";

import { useDeferred, useEventEmitter } from "hooks/common";

import { useCommunityRemoving, useSavedCommunitiesQuery } from "hooks/business/Marketplace";

import { map } from "lib/utils/ArrayUtils";
import { path } from "lib/utils/ContextUtils";

import { ReactComponent as Minimize } from "images/minimize-2.svg";

import Breadcrumbs from "./Breadcrumbs/Breadcrumbs";
import CommunityCard from "./CommunityCard/CommunityCard";
import ReferralRequestEditor from "../../../Referrals/ReferralRequestEditor/ReferralRequestEditor";

import "./SavedCommunities.scss";

const REFERRAL_REQUEST_SUCCESS_TEXT =
  'The request will be displayed in the "Outbound" section located under the "Referrals and Inquires" tab. You can see the details and manage status of the referral request there.';

function SavedCommunities() {
  const [selected, setSelected] = useState(null);
  const [isReferralRequestEditorOpen, toggleReferralRequestEditor] = useState(false);

  const history = useHistory();
  const emitter = useEventEmitter();

  const { data, refetch, isFetching } = useSavedCommunitiesQuery(
    {},
    {
      staleTime: 0,
    },
  );

  const { mutateAsync: remove, isLoading: isRemoving } = useCommunityRemoving(
    {},
    {
      onSuccess: () => {
        refetch();
        setSelected(null);
        emitter.fire("Marketplace.Community:deleted", { communityId: selected.communityId });
      },
    },
  );

  const removeDeferred = useDeferred(remove);

  function navigateToMarketplace() {
    history.push(path("/marketplace"));
  }

  const onRemove = useCallback(
    (o) => {
      setSelected(o);
      removeDeferred(o);
    },
    [removeDeferred],
  );

  const onCreateReferral = useCallback((community) => {
    setSelected(community);
    toggleReferralRequestEditor(true);
  }, []);

  const onCloseReferralRequestEditor = useCallback(() => {
    setSelected(null);
    toggleReferralRequestEditor(false);
  }, []);

  return (
    <DocumentTitle title="Simply Connect | Saved for Later">
      <div className="SavedCommunities">
        <div className="SavedCommunities-Navigation">
          <Breadcrumbs />

          <Minimize onClick={navigateToMarketplace} className="SavedCommunities-MinimizeBtn" />
        </div>

        <div className="SavedCommunities-Header">
          <div className="SavedCommunities-Title">
            <span className="SavedCommunities-TitleText">Saved for later</span>

            <Badge color="info" className="Badge Badge_place_top-right">
              {data?.length}
            </Badge>
          </div>

          <div className="SavedCommunities-ControlPanel"></div>
        </div>

        <div className="SavedCommunities-Body">
          {(isFetching || isRemoving) && <Loader hasBackdrop />}

          <div className="SavedCommunities-Grid">
            {map(data, (community) => (
              <CommunityCard
                data={community}
                onRemove={onRemove}
                className="SavedCommunities-CommunityCard"
                onCreateReferral={onCreateReferral}
              />
            ))}
          </div>
        </div>
        <ReferralRequestEditor
          marketplace={selected}
          isOpen={isReferralRequestEditorOpen}
          communityId={selected?.communityId}
          onClose={onCloseReferralRequestEditor}
          successDialog={{ text: REFERRAL_REQUEST_SUCCESS_TEXT }}
        />
      </div>
    </DocumentTitle>
  );
}

export default memo(SavedCommunities);
