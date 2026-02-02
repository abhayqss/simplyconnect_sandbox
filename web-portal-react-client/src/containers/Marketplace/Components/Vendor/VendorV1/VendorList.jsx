import React, { useEffect, useMemo, useState } from "react";
import "./VendorList.scss";
import cn from "classnames";
import { ReactComponent as Filter } from "images/filters.svg";
import { SYSTEM_ROLES } from "../../../../../lib/Constants";
import { Badge, Button, Col, Collapse, Row } from "reactstrap";
import { SelectField, TextField } from "../../../../../components/Form";
import Pagination from "../../../../../components/Pagination/Pagination";
import services from "../../../../../services/OrganizationService";
import service from "../../../../../services/Marketplace";
import { Loader } from "../../../../../components";
import { useHistory } from "react-router-dom";
import { useDispatch, useSelector } from "react-redux";
import PrimaryFilterWrapper from "./VendorPrimaryFilter";
import { getVendorType } from "../../../../../redux/marketplace/Vendor/VendorActions";
import { useAuthUser } from "../../../../../hooks/common";
import { map } from "underscore";
import { useStatesQuery } from "../../../../../hooks/business/directory/query";
import defaultImg from "../../../../../images/marketplace/defaultImg.png";
import ReferralRequestEditor from "../../../../Referrals/ReferralRequestEditor/ReferralRequestEditor";
import { SuccessDialog } from "../../../../../components/dialogs";
import adminAssociationsService from "../../../../../services/AssociationsService";

const vendorList = () => {
  const history = useHistory();
  const dispatch = useDispatch();
  const { HOME_CARE_ASSISTANT } = SYSTEM_ROLES;
  const ROLES_WITH_DISABLED_FILTER = [HOME_CARE_ASSISTANT];

  const { communityId, organizationId } = useSelector((state) => state.Vendor);

  const [isFilterOpen, setIsFilterOpen] = useState(true);
  const [page, setPage] = useState();
  const [totalCount, setTotalCount] = useState(0);
  const size = 18; // 每页显示条数
  const [isFetching, setIsFetching] = useState(false);
  const [vendorList, setVendorList] = useState([]);
  const [selectVendorId, setSelectVendorId] = useState();
  const [selectVendor, setSelectVendor] = useState();
  const [keyword, setKeyword] = useState("");
  const [category, setCategory] = useState("");
  const [state, setState] = useState("");
  const [vendorReferModalOpen, setVendorReferModalOpen] = useState(false);
  const [isReferSuccessDialogOpen, setIsReferSuccessDialogOpen] = useState(false);

  function valueTextMapper({ id, name, title, label }) {
    return { value: id || name, text: title || label || name };
  }

  const VendorTypeData = useSelector((state) => state.vendor.vendorTypeData);

  const { data: states = [] } = useStatesQuery();

  const mappedStates = useMemo(() => map(states, valueTextMapper), [states]);

  useEffect(() => {
    dispatch(getVendorType());
  }, []);

  const user = useAuthUser();
  const { ASSOCIATION } = SYSTEM_ROLES;
  const [isAssociation, setIsAssociation] = useState(false);
  useEffect(() => {
    if (user.roleName === ASSOCIATION) {
      setIsAssociation(true);
    } else {
      setIsAssociation(false);
    }
  }, [user]);
  const selectUser = (state) => state.auth.login.user.data;

  function isFilterEnabled() {
    return Boolean(selectUser && !ROLES_WITH_DISABLED_FILTER.includes(selectUser.roleName));
  }

  const handlePageChange = (newPage) => {
    sessionStorage.setItem("vendorListCurrentPage", newPage);
    setPage(newPage);
  };

  const handleFieldChange = (name, value) => {
    switch (name) {
      case "keyword":
        setKeyword(value);
        break;
      case "category":
        setCategory(value);
        break;
      case "state":
        setState(value);
        break;
      default:
        break;
    }
  };

  useEffect(() => {
    const storedPage = sessionStorage.getItem("vendorListCurrentPage");
    if (storedPage) {
      setPage(Number(storedPage));
    } else {
      setPage(1);
    }
  }, []);

  useEffect(() => {
    setVendorList([]);
    setIsFetching(true);

    if (isAssociation) {
      const params = {
        page: page - 1,
        size,
      };
      getAssociationVendorData();
    } else {
      const params = {
        communityId,
        page: page - 1,
        size,
      };
      getVendorDataList(params);
    }
  }, [communityId, page, isAssociation]);

  const getVendorDataList = (params) => {
    if (params.communityId !== null) {
      services.featVendorOfAssociation(params).then(async (res) => {
        // 获取供应商的 Logo
        const fetchLogos = res.data.map(async (item) => {
          try {
            if (item.logo) {
              // 请求 Logo 数据并赋值给 item.logo
              await service.getVendorLogo(item.vendorId).then((response) => {
                item.logo = response.data;
                return Promise.resolve();
              });
            }
            return Promise.resolve();
          } catch (e) {
            return Promise.resolve();
          }
        });

        await Promise.all(fetchLogos);

        // 更新供应商列表
        setVendorList(res.data);
        setTotalCount(res.totalCount);
        setIsFetching(false);
      });
    } else {
      setVendorList([]);
      setTotalCount(0);
      setIsFetching(false);
    }
  };

  const getAssociationVendorData = (params) => {
    adminAssociationsService.marketPlaceFindAssociationsVendor(params).then(async (res) => {
      // 获取供应商的 Logo
      const fetchLogos = res?.data?.map(async (item) => {
        item.vendorId = item.id;
        try {
          if (item.logo) {
            // 请求 Logo 数据并赋值给 item.logo
            await service.getVendorLogo(item.vendorId).then((response) => {
              item.logo = response.data;
              return Promise.resolve();
            });
          }
          return Promise.resolve();
        } catch (e) {
          return Promise.resolve();
        }
      });

      await Promise.all(fetchLogos);

      // 更新供应商列表
      setVendorList(res.data);
      setTotalCount(res.totalCount);
      setIsFetching(false);
    });
  };

  const onReset = () => {
    setKeyword(null);
    setState(null);
    setCategory(null);
  };
  const apply = () => {
    if (keyword || state || category) {
      if (isAssociation) {
        getAssociationVendorData({
          state,
          vendorTypeIds: category,
          keyword,
        });
      } else {
        getVendorDataList({
          communityId,
          state,
          vendorTypeIds: category,
          keyword,
        });
      }
    }
  };

  const onRefer = (item) => {
    setSelectVendorId(item?.vendorId);
    setSelectVendor(item);
    setVendorReferModalOpen(true);
  };

  const onCloseReferModal = () => {
    setVendorReferModalOpen(false);
  };

  const onSaveReferralRequestSuccess = () => {
    setVendorReferModalOpen(false);
  };

  const goDetail = (id) => {
    sessionStorage.setItem("vendorListCurrentPage", page);
    history.push(`/web-portal/marketplace/vendorDetail/${id}`, {
      title: "VendorList",
      href: `/marketplace/vendorList`,
      isEnabled: true,
    });
  };
  return (
    <div className="vendorListWrap">
      {isFetching && <Loader hasBackdrop style={{ position: "fixed" }} />}
      <div>{!isAssociation && <PrimaryFilterWrapper />}</div>
      <div className={"vendorListWrapHeader"}>
        <div className="vendorListTitle">
          <div className="vendorListTitleText">Vendor List</div>
          {totalCount > 0 && (
            <Badge color="info" className="vendorTitleBadge">
              {totalCount}
            </Badge>
          )}
        </div>
        <div className="Vednors-Actions">
          {isFilterEnabled() && (
            <Filter
              className={cn(
                "VendorsFilter-Icon",
                isFilterOpen ? "VendorsFilter-Icon_rotated_90" : "VendorsFilter-Icon_rotated_0",
              )}
              onClick={() => setIsFilterOpen(!isFilterOpen)}
            />
          )}
        </div>
      </div>

      {isFilterEnabled && (
        <Collapse isOpen={isFilterOpen}>
          <div className="vendorListCollapse">
            <Row>
              <Col md={4} lg={4}>
                <TextField
                  type="text"
                  name="keyword"
                  value={keyword}
                  label="Keyword"
                  placeholder="Find your ideal vendor by name or zip code"
                  onChange={handleFieldChange}
                />
              </Col>

              <Col md={4} lg={4}>
                <SelectField
                  hasAllOption
                  name="category"
                  options={VendorTypeData.map((option) => ({
                    value: option.id,
                    text: option.name,
                  }))} // 转换成MultiSelect需要的格式
                  value={category}
                  label="Category"
                  placeholder={"Select Category"}
                  // className="ClientFilter-SelectField"
                  isMultiple={true}
                  onChange={handleFieldChange}
                />
              </Col>

              <Col md={4} lg={4}>
                <SelectField
                  hasAllOption
                  name="state"
                  options={mappedStates}
                  value={state}
                  label="State"
                  placeholder={"Select State"}
                  isMultiple={false}
                  onChange={handleFieldChange}
                />
              </Col>

              {/* <Col md={4} lg={4}>
            <SelectField
              hasAllOption
              name="service"
              options={[]}
              value={""}
              label="Service"
              placeholder={"Select Service"}
              isMultiple={true}
            />
          </Col>*/}
            </Row>

            <Row>
              <Col
                md={12}
                lg={12}
                className="padding-top-31"
                style={{ display: "flex", justifyContent: "flex-end", alignItems: "center", flexDirection: "row" }}
              >
                <Button outline color="success" data-testid="clear-btn" className="margin-right-25" onClick={onReset}>
                  Clear
                </Button>
                <Button color="success" data-testid="apply-btn" onClick={apply}>
                  Apply
                </Button>
              </Col>
            </Row>
          </div>
        </Collapse>
      )}

      <div className="vendorBoxWrap">
        {vendorList.map((item) => {
          return (
            <div className="vendorBox" key={item.vendorId} onClick={() => goDetail(item.vendorId)}>
              <div className="vendorListImgBox">
                <img
                  src={item?.logo ? `data:image/png;base64,${item?.logo}` : defaultImg}
                  className="vendorImg"
                  alt={item?.vendorName || item?.name}
                />
              </div>

              <div className="vendorName">{item.vendorName || item.name || "-"}</div>
              <div className="vendorAddress">
                {item?.street || "-"} <span className="vendorZipCode">{item?.stateInfo?.zipCode || "-"}</span>
              </div>
              <div className="vendorPhone">{item?.phone || "-"}</div>
              <Button
                outline
                color="success"
                data-testid="clear-btn"
                className="vendorReferButton"
                disabled={!item?.canRefer}
                onClick={(e) => {
                  e.preventDefault();
                  e.stopPropagation();
                  onRefer(item);
                }}
              >
                Refer
              </Button>
            </div>
          );
        })}
      </div>
      <ReferralRequestEditor
        isFromVendor={true}
        isFromSearch={true}
        isOpen={vendorReferModalOpen}
        isClinicalVendor={selectVendor?.hieAgreement}
        marketplace={{}}
        vendorId={selectVendorId}
        organizationId={organizationId}
        onClose={onCloseReferModal}
        onSaveSuccess={onSaveReferralRequestSuccess}
        successDialog={{
          text: `The request will be displayed in the "Outbound" section located under the "Referrals and Inquires" tab. You can see the details and manage status of the referral request there.`,
        }}
      />

      {isReferSuccessDialogOpen && (
        <SuccessDialog
          isOpen
          title="Refer have been send."
          buttons={[
            {
              text: "OK",
              onClick: () => {
                setIsReferSuccessDialogOpen(false);
              },
            },
          ]}
        />
      )}
      <div style={{ marginTop: 10 }}>
        <Pagination page={page} size={size} totalCount={totalCount} onPageChange={handlePageChange} />
      </div>
    </div>
  );
};

export default vendorList;
