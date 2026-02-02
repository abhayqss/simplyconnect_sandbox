import React, { memo, useCallback, useEffect, useMemo, useRef, useState } from "react";

import cn from "classnames";
import PTypes from "prop-types";

import { chain, compact, filter, map, noop } from "underscore";

import { useHistory, useParams } from "react-router-dom";

import ShowMore from "react-show-more";
import DocumentTitle from "react-document-title";

import { Col, Row } from "reactstrap";

import { Breadcrumbs, Carousel, CollapsibleSection, DataLoadable, ErrorViewer, Footer, Map, Picture } from "components";

import { Button, IconButton } from "components/buttons";

import { useEventEmitter, useLocationState, useQueryInvalidation } from "hooks/common";

import { useCommunityQuery, useCommunityRemoving, useCommunitySaving } from "hooks/business/Marketplace";

import {
  useMarketplaceLanguagesQuery,
  useServiceCategoriesQuery,
  useServicesQuery,
} from "hooks/business/directory/query";

import ReferralRequestEditor from "containers/Referrals/ReferralRequestEditor/ReferralRequestEditor";

import { ReactComponent as Phone } from "images/phone.svg";
import { ReactComponent as Heart } from "images/like.svg";
import { ReactComponent as Heart2 } from "images/heart.svg";
import { ReactComponent as Bed } from "images/bed-sleep.svg";
import { ReactComponent as Location } from "images/location.svg";
import { ReactComponent as Website } from "images/website-24.svg";
import { ReactComponent as Checkmark } from "images/check-mark-4.svg";
import { ReactComponent as BedPulse } from "images/bed-sleep-pulse.svg";
import { ReactComponent as ChevronTop } from "images/chevron-top.svg";
import { ReactComponent as ChevronBottom } from "images/chevron-bottom.svg";

import { DIMENSIONS } from "lib/Constants";

import {
  getStandardFormattedAddress,
  hyphenatedToTitle,
  isInteger,
  isNotEmpty,
  PhoneNumberUtils,
  toNumberExcept,
} from "lib/utils/Utils";

import { isString } from "lib/utils/StringUtils";

import { getAbsoluteUrl } from "lib/utils/UrlUtils";

import { path } from "lib/utils/ContextUtils";

import { SavedCommunitiesPopup } from "../../SavedCommunities";

import "./CommunityDetails.scss";

const THRESHOLD = 768;
const TITLE_HEIGHT = 70;
const BREAD_CRUMBS_HEIGHT = 65;

const { FOOTER_HEIGHT, NAVIGATION_BAR_HEIGHT } = DIMENSIONS;

const { formatPhoneNumber } = PhoneNumberUtils;

function MarketplaceCommunityDetails({ clientId, onChoose, programSubTypeId, shouldCancelClient, ...props }) {
  const pictureCarouselRef = useRef();

  const [error, setError] = useState(null);
  const [isSaved, setSaved] = useState(false);
  const [isReferralRequestEditorOpen, toggleReferralRequestEditorOpen] = useState(false);

  const params = useParams();
  const history = useHistory();
  const emitter = useEventEmitter();
  const invalidateQuery = useQueryInvalidation();

  const [{ serviceIds } = {}, clearLocationState] = useLocationState({ isCached: false });

  const partnerName = params.partnerName ?? props.partnerName;
  const communityName = params.communityName ?? props.communityName;

  const partnerId = toNumberExcept(params.partnerId ?? props.partnerId, [null, undefined]);

  const mapHeight =
    document.body.clientHeight - NAVIGATION_BAR_HEIGHT - BREAD_CRUMBS_HEIGHT - TITLE_HEIGHT - FOOTER_HEIGHT;

  const { data, refetch, isFetching } = useCommunityQuery(
    {
      referralClientId: clientId,
      communityId: partnerId ?? params.communityId ?? props.communityId,
    },
    {
      staleTime: 0,
      onSuccess: (data) => {
        if (isString(params.communityId)) {
          history.replace(`${params.communityName}--@id=${data.id}`);
        }
      },
      onError: setError,
    },
  );

  const communityId = data?.id ?? params.communityId ?? props.communityId;

  const { data: serviceCategories = [] } = useServiceCategoriesQuery({}, { staleTime: 0 });

  const filteredServiceCategories = useMemo(
    () => filter(serviceCategories, (o) => data?.marketplace?.serviceCategoryIds?.includes(o.id)),
    [data, serviceCategories],
  );

  const { data: services = [] } = useServicesQuery({}, { staleTime: 0 });

  const filteredServices = useMemo(
    () =>
      chain(services)
        .filter((o) => data?.marketplace?.serviceIds?.includes(o.id))
        .sortBy("title")
        .value(),
    [data, services],
  );

  const { data: languages = [] } = useMarketplaceLanguagesQuery({}, { staleTime: 0 });

  const filteredLanguages = useMemo(
    () => filter(languages, (o) => data?.marketplace?.languageIds?.includes(o.id)),
    [data, languages],
  );

  const { mutateAsync: save } = useCommunitySaving(
    { communityId: partnerId ?? communityId },
    {
      onSuccess: () => {
        setSaved(true);
        invalidateQuery("Marketplace.SavedCommunities");
      },
    },
  );

  const { mutateAsync: remove } = useCommunityRemoving(
    { communityId: partnerId ?? communityId },
    {
      onSuccess: () => {
        setSaved(false);
        invalidateQuery("Marketplace.SavedCommunities");
      },
    },
  );

  const onViewPartners = useCallback(() => {
    history.push(path(`marketplace/communities/${communityName}--@id=${communityId}/in-network-partners`));
  }, [history, communityId, communityName]);

  const onViewSavedCommunities = useCallback(() => {
    history.push(path("/saved-for-later"));
  }, [history]);

  const onOpenReferralRequestEditor = useCallback(() => {
    toggleReferralRequestEditorOpen(true);
  }, []);

  const onCloseReferralRequestEditor = useCallback(() => {
    toggleReferralRequestEditorOpen(false);
  }, []);

  const onSave = useCallback(() => save(), [save]);

  const onRemove = useCallback(() => remove(), [remove]);

  const onConversation = useCallback(() => {}, []);

  const onSaveReferralRequestSuccess = useCallback(() => {
    refetch();
    clearLocationState();
  }, [refetch, clearLocationState]);

  const onCommunityDeleted = useCallback(
    ({ data }) => {
      if (data.communityId === (partnerId ?? communityId)) setSaved(false);
    },
    [partnerId, communityId],
  );

  useEffect(() => {
    setSaved(data?.marketplace?.isSaved);
  }, [data]);

  useEffect(() => {
    if (pictureCarouselRef.current) {
      Carousel(pictureCarouselRef.current, {});
    }
  }, []);

  useEffect(() => {
    emitter.on("Marketplace.Community:deleted", onCommunityDeleted);
    return () => emitter.off("Marketplace.Community:deleted", onCommunityDeleted);
  }, [emitter, onCommunityDeleted]);

  return (
    <DocumentTitle
      title={
        "Simply Connect | Marketplace | " +
        (partnerId ? `${hyphenatedToTitle(communityName)} | In-Network Providers | ` : "") +
        "Community Details"
      }
    >
      <>
        <div className="MarketplaceCommunityDetails">
          <Breadcrumbs
            className="margin-top-15 margin-left-24 margin-bottom-15"
            items={compact([
              {
                title: "Marketplace",
                href: "/marketplace",
                isEnabled: true,
                onClick: (e) => {
                  if (isInteger(clientId)) {
                    e.preventDefault();
                    history.back();
                  }
                },
              },
              {
                title: "Community Details",
                href: `/marketplace/communities/${communityName}--@id=${communityId}`,
                isActive: !partnerId,
              },
              partnerId && {
                title: "In-Network Providers",
                href: `/marketplace/communities/${communityName}--@id=${communityId}/in-network-partners`,
              },
              partnerId && {
                title: "Community Details",
                href: `/marketplace/communities/${communityName}--@id=${communityId}/in-network-partners/${partnerName}--@id=${partnerId}`,
                isActive: true,
              },
            ])}
          />
          <div className="MarketplaceCommunityDetails-Body">
            <DataLoadable data={data} isLoading={isFetching} loaderStyle={{ marginTop: "70%" }}>
              {(data) => {
                const { marketplace, organizationId } = data;

                const marker = {
                  isSelected: true,
                  coordinate: {
                    lat: data.location?.latitude,
                    lng: data.location?.longitude,
                  },
                  data: { communityId: data.id },
                };

                const canRequestReferral = marketplace?.referralEmails?.length > 0;

                return (
                  <>
                    {/*<Row>
											<Picture
												className="MarketplaceCommunityDetails-Logo"
												path={`/organizations/${organizationId}/communities/${partnerId ? partnerId : communityId}/logo`}
											/>

											<Picture
												className="MarketplaceCommunityDetails-Logo"
												path={`/organizations/${organizationId}/logo`}
											/>
										</Row>*/}
                    <Row>
                      <Col md={4}>
                        <div className="MarketplaceCommunityDetails-Header">
                          <div className="MarketplaceCommunityDetails-Title">{data.name}</div>
                          <IconButton
                            size={24}
                            Icon={isSaved ? Heart : Heart2}
                            shouldHighLight={false}
                            onClick={isSaved ? onRemove : onSave}
                          />
                        </div>
                      </Col>
                    </Row>
                    <Row>
                      <Col md={4}>
                        <div
                          className="MarketplaceCommunityDetails-Summary"
                          style={{
                            maxHeight: window.innerWidth > THRESHOLD ? mapHeight : null,
                          }}
                        >
                          <div className="MarketplaceCommunityDetails-OrganizationCommunityNames">
                            {data.organizationName}
                          </div>

                          {isNotEmpty(filteredServiceCategories) && (
                            <div className="MarketplaceCommunityDetails-ServiceCategories">
                              {map(filteredServiceCategories, (o) => (
                                <div className="MarketplaceCommunityDetails-ServiceCategory">{o.label ?? o.title}</div>
                              ))}
                            </div>
                          )}

                          <div className="MarketplaceCommunityDetails-Address">
                            <Location className="MarketplaceCommunityDetails-Icon" />
                            {getStandardFormattedAddress({
                              ...data,
                              zip: data.zipCode,
                              state: data.stateAbbr,
                            })}
                          </div>

                          <div className="MarketplaceCommunityDetails-Phone">
                            <Phone className="MarketplaceCommunityDetails-Icon" />
                            {formatPhoneNumber(data.phone)}
                          </div>

                          {isNotEmpty(data.websiteUrl) && (
                            <div className="MarketplaceCommunityDetails-Website margin-bottom-24">
                              <Website className="MarketplaceCommunityDetails-Icon" />
                              <a className="link" target="_blank" href={getAbsoluteUrl(data.websiteUrl)}>
                                Visit Public Website
                              </a>
                            </div>
                          )}

                          {/*{isNotEmpty(data.canConversation) && (
														<div className="MarketplaceCommunityDetails-Website">
															<Website className="MarketplaceCommunityDetails-Icon"/>
															<a
																className="link"
																target="_blank"
																onClick={onConversation}
															>
																Open Chat
															</a>
														</div>
													)}*/}

                          <div className="d-flex flex-row flex-wrap">
                            {!partnerId && data.canViewPartners && (
                              <Button
                                outline
                                color="success"
                                className="MarketplaceCommunityDetails-ViewPartnersBtn margin-bottom-20"
                                onClick={onViewPartners}
                              >
                                View In-Network providers
                              </Button>
                            )}
                            {marketplace.isReferralEnabled && (
                              <Button
                                color="success"
                                className="margin-bottom-20"
                                disabled={!(canRequestReferral && marketplace.canAddReferral)}
                                onClick={onOpenReferralRequestEditor}
                                tooltip={
                                  !(canRequestReferral && marketplace.canAddReferral) && {
                                    placement: "top",
                                    trigger: "click hover",
                                    render: () => (
                                      <>
                                        <div>
                                          {!canRequestReferral &&
                                            `The community doesn't have email for receiving referral requests. Please contact Simply Connect support team.`}
                                        </div>
                                        <div>
                                          {!marketplace.canAddReferral && `You can't refer a client to this community.`}
                                        </div>
                                      </>
                                    ),
                                  }
                                }
                              >
                                Create Referral
                              </Button>
                            )}
                          </div>

                          {isNotEmpty(marketplace.servicesSummaryDescription) && (
                            <div className="MarketplaceCommunityDetails-ServicesSummaryDescription">
                              <ShowMore lines={4} more="more" less="less" anchorClass="ShowMoreBtn">
                                {marketplace.servicesSummaryDescription}
                              </ShowMore>
                            </div>
                          )}

                          {isNotEmpty(data.pictures) && (
                            <div className="MarketplaceCommunityDetails-Section margin-bottom-24">
                              <div className="MarketplaceCommunityDetails-SectionTitle margin-bottom-20">Photos</div>
                              <Carousel containerClassName="MarketplaceCommunityDetails-CommunityPhotosContainer">
                                {map(data.pictures, (o) => (
                                  <Carousel.Slide>
                                    <Picture
                                      hasViewer
                                      mimeType={o.mimeType}
                                      className="MarketplaceCommunityDetails-CommunityPhoto"
                                      path={`/organizations/${data.organizationId}/communities/${communityId}/pictures/${o.id}`}
                                    />
                                  </Carousel.Slide>
                                ))}
                              </Carousel>
                            </div>
                          )}

                          {isNotEmpty(data.numberOfBeds) && (
                            <div className="MarketplaceCommunityDetails-NumberOfBeds">
                              <div className="MarketplaceCommunityDetails-NumberOfBedsTitle">
                                <BedPulse className="margin-right-10" /># of Units
                              </div>
                              <div className="MarketplaceCommunityDetails-NumberOfVacantBedsTitle">
                                <Bed className="margin-right-10" /># of Open Units
                              </div>
                              <div className="MarketplaceCommunityDetails-NumberOfBedsValue">{data.numberOfBeds}</div>
                              <div className="MarketplaceCommunityDetails-NumberOfVacantBedsValue">
                                {data.numberOfVacantBeds}
                              </div>
                            </div>
                          )}

                          {isNotEmpty(filteredServices) && (
                            <CollapsibleSection
                              title="Services"
                              isOpenByDefault
                              className="MarketplaceCommunityDetails-ServicesSection"
                              headerClassName="MarketplaceCommunityDetails-ServicesSectionHeader"
                              titleClassName="MarketplaceCommunityDetails-ServicesSectionTitle"
                              renderHeaderIcon={({ isOpen, className }) =>
                                isOpen ? (
                                  <ChevronTop
                                    className={cn(className, "MarketplaceCommunityDetails-ServicesSectionIcon")}
                                  />
                                ) : (
                                  <ChevronBottom
                                    className={cn(className, "MarketplaceCommunityDetails-ServicesSectionIcon")}
                                  />
                                )
                              }
                            >
                              {map(filteredServices, (o) => (
                                <div key={o.id} className="MarketplaceCommunityDetails-Service">
                                  <div className="margin-right-8">
                                    <Checkmark />
                                  </div>
                                  <div>{o.label ?? o.title}</div>
                                </div>
                              ))}
                            </CollapsibleSection>
                          )}

                          {isNotEmpty(filteredLanguages) && (
                            <CollapsibleSection
                              title="Languages"
                              className="MarketplaceCommunityDetails-LanguagesSection"
                              headerClassName="MarketplaceCommunityDetails-LanguagesSectionHeader"
                              titleClassName="MarketplaceCommunityDetails-LanguagesSectionTitle"
                              renderHeaderIcon={({ isOpen, className }) =>
                                isOpen ? (
                                  <ChevronTop
                                    className={cn(className, "MarketplaceCommunityDetails-LanguagesSectionIcon")}
                                  />
                                ) : (
                                  <ChevronBottom
                                    className={cn(className, "MarketplaceCommunityDetails-LanguagesSectionIcon")}
                                  />
                                )
                              }
                            >
                              {map(filteredLanguages, (o) => (
                                <div key={o.id} className="MarketplaceCommunityDetails-Language">
                                  <div className="margin-right-8">
                                    <Checkmark />
                                  </div>
                                  <div>{o.label ?? o.title}</div>
                                </div>
                              ))}
                            </CollapsibleSection>
                          )}
                        </div>
                      </Col>
                      <Col md={8}>
                        <div style={{ height: mapHeight }}>
                          <Map markers={[marker]} />
                        </div>
                      </Col>
                    </Row>
                  </>
                );
              }}
            </DataLoadable>
          </div>
          <ReferralRequestEditor
            isOpen={isReferralRequestEditorOpen}
            communityId={data?.id}
            marketplace={{
              communityId: data?.id,
              communityName: data?.name,
              organizationId: data?.organizationId,
              organizationName: data?.organizationName,
              serviceIds: data?.marketplace?.serviceIds,
              selected: { serviceIds },
            }}
            onClose={onCloseReferralRequestEditor}
            onSaveSuccess={onSaveReferralRequestSuccess}
            successDialog={{
              text: `The request will be displayed in the "Outbound" section located under the "Referrals and Inquires" tab. You can see the details and manage status of the referral request there.`,
              ...(isInteger(clientId) && {
                buttons: {
                  submit: {
                    text: "Copy to Service plan",
                    onClick: () => onChoose(data),
                  },
                },
              }),
            }}
          />
          {error && <ErrorViewer isOpen error={error} onClose={() => setError(null)} />}
          {!isInteger(clientId) && (
            <SavedCommunitiesPopup
              onCreateReferral={onOpenReferralRequestEditor}
              onExtendedMode={onViewSavedCommunities}
              className="position-absolute-bottom-right"
            />
          )}
        </div>

        <Footer theme="gray" className="Marketplace-Footer" />
      </>
    </DocumentTitle>
  );
}

MarketplaceCommunityDetails.propTypes = {
  clientId: PTypes.number,
  communityId: PTypes.number,
  communityName: PTypes.string,
  onBack: PTypes.func,
  onChoose: PTypes.func,
};

MarketplaceCommunityDetails.defaultProps = {
  onBack: noop,
  onChoose: noop,
};

export default memo(MarketplaceCommunityDetails);
