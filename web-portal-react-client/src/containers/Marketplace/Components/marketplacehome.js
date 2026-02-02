import React, { memo, useEffect, useState } from "react";
import expandImg from "../../../images/marketplace/expand.svg";

import "./Marketplace.scss";
import Building from "../Components/Building/Building";
import { Link, useHistory } from "react-router-dom";
import { path } from "../../../lib/utils/ContextUtils";
import DocumentTitle from "react-document-title";
import Footer from "../../../components/Footer/Footer";
import { useDispatch, useSelector } from "react-redux";
import SelectOnly from "../../../components/SelectOnly";
import MarketplaceSearch from "./SearchData/MarketplaceSearch";
import { Loader, PrimaryFilter } from "../../../components";
import { useCommunityPrimaryFilterDirectory } from "../../../hooks/business/Marketplace";
import { usePrimaryFilter } from "../../../hooks/common/filter";
import { useAuthUser, useQueryParams } from "../../../hooks/common";
import { getBuildingList } from "../../../redux/marketplace/Building/BuildingActions";
import VendorV1 from "./Vendor/VendorV1/VendorV1";
import { ALL_VENDORS_ROLES, ONLY_VIEW_ROLES, VENDOR_SYSTEM_ROLES } from "../../../lib/Constants";
const searchType = [
  {
    label: "Building",
    id: "Building",
  },
  {
    label: "Vendor",
    id: "Vendor",
  },
];

function MarketplaceHome() {
  const MAX_COMMUNITY_COUNT = 8;
  const user = useAuthUser();

  const history = useHistory();

  const totalCount = useSelector((state) => state.building.totalCount);

  const [currentExpand, setCurrentExpand] = useState([true, true]);
  const [selectValue, setSelectValue] = useState(searchType[0]);
  const [isFromSearch, setIsFromSearch] = useState(false);
  const [searchValue, setSearchValue] = useState(""); // 传递值
  const [isFetching, setIsFetching] = useState(false);

  const [isSearch, setIsSearch] = useState(false);

  const defaultOrganizationId = JSON.parse(localStorage.getItem(`AUTHENTICATED_USER`) || `{}`)?.organizationId;

  const primaryFilter = usePrimaryFilter("FEATURED_COMMUNITY_PRIMARY_FILTER", {
    isCommunityMultiSelection: false,
  });

  const isVendorSystemRole = ALL_VENDORS_ROLES.includes(user.roleName);

  const { communityId, organizationId } = primaryFilter.data;
  const expandStatus = (index) => {
    currentExpand[index] = !currentExpand[index];

    setCurrentExpand([...currentExpand]);

    sessionStorage.setItem("_currentExpand", JSON.stringify([...currentExpand]));
  };

  useEffect(() => {
    setIsFetching(true);
    const expandStatus = JSON.parse(sessionStorage.getItem("_currentExpand") || "[true,true]");
    setCurrentExpand([...expandStatus]);
    setTimeout(() => {
      setIsFetching(false);
    }, 1500);

    sessionStorage.removeItem("vendorListCurrentPage");
  }, []);

  const { providerId, communityId: cId, organizationId: orgId, shouldCreateReferral } = useQueryParams();

  useEffect(() => {
    if (shouldCreateReferral) {
      primaryFilter.changeFields({
        organizationId: orgId,
        communityId: cId,
      });
    }
  }, [cId, orgId, providerId, shouldCreateReferral]);

  const changeType = (value) => {
    setSelectValue(value);
  };

  const { communities, organizations } = useCommunityPrimaryFilterDirectory(
    { organizationId, communityId },
    { actions: primaryFilter.changeCommunityField },
  );

  const dispatch = useDispatch();

  const communityFilter = communities?.find((item) => item.id === communityId);

  const orgFilter = organizations?.find((item) => item.id === organizationId);

  useEffect(() => {
    if (organizationId && !isSearch) {
      dispatch(getBuildingList(1, 12, "", organizationId));
    }
  }, [organizationId, isSearch]);

  const buildingList = useSelector((state) => state.building.data);

  return (
    <DocumentTitle title="Simply Connect | Marketplace">
      <div className="MarketplaceWrap">
        <div className="MarketplaceHeader">
          <div className="MarketplaceHeadertitle">Marketplace</div>

          <div className="MarketplaceHeadertitleRight">
            {selectValue.id === searchType[0].id && (
              <PrimaryFilter
                communities={communities}
                organizations={organizations}
                {...primaryFilter}
                hasCommunityField={false}
                onChangeOrganizationField={(organizationId) => primaryFilter.changeOrganizationField(organizationId)}
                isCommunityMultiSelection={false}
                classNameOrg="classNameOrg"
              />
            )}

            <div className="MarketplaceHeadertitleSearch">
              <SelectOnly
                className="markplaceSearch"
                defaultValue={searchType[0]}
                options={searchType}
                name="markplaceSearch"
                selectedOption={selectValue}
                selectedChange={changeType}
              />
              <input
                type="text"
                className={"markplaceSearchInput"}
                onChange={(e) => {
                  setSearchValue(e.target.value);
                  setIsSearch(true);
                }}
                value={searchValue}
              />
            </div>

            <Link to={path("/marketplace/simplyNexus")} id={"marketplace-simplyNexus"}>
              <div className="MarketplaceHeaderTitleButton">Simply Nexus</div>
            </Link>
          </div>
        </div>
        {isFetching && <Loader hasBackdrop style={{ position: "fixed" }} />}

        {/* building  */}

        {!isSearch ? (
          <>
            <div className="BuildingHeader">
              <div className="BuildingHeaderLeft" onClick={() => expandStatus(0)}>
                <img src={expandImg} alt="" className={currentExpand[0] ? "" : "rotateSvg"} />
                <span>Building Directory</span>
              </div>

              {totalCount > 12 && (
                <div
                  className="BuildingHeaderRight"
                  onClick={() => {
                    history.push(`/web-portal/marketplace/buildingList/${organizationId}`);
                  }}
                >
                  View More>
                </div>
              )}
            </div>
            {/*    传入 organizationId 和 communityId*/}
            {currentExpand[0] && (
              <Building
                buildingList={buildingList}
                organizationId={organizationId}
                organizationName={orgFilter?.label}
                communityName={communityFilter?.name}
                communityId={communityId}
              />
            )}

            {/* vendor */}

            <div className="VendorHeader">
              <div className="VendorHeaderLeft" onClick={() => expandStatus(1)}>
                <img src={expandImg} alt="" className={currentExpand[1] ? "" : "rotateSvg"} />
                <span>Vendor Directory</span>
              </div>

              {!isVendorSystemRole && (
                <Link to={path("/marketplace/vendorList")} className="VendorHeaderRight">
                  View More>
                </Link>
              )}
            </div>

            {currentExpand[1] && <VendorV1 />}
          </>
        ) : (
          <>
            <MarketplaceSearch
              organizationId={organizationId}
              searchType={selectValue}
              isFromSearch={isFromSearch}
              setIsFromSearch={setIsFromSearch}
              searchData={searchValue}
              setIsSearch={setIsSearch}
              setSearchValue={setSearchValue}
              setSelectValue={setSelectValue}
              communityId={communityId}
              organizationName={orgFilter?.label}
              communityName={communityFilter?.name}
            />
          </>
        )}

        <Footer theme="gray" className="markplaceFooter" />
      </div>
    </DocumentTitle>
  );
}

export default memo(MarketplaceHome);
