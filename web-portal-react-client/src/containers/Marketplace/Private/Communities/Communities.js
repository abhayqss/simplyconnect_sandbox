import React, { memo, useCallback, useEffect, useMemo, useState } from "react";

import { compose, map, noop } from "underscore";

import cn from "classnames";

import DocumentTitle from "react-document-title";

import { useHistory, useParams } from "react-router-dom";

import { useGeolocated } from "react-geolocated";

import { useDebouncedCallback } from "use-debounce";

import { Button, Col, Collapse, Row } from "reactstrap";

import { Breadcrumbs, ErrorViewer, FlatList, Loader, SearchField } from "components";

import { SelectField } from "components/Form";

import { Dialog } from "components/dialogs";

import { ServicePrimaryInfo } from "components/business/Marketplace";

import { withMarketplaceContext, withTooltip } from "hocs";

import { useDeferred, useEventEmitter, useListState, useLocationState } from "hooks/common";

import { useAuthUser } from "hooks/common/redux";

import { useServicesQuery } from "hooks/business/directory/query";

import { useContactLocationQuery } from "hooks/business/admin/contact";

import {
  useCommunitiesQuery,
  useCommunityFilter,
  useCommunityFilterDefaultDataCache,
  useInNetworkCommunityExistsQuery,
} from "hooks/business/Marketplace";

import CommunityFilterEntity from "entities/CommunityFilter";
import ReferralRequestEditor from "containers/Referrals/ReferralRequestEditor/ReferralRequestEditor";

import { SYSTEM_ROLES } from "lib/Constants";

import { hyphenate, isInteger, toNumberExcept } from "lib/utils/Utils";

import { path } from "lib/utils/ContextUtils";

import { ReactComponent as Filter } from "images/filters.svg";

import { CommunityFilter, CommunityMap, CommunitySummary, FeaturedCommunities } from "./index";

import { SavedCommunitiesPopup } from "../SavedCommunities";

import "./Communities.scss";

const { PERSON_RECEIVING_SERVICES } = SYSTEM_ROLES;

const SearchButton = withTooltip({
  text: "The search box must contain at least two letters.",
})(Button);

function renderServicePrimaryInfo({ text: title, data, highlightedText }) {
  return <ServicePrimaryInfo title={title} categoryTitle={data.categoryTitle} highlightedText={highlightedText} />;
}

function Communities({
  clientId,
  domainId,
  className,
  onChoose,
  onSelect = noop,
  programSubTypeId,
  shouldCancelClient: needToCancelClient,
}) {
  const [selected, setSelected] = useState(null);
  const [searchText, setSearchText] = useState("");
  const [isFilterOpen, toggleFilter] = useState(true);
  const [isFilterIncluded, setFilterIncluded] = useState(true);

  const [fetchCount, setFetchCount] = useState(0);

  const [serviceId, setServiceId] = useState([]);
  const [serviceSearchText, setServiceSearchText] = useState("");

  const [locationState, clearLocationState] = useLocationState();
  const [shouldCancelClient, setShouldCancelClient] = useState(needToCancelClient);

  const [isReferralRequestEditorOpen, setReferralRequestEditorOpen] = useState(false);
  const [isIntroductionDialogOpen, toggleIntroductionDialog] = useState(locationState?.isIntroductionNeed);

  const user = useAuthUser();
  const history = useHistory();

  const isClient = isInteger(clientId);

  let { communityName, ...params } = useParams();

  const communityId = toNumberExcept(params.communityId, [null, undefined]);

  const emitter = useEventEmitter();

  const { coords, isGeolocationEnabled, isGeolocationAvailable } = useGeolocated({
    userDecisionTimeout: 5000,
  });

  const { data: defaultCoordinate = {}, isFetching: isDefaultCoordinateFetching } = useContactLocationQuery(
    { contactId: user?.id },
    {
      staleTime: 0,
      enabled: Boolean(user),
      onSuccess: () => refetchDeferred(),
    },
  );

  const { isFetching: isInNetworkCommunityExistsFetching } = useInNetworkCommunityExistsQuery(
    {},
    {
      onSuccess: () => refetchDeferred(),
    },
  );

  const coordinate = useMemo(() => {
    const coordinate = defaultCoordinate ?? {};

    if (isGeolocationEnabled && isGeolocationAvailable && coords) {
      coordinate.latitude = coords.latitude;
      coordinate.longitude = coords.longitude;
    }

    return coordinate;
  }, [coords, defaultCoordinate, isGeolocationEnabled, isGeolocationAvailable]);

  const { state, setError, clearError, changeFilter } = useListState({ filterEntity: CommunityFilterEntity });

  const { error } = state;

  const { data: services, remove: removeServices } = useServicesQuery(
    { text: serviceSearchText },
    {
      staleTime: 0,
      enabled: serviceSearchText?.length > 2,
    },
  );

  const filter = useMemo(
    () => ({
      ...state.filter.toJS(),
      ...(isInteger(communityId) && {
        includeMyCommunities: false,
        includeInNetworkCommunities: false,
      }),
    }),
    [state.filter, communityId],
  );

  const {
    fetch,
    refetch,
    fetchMore,
    isFetching,
    pagination,
    isFetchingMore,
    aggregatedData: data,
  } = useCommunitiesQuery(
    {
      searchText,
      communityId,
      ...coordinate,
      ...(isFilterIncluded ? filter : { serviceIds: serviceId }),
    },
    {
      staleTime: 0,
      onError: setError,
      onMutate: () => {
        emitter.fire("Marketplace.Communities:fetch");
      },
    },
  );

  const refetchDeferred = useDeferred(refetch);

  const { get: getFilterDefaultData } = useCommunityFilterDefaultDataCache();

  const customFilter = useCommunityFilter(`${communityId ? "IN_NETWORK_" : ""}COMMUNITY_FILTER`, {
    canReApply: true,
    getDefaultData: getFilterDefaultData,
    onChange: (changes, isChanged) => {
      changeFilter(changes);
      if (isChanged) setFilterIncluded(true);
    },
    onApply: () => {
      refetch();

      if (isFilterIncluded) {
        setServiceSearchText("");
        setServiceId([]);
        removeServices();
      }
    },
    onReset: (isSaved) => {
      isSaved && isFilterIncluded && refetch();
    },
  });

  const mappedServices = useMemo(
    () =>
      map(services, (o) => ({
        type: "tick",
        value: o.id,
        text: o.label ?? o.title,
        data: { categoryTitle: o.serviceCategoryTitle },
      })),
    [services],
  );

  const referralRequestSuccessDialog = useMemo(
    () => ({
      ...(![PERSON_RECEIVING_SERVICES].includes(user?.roleName) && {
        text: `TThe request will be displayed in the "Outbound" section located under the "Referrals and Inquires" tab. You can see the details and manage status of the referral request there.`,
      }),
      ...(isClient && {
        buttons: {
          submit: {
            text: "Copy to Service plan",
            onClick: () => onChoose(selected),
          },
        },
      }),
    }),
    [user, isClient, onChoose, selected],
  );

  const onToggleFilter = useCallback(() => {
    toggleFilter((v) => !v);
  }, []);

  const clearSearch = useCallback(() => {
    setSearchText("");
    refetchDeferred();
  }, [refetchDeferred]);

  const changeSearchField = useCallback((name, value) => {
    setSearchText(value);
  }, []);

  const clearServiceSearchText = useCallback(() => {
    removeServices();
    setServiceSearchText("");
    refetchDeferred();
  }, [removeServices, refetchDeferred]);

  const changeServiceSearchText = useCallback(
    (name, value) => {
      setServiceSearchText(value);
      if (serviceSearchText?.length < 3) removeServices();
    },
    [removeServices, serviceSearchText],
  );

  const onChangeServices = useCallback(
    (name, value) => {
      setServiceId(value);
      setFilterIncluded(false);
      customFilter.reset();
      refetchDeferred();
    },
    [customFilter, refetchDeferred],
  );

  const onClearServices = useCallback(() => {
    setServiceId(null);
    setFilterIncluded(true);
    refetchDeferred();
  }, [refetchDeferred]);

  function onSearch() {
    if (searchText.length > 1) refetch();
  }

  const onView = useCallback(
    (data) => {
      if (isClient) {
        onSelect(data, shouldCancelClient);
      } else {
        history.push(
          path(
            `marketplace/communities/${communityName || hyphenate(data.communityName)}--@id=${communityId || data.communityId}`,
            communityId ? `/in-network-partners/${hyphenate(data.communityName)}--@id=${data.communityId}` : "",
          ),
          { serviceIds: [] }, //todo fix it
        );
      }
    },
    [history, isClient, onSelect, communityId, communityName, shouldCancelClient],
  );

  const debouncedFetchMore = useDebouncedCallback(fetchMore, 100);

  function navigateSavedCommunities() {
    history.push(path("/saved-for-later"));
  }

  function onCancelClient() {
    setShouldCancelClient(true);
  }

  function onCreateReferral(data) {
    setSelected(data);
    setReferralRequestEditorOpen(true);
  }

  function onCloseReferralRequestEditor() {
    setSelected(null);
    setReferralRequestEditorOpen(false);
  }

  useEffect(() => {
    fetch();
  }, [fetch]);

  return (
    <DocumentTitle
      title={`Simply Connect | Marketplace${communityId ? ` | ${communityName} | In-Network Partners` : ""}`}
    >
      <div className={cn("MarketplaceCommunities", className)}>
        {communityId && (
          <Breadcrumbs
            className="margin-top-15 margin-left-35 margin-bottom-15"
            items={[
              {
                title: "Marketplace",
                href: "/marketplace",
                isEnabled: true,
              },
              {
                title: "Community Details",
                href: `/marketplace/communities/${communityName}--@id=${communityId}`,
              },
              {
                title: "In-Network Partners",
                href: `/marketplace/communities/${communityName}--@id=${communityId}/in-network-partners`,
                isActive: true,
              },
            ]}
          />
        )}

        <FeaturedCommunities onReferral={onCreateReferral} />

        <div className="CommunityFilter-Header">
          <div className="CommunityFilter-Title">Filter</div>
          <div className="flex-1 d-flex flex-row">
            <SelectField
              hasSearchBox
              optionType="none"
              name="serviceSearchText"
              value={serviceId}
              options={mappedServices}
              placeholder="Search by service"
              className="CommunityFilter-SearchField flex-1 margin-right-24"
              onClear={onClearServices}
              onChange={onChangeServices}
              onClearSearchText={clearServiceSearchText}
              onChangeSearchText={changeServiceSearchText}
              formatOptionText={renderServicePrimaryInfo}
            />
            <Filter
              className={cn(
                "CommunityFilter-Icon",
                isFilterOpen ? "CommunityFilter-Icon_rotated_90" : "CommunityFilter-Icon_rotated_0",
              )}
              onClick={onToggleFilter}
            />
          </div>
        </div>

        <Collapse isOpen={isFilterOpen}>
          <CommunityFilter {...customFilter} areInNetworkCommunities={isInteger(communityId)} />
        </Collapse>

        <div className="MarketplaceCommunities-Title">Solutions</div>

        <Row className="h-100">
          <Col md={4} className="h-100">
            <div className="MarketplaceCommunityList">
              <div className="MarketplaceCommunityList-Search">
                <SearchField
                  type="text"
                  name="searchText"
                  value={searchText}
                  hasSearchIcon={false}
                  placeholder="Search by name, address, or category"
                  className="MarketplaceCommunityList-SearchField"
                  onClear={clearSearch}
                  onChange={changeSearchField}
                  onEnterKeyDown={onSearch}
                />

                <SearchButton
                  color="success"
                  disabled={searchText.length < 2}
                  isTooltipEnabled={searchText.length < 2}
                  className="MarketplaceCommunityList-SearchBtn"
                  onClick={onSearch}
                >
                  Search
                </SearchButton>
              </div>

              {!isFetching && !data.length && (
                <div className="font-size-15 text-center">
                  No communities found. Please change the filtering criteria.
                </div>
              )}

              {!!data.length && (
                <FlatList
                  list={data}
                  itemKey="communityId"
                  className={cn("MarketplaceCommunityList-List", {
                    "MarketplaceCommunityList-List_fetching": isFetchingMore,
                  })}
                  itemClassName="MarketplaceCommunityList-ListItem"
                  loadMore={debouncedFetchMore}
                  shouldLoadMore={!isFetchingMore && data.length < pagination.totalCount}
                  onEndReachedThreshold={100}
                >
                  {(community) => {
                    return (
                      <CommunitySummary
                        key={community.id}
                        data={community}
                        className="margin-bottom-24"
                        highlightedText={searchText}
                        onMoreInfo={onView}
                      />
                    );
                  }}
                </FlatList>
              )}
              {(isFetching || isDefaultCoordinateFetching || isInNetworkCommunityExistsFetching) && !isFetchingMore && (
                <Loader className="MarketplaceCommunityList-Loader" />
              )}

              {isFetchingMore && <Loader className="MarketplaceCommunityList-Loader_paginated" />}
            </div>
          </Col>

          <Col md={8} className="h-100">
            <CommunityMap
              clientId={clientId}
              onViewCommunity={onView}
              filter={{
                searchText,
                ...coordinate,
                ...(isFilterIncluded ? filter : { serviceIds: serviceId }),
              }}
              defaultRegion={{
                lat: coordinate?.latitude,
                lng: coordinate?.longitude,
              }}
              programSubTypeId={programSubTypeId}
              shouldCancelClient={shouldCancelClient}
            />
          </Col>
        </Row>

        <ReferralRequestEditor
          marketplace={selected}
          isOpen={isReferralRequestEditorOpen}
          communityId={selected?.communityId}
          organizationId={selected?.organizationId}
          isFeaturedCommunity={selected?.isFeatured}
          onClose={onCloseReferralRequestEditor}
          successDialog={referralRequestSuccessDialog}
        />

        {isIntroductionDialogOpen && (
          <Dialog
            isOpen
            buttons={[
              {
                text: "Close",
                onClick: () => {
                  clearLocationState();
                  toggleIntroductionDialog(false);
                },
              },
            ]}
          >
            A searchable marketplace for paperless referrals, keeping your brand top of mind for Clients and families.
          </Dialog>
        )}

        <SavedCommunitiesPopup
          total={5}
          onCreateReferral={onCreateReferral}
          onExtendedMode={navigateSavedCommunities}
          className="position-absolute-bottom-right"
        />

        {error && <ErrorViewer isOpen error={error} onClose={clearError} />}
      </div>
    </DocumentTitle>
  );
}

export default compose(memo, withMarketplaceContext)(Communities);
