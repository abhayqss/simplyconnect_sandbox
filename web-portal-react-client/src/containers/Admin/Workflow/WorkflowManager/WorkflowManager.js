import DocumentTitle from "react-document-title";
import { UpdateSideBarAction } from "actions/admin";
import { Breadcrumbs } from "components";
import React, { useEffect, useMemo, useState, useRef, useCallback } from "react";
import { useQueryClient } from "@tanstack/react-query";
import Loader from "components/Loader/Loader";
import Table from "components/Table/Table";
import SearchField from "components/SearchField/SearchField";
import "./WorkflowManager.scss";
import { Button, ButtonGroup, Col, Row, UncontrolledTooltip as Tooltip } from "reactstrap";
import moment from "moment";
import Actions from "components/Table/Actions/Actions";
import AddWorkflowTemplate from "components/AdminWorkflow/AddAdminWorkflow/AddWorkflowTemplate";
import { Link, useHistory } from "react-router-dom";
import { path } from "lib/utils/ContextUtils";
import { useFetchWorkflowDetail } from "hooks/business/admin/workflow";

import WorkflowPrimaryFilter from "../WorkflowPrimaryFilter/WorkflowPrimaryFilter";
import {
  useWorkflowFilterCombination,
  useWorkflowOptionsQuery,
  useServicePlanTemplatesQuery,
  useOrganizationOptionsQuery,
  useDeleteWorkflowTemplateMutation,
  useDeleteServicePlanTemplateMutation,
  useRemovePublishedWorkflowTemplateMutation,
  useRemovePublishedServicePlanTemplateMutation,
} from "hooks/business/admin/workflow";
import { SurveyPDF } from "survey-pdf";
import { ErrorDialog, SuccessDialog } from "../../../../components/dialogs";
import CopyCustomTemplate from "./copyCustomTemplate";
import CopyWorkflowLibraryTemplate from "./copyWorkflowLibraryTemplate";
import PublishWorkflowToOtherOrganization from "./PublishWorkflowToOtherOrganization";
import { deleteSurvey, openDatabase, saveSurvey } from "../../../../lib/utils/surveyJson";
import initSurveyCustomComponent from "../WorkflowManagementCreate/CustomComponent/initSurveyCustomComponent";

const WorkflowManager = () => {
  const queryClient = useQueryClient();
  const [isLoading, setIsLoading] = useState(false);

  // 受控输入
  const [searchName, setSearchName] = useState("");
  const [page, setPage] = useState(1);
  const [workflowDataListPagination, setWorkflowDataListPagination] = useState();
  const history = useHistory();
  const [sort, setSort] = useState("");
  const [isShowAddModal, setIsShowAddModal] = useState(false);

  const [copyWorkflowModel, setCopyWorkflowModel] = useState(false);
  const [currentCopyWorkflowData, setCurrentCopyWorkflowData] = useState();

  const [copyModel, setCopyModel] = useState(false);

  const [publishWorkflowModel, setPublishWorkflowModel] = useState(false);
  const [currentPublishWorkflowData, setCurrentPublishWorkflowData] = useState();

  // 只做受控赋值
  const onChangeSearchNameFilterField = useCallback((name, value) => {
    setSearchName(value);
  }, []);

  // 防抖后的搜索名
  const [debouncedSearchName, setDebouncedSearchName] = useState("");
  useEffect(() => {
    const handler = setTimeout(() => {
      setDebouncedSearchName(searchName);
      setPage(1);
    }, 300);
    return () => clearTimeout(handler);
  }, [searchName]);
  const [currentCopyData, setCurrentCopyData] = useState();

  const [isShowSuccessDialog, setIsShowSuccessDialog] = useState(false);
  const [successTitle, setSuccessTitle] = useState("");
  const [errorMessage, setErrorMessage] = useState(false);

  const [isEdit, setIsEdit] = useState(false);
  const [editData, setEditData] = useState({});

  const [isWorkflow, setIsWorkflow] = useState(true);

  // 用于编辑时获取详情
  const [editWorkflowId, setEditWorkflowId] = useState(null);
  const { data: editDetail, isLoading: isEditDetailLoading } = useFetchWorkflowDetail(editWorkflowId, {
    enabled: !!editWorkflowId,
  });

  React.useEffect(() => {
    if (editDetail && editWorkflowId) {
      const apiData = editDetail.data;
      const workflowData = {
        categoryId: apiData.categoryId,
        communityIds: apiData.communityIds,
        documentESign: apiData.documentESign,
        name: apiData.name,
        ongoingWorkflow: apiData.ongoingWorkflow,
        organizationId: apiData.organizationId,
        scoreWorkflow: apiData.scoreWorkflow,
        id: apiData.id,
        isCreateServicePlan: apiData.isCreateServicePlan || false,
        code: apiData.code,
      };
      const surveyJson = JSON.parse(apiData.content);
      const surveyQuestion = {
        logoPosition: surveyJson.logoPosition,
        elements: surveyJson.pages[0].elements,
      };
      localStorage.setItem("workflowData", JSON.stringify(workflowData));
      openDatabase().then((db) => saveSurvey(db, JSON.stringify(surveyQuestion)));
      setEditWorkflowId(null);
      history.push("/web-portal/admin/workflowManagement/create");
    }
  }, [editDetail, editWorkflowId, history]);

  // Get filter data from existing hooks
  const { primary, custom } = useWorkflowFilterCombination(
    {
      onChange: () => {},
    },
    {},
  );
  const { organizationId, communityIds } = primary?.data || {};

  // Prepare query parameters
  const queryParams = useMemo(
    () => ({
      page: page - 1,
      size: 10,
      sort,
      organizationId: organizationId || null,
      communityIds: Array.isArray(communityIds) ? communityIds : [],
      name: debouncedSearchName,
    }),
    [page, sort, organizationId, communityIds, debouncedSearchName],
  );

  // TanStack Query hooks
  const { data: organizationOptionsData } = useOrganizationOptionsQuery();

  const {
    data: workflowData,
    isLoading: isWorkflowLoading,
    isFetching: isWorkflowFetching,
    error: workflowError,
    refetch: refetchWorkflow,
  } = useWorkflowOptionsQuery(queryParams, {
    enabled: isWorkflow && !!(organizationId && Array.isArray(communityIds) && communityIds.length > 0),
  });

  const {
    data: servicePlanData,
    isLoading: isServicePlanLoading,
    isFetching: isServicePlanFetching,
    error: servicePlanError,
    refetch: refetchServicePlan,
  } = useServicePlanTemplatesQuery(queryParams, {
    enabled: !isWorkflow && !!(organizationId && Array.isArray(communityIds) && communityIds.length > 0),
  });

  // Mutations
  const deleteWorkflowMutation = useDeleteWorkflowTemplateMutation({
    onSuccess: () => {
      setIsShowSuccessDialog(true);
      setSuccessTitle("Workflow template deleted successfully.");
      // 清除所有相关的查询缓存，避免分页数据混乱
      queryClient.invalidateQueries(["workflow-templates"]);
      queryClient.invalidateQueries(["service-plan-templates"]);
      // 智能分页处理
      handlePageAfterDelete();
      setIsLoading(false);
    },
    onError: () => {
      setErrorMessage("Delete anomaly.");
      setIsLoading(false);
    },
  });

  const deleteServicePlanMutation = useDeleteServicePlanTemplateMutation({
    onSuccess: () => {
      setIsShowSuccessDialog(true);
      setSuccessTitle("Service Plan template deleted successfully.");
      // 清除所有相关的查询缓存，避免分页数据混乱
      queryClient.invalidateQueries(["workflow-templates"]);
      queryClient.invalidateQueries(["service-plan-templates"]);
      // 智能分页处理
      handlePageAfterDelete();
      setIsLoading(false);
    },
    onError: () => {
      setErrorMessage("Delete anomaly.");
      setIsLoading(false);
    },
  });

  const removePublishedWorkflowMutation = useRemovePublishedWorkflowTemplateMutation({
    onSuccess: () => {
      setIsShowSuccessDialog(true);
      setSuccessTitle("Workflow template archived successfully.");
      // 清除所有相关的查询缓存，避免分页数据混乱
      queryClient.invalidateQueries(["workflow-templates"]);
      queryClient.invalidateQueries(["service-plan-templates"]);
      // 智能分页处理
      handlePageAfterDelete();
      setIsLoading(false);
    },
    onError: () => {
      setErrorMessage("Archived anomaly.");
      setIsLoading(false);
    },
  });

  const removePublishedServicePlanMutation = useRemovePublishedServicePlanTemplateMutation({
    onSuccess: () => {
      setIsShowSuccessDialog(true);
      setSuccessTitle("Service plan template archived successfully.");
      // 清除所有相关的查询缓存，避免分页数据混乱
      queryClient.invalidateQueries(["workflow-templates"]);
      queryClient.invalidateQueries(["service-plan-templates"]);
      // 智能分页处理
      handlePageAfterDelete();
      setIsLoading(false);
    },
    onError: () => {
      setErrorMessage("Archived anomaly.");
      setIsLoading(false);
    },
  });

  // Derived data
  const organizationOptions = organizationOptionsData?.data || [];
  const currentData = isWorkflow ? workflowData?.data || [] : servicePlanData?.data || [];

  // 只有当查询被启用时才考虑 loading 状态
  const isQueryEnabled = !!(organizationId && Array.isArray(communityIds) && communityIds.length > 0);
  const isFetching =
    isQueryEnabled &&
    (isWorkflow ? isWorkflowLoading || isWorkflowFetching : isServicePlanLoading || isServicePlanFetching);

  // 统一使用全局 loading 策略
  //
  // 原因：列表内的 loading 会导致以下问题：
  // 1. 数据先消失再展示，造成视觉闪烁
  // 2. 用户体验差，看起来像是数据丢失了
  // 3. 特别是在缓存失效重新获取数据时，闪烁更明显
  //
  // 全局 loading 的优势：
  // 1. 保持数据展示，不会出现空白
  // 2. 用户明确知道系统正在处理
  // 3. 视觉体验更加平滑
  const isManualLoading = isLoading; // 手动操作（删除、归档等）的 loading
  const isEditLoading = isEditDetailLoading && !!editWorkflowId; // 编辑详情加载
  const isDataFetching = isFetching; // 数据获取/缓存失效重新获取的 loading

  // 统一使用全局 loading，避免数据消失再展示的闪烁效果
  const shouldShowGlobalLoading = isManualLoading || isEditLoading || isDataFetching;

  const currentTotalCount = isWorkflow ? workflowData?.totalCount : servicePlanData?.totalCount;

  useEffect(() => {
    localStorage.removeItem("workflowData");

    openDatabase().then((db) => {
      return deleteSurvey(db);
    });

    initSurveyCustomComponent();
  }, []);

  // Update pagination when data changes
  useEffect(() => {
    if (currentTotalCount !== undefined) {
      setWorkflowDataListPagination({
        page: page,
        size: 10,
        totalCount: currentTotalCount,
      });
    }
  }, [currentTotalCount, page]);

  // 智能分页处理函数 - 缓存清除后的页码调整
  const handlePageAfterDelete = useCallback(() => {
    // 延迟执行，等待查询缓存清除和数据重新获取
    setTimeout(() => {
      const currentTotalCountAfterDelete = isWorkflow ? workflowData?.totalCount : servicePlanData?.totalCount;

      if (currentTotalCountAfterDelete !== undefined) {
        const pageSize = 10;
        const totalPages = Math.ceil(currentTotalCountAfterDelete / pageSize);

        // 如果没有数据，跳转到第一页
        if (currentTotalCountAfterDelete === 0) {
          setPage(1);
          return;
        }

        // 如果当前页超出了总页数，跳转到最后一页
        if (page > totalPages) {
          setPage(totalPages);
          return;
        }

        // 检查当前页是否还有数据
        const currentPageStartIndex = (page - 1) * pageSize;
        const hasDataOnCurrentPage = currentPageStartIndex < currentTotalCountAfterDelete;

        if (!hasDataOnCurrentPage && totalPages > 0) {
          setPage(totalPages);
          return;
        }

        // 否则保持当前页不变
      }
    }, 100); // 短暂延迟确保数据已更新
  }, [page, isWorkflow, workflowData?.totalCount, servicePlanData?.totalCount]);

  const onSort = (field, sort) => {
    setSort(`${field},${sort}`);
  };
  const onEditItem = (data) => {
    const workflowData = {
      categoryId: data.categoryId,
      communityIds: data.communityIds,
      documentESign: data.documentESign,
      name: data.name,
      ongoingWorkflow: data.ongoingWorkflow,
      organizationId: data.organizationId,
      scoreWorkflow: data.scoreWorkflow,
      id: data.id,
      isCreateServicePlan: data?.isCreateServicePlan || false,
      code: data.code, // 确保 code 一起带上
    };

    const surveyJson = JSON.parse(data.content);

    const surveyQuestion = {
      logoPosition: surveyJson.logoPosition,
      elements: surveyJson.pages[0].elements,
    };
    localStorage.setItem("workflowData", JSON.stringify(workflowData));

    // localStorage.setItem("survey-json", JSON.stringify(surveyQuestion));

    openDatabase().then((db) => {
      return saveSurvey(db, JSON.stringify(surveyQuestion));
    });

    history.push("/web-portal/admin/workflowManagement/create");
  };

  const onDeleteItem = (data) => {
    setIsLoading(true);
    deleteWorkflowMutation.mutate(data.id);
  };

  const onDeleteServicePlan = (data) => {
    setIsLoading(true);
    deleteServicePlanMutation.mutate(data.id);
  };

  const onDownloadItem = (data) => {
    downloadPDF(data.content, data.name);
  };

  const editWorkflow = () => {
    setIsShowAddModal(false);
    setIsShowSuccessDialog(true);
    setSuccessTitle("Workflow template edited successfully.");
    if (isWorkflow) {
      refetchWorkflow();
    } else {
      refetchServicePlan();
    }
  };

  const removePublishTemplate = (row) => {
    removePublishedWorkflowMutation.mutate(row.id);
  };

  const removeServicePlanPublishTemplate = (row) => {
    removePublishedServicePlanMutation.mutate(row.id);
  };

  const ICON_SIZE = 36;
  const SuperAdminColumns = [
    {
      dataField: "name",
      text: "Name",
      sort: true,
      onSort: onSort,
      align: "left",
      headerStyle: {
        width: "350px",
      },
      formatter: (v, row, isMobile) => {
        return (
          <>
            <div className="">
              <Link
                id={`${isMobile ? "m-" : ""}workflow-${row.id}`}
                to={path(`/admin/workflowManagement/preview/${row.id}/1`)}
                className="workflowTable-name-Item"
                onClick={() => {
                  localStorage.setItem("triggerCurrentComId", JSON.stringify(row.communityIds));
                }}
              >
                {v}
              </Link>
            </div>
            <Tooltip
              placement="top"
              target={`${isMobile ? "m-" : ""}workflow-${row.id}`}
              modifiers={[
                {
                  name: "offset",
                  options: { offset: [0, 6] },
                },
                {
                  name: "preventOverflow",
                  options: { boundary: document.body },
                },
              ]}
            >
              Preview
            </Tooltip>
          </>
        );
      },
    },
    {
      dataField: "communityIds",
      text: "Communities",
      sort: false,
      align: "left",
      headerAlign: "left",
      headerClasses: "VendorsList-CommunitiesColHeader",
      formatter: (v, row) => {
        return v ? (
          <>
            <a tabIndex={0} data-toggle="tooltip" id={`servicePlan-${row.id}_com-count`} className="Vendor-not-zero">
              {v.length}
            </a>

            <Tooltip
              trigger="focus"
              placement="top"
              target={`servicePlan-${row.id}_com-count`}
              modifiers={[
                {
                  name: "offset",
                  options: { offset: [0, 6] },
                },
                {
                  name: "preventOverflow",
                  options: { boundary: document.body },
                },
              ]}
            >
              <div style={{ maxHeight: 300, overflow: "scroll" }}>
                {row?.communityNames?.map((name, index) => (
                  <div key={index}>{name}</div>
                ))}
              </div>
            </Tooltip>
          </>
        ) : (
          0
        );
      },
    },
    // 新增 Code 列
    {
      dataField: "code",
      text: "Code",
      align: "left",
      headerAlign: "left",
      headerStyle: { width: "120px", minWidth: "120px" },
      formatter: (v, row) => (
        <div
          style={{
            maxWidth: 100,
            overflow: "hidden",
            textOverflow: "ellipsis",
            whiteSpace: "nowrap",
            display: "inline-block",
            verticalAlign: "middle",
          }}
          id={`code-tooltip-${row.id}`}
          title=""
        >
          {v || "-"}
          <Tooltip
            placement="top"
            target={`code-tooltip-${row.id}`}
            modifiers={[
              { name: "offset", options: { offset: [0, 6] } },
              { name: "preventOverflow", options: { boundary: document.body } },
            ]}
          >
            {v || "-"}
          </Tooltip>
        </div>
      ),
    },
    {
      dataField: "createTime",
      text: "Created",
      sort: true,
      align: "left",
      headerAlign: "left",
      headerClasses: "hide-on-tablet",
      classes: "hide-on-tablet",
      onSort: onSort,
      formatter: (v) => v && moment(v).format("MM/DD/YYYY"),
    },
    {
      dataField: "publishTime",
      text: "Published On",
      sort: true,
      align: "left",
      headerAlign: "left",
      headerClasses: "hide-on-tablet",
      classes: "hide-on-tablet",
      onSort: onSort,
      formatter: (v) => v && moment(v).format("MM/DD/YYYY"),
    },
    {
      dataField: "status",
      text: "Status",
      align: "left",
      headerAlign: "left",
      // headerStyle: { width: "100px" },
      formatter: (v) => {
        return (
          <>
            {v === "DRAFT" && <div className={"status-btn draft-btn"}>Draft</div>}
            {v === "PUBLISHED" && <div className={"status-btn published-btn"}>Published</div>}
            {v === "ARCHIVED" && <div className={"status-btn draft-btn"}>Archived</div>}
          </>
        );
      },
    },
    {
      dataField: "@actions",
      text: "",
      align: "right",
      formatter: (v, row) => {
        return (
          <>
            {row.status === "PUBLISHED" || row.status === "ARCHIVED" ? (
              <div style={{ display: "flex", justifyContent: "end", gap: 5 }}>
                {/* <Actions
                  data={row}
                  hasConfigureAction={true}
                  iconSize={ICON_SIZE}
                  onConfigure={(row) => {
                    onSettingItem(row);
                  }}
                  addHintMessage={"setting one"}
                ></Actions>*/}
                <Actions
                  data={row}
                  hasAddAction={true}
                  iconSize={ICON_SIZE}
                  onAdd={(row) => {
                    setCopyWorkflowModel(true);
                    setCurrentCopyWorkflowData(row);
                  }}
                  addHintMessage={"copy one"}
                />
                <Actions
                  data={row}
                  hasPublishAction={true}
                  iconSize={ICON_SIZE}
                  onPublish={(row) => {
                    setPublishWorkflowModel(true);
                    setCurrentPublishWorkflowData(row);
                  }}
                  publishHintMessage={"publish to other organization"}
                />
                <Actions
                  data={row}
                  hasDownloadAction={true}
                  iconSize={ICON_SIZE}
                  onDownload={(row) => {
                    onDownloadItem(row);
                  }}
                />

                {row.status === "PUBLISHED" && (
                  <Actions
                    data={row}
                    hasArchive={true}
                    iconSize={ICON_SIZE}
                    onArchive={(row) => {
                      removePublishTemplate(row);
                    }}
                  />
                )}
              </div>
            ) : (
              <div style={{ display: "flex", justifyContent: "end", gap: 5 }}>
                <Actions
                  data={row}
                  hasEditAction={true}
                  iconSize={ICON_SIZE}
                  editHintMessage="Edit details"
                  onEdit={(row) => {
                    localStorage.setItem("triggerCurrentComId", JSON.stringify(row.communityIds));
                    setEditWorkflowId(row.id);
                  }}
                />
                <Actions
                  data={row}
                  hasDeleteAction={true}
                  iconSize={ICON_SIZE}
                  editHintMessage="Delete workflow details"
                  onDelete={(row) => {
                    onDeleteItem(row);
                  }}
                />
              </div>
            )}
          </>
        );
      },
    },
  ];

  const servicePlanColumns = [
    {
      dataField: "name",
      text: "Name",
      sort: true,
      onSort: onSort,
      align: "left",
      headerStyle: {
        width: "350px",
      },
      formatter: (v, row, isMobile) => {
        return (
          <>
            <div className="">
              <Link
                id={`${isMobile ? "m-" : ""}service-${row.id}`}
                to={path(`/admin/workflowManagement/preview/${row.id}/0`)}
                className="workflowTable-name-Item"
                onClick={() => {
                  localStorage.setItem("triggerCurrentComId", JSON.stringify(row.communityIds));
                }}
              >
                {v}
              </Link>
            </div>
            <Tooltip
              placement="top"
              target={`${isMobile ? "m-" : ""}service-${row.id}`}
              modifiers={[
                {
                  name: "offset",
                  options: { offset: [0, 6] },
                },
                {
                  name: "preventOverflow",
                  options: { boundary: document.body },
                },
              ]}
            >
              Preview
            </Tooltip>
          </>
        );
      },
    },
    {
      dataField: "communityIds",
      text: "Communities",
      sort: false,
      align: "left",
      headerAlign: "left",
      headerClasses: "VendorsList-CommunitiesColHeader",
      formatter: (v, row) => {
        return v ? (
          <>
            <a tabIndex={0} data-toggle="tooltip" id={`servicePlan-${row.id}_com-count`} className="Vendor-not-zero">
              {v.length}
            </a>

            <Tooltip
              trigger="focus"
              placement="top"
              target={`servicePlan-${row.id}_com-count`}
              modifiers={[
                {
                  name: "offset",
                  options: { offset: [0, 6] },
                },
                {
                  name: "preventOverflow",
                  options: { boundary: document.body },
                },
              ]}
            >
              <div style={{ maxHeight: 300, overflow: "scroll" }}>
                {row?.communityNames?.map((name, index) => (
                  <div key={index}>{name}</div>
                ))}
              </div>
            </Tooltip>
          </>
        ) : (
          0
        );
      },
    },
    // 新增 Code 列
    {
      dataField: "code",
      text: "Code",
      align: "left",
      headerAlign: "left",
      headerStyle: { width: "120px", minWidth: "120px" },
      formatter: (v, row) => (
        <div
          style={{
            maxWidth: 100,
            overflow: "hidden",
            textOverflow: "ellipsis",
            whiteSpace: "nowrap",
            display: "inline-block",
            verticalAlign: "middle",
          }}
          id={`code-tooltip-${row.id}`}
          title=""
        >
          {v || "-"}
          <Tooltip
            placement="top"
            target={`code-tooltip-${row.id}`}
            modifiers={[
              { name: "offset", options: { offset: [0, 6] } },
              { name: "preventOverflow", options: { boundary: document.body } },
            ]}
          >
            {v || "-"}
          </Tooltip>
        </div>
      ),
    },
    {
      dataField: "createTime",
      text: "Created",
      sort: true,
      align: "left",
      headerAlign: "left",
      headerClasses: "hide-on-tablet",
      classes: "hide-on-tablet",
      // headerStyle: { width: "80px" },
      onSort: onSort,
      formatter: (v) => v && moment(v).format("MM/DD/YYYY"),
    },
    {
      dataField: "publishTime",
      text: "Published On",
      sort: true,
      align: "left",
      headerAlign: "left",
      headerClasses: "hide-on-tablet",
      classes: "hide-on-tablet",
      onSort: onSort,
      formatter: (v) => v && moment(v).format("MM/DD/YYYY"),
    },
    {
      dataField: "status",
      text: "Status",
      align: "left",
      headerAlign: "left",
      // headerStyle: { width: "100px" },
      formatter: (v) => {
        return (
          <>
            {v === "DRAFT" && <div className={"status-btn draft-btn"}>Draft</div>}
            {v === "PUBLISHED" && <div className={"status-btn published-btn"}>Published</div>}
            {v === "ARCHIVED" && <div className={"status-btn draft-btn"}>Archived</div>}
          </>
        );
      },
    },
    {
      dataField: "@actions",
      text: "",
      // headerStyle: {
      //   width: "120px",
      // },
      align: "right",
      formatter: (v, row) => {
        return (
          <>
            {row.status === "PUBLISHED" || row.status === "ARCHIVED" ? (
              <>
                <div style={{ display: "flex", justifyContent: "end", gap: 5 }}>
                  <Actions
                    data={row}
                    hasAddAction={true}
                    iconSize={ICON_SIZE}
                    onAdd={(row) => {
                      setCopyModel(true);
                      setCurrentCopyData(row);
                    }}
                    addHintMessage={"copy one"}
                  />

                  <Actions
                    data={row}
                    hasPublishAction={true}
                    iconSize={ICON_SIZE}
                    onPublish={(row) => {
                      setPublishWorkflowModel(true);
                      setCurrentPublishWorkflowData(row);
                    }}
                    publishHintMessage={"publish to other organization"}
                  />

                  <Actions
                    data={row}
                    hasDownloadAction={true}
                    iconSize={ICON_SIZE}
                    editHintMessage="download  details"
                    onDownload={(row) => {
                      onDownloadItem(row);
                    }}
                  />

                  {row.status === "PUBLISHED" && (
                    <Actions
                      data={row}
                      hasArchive={true}
                      iconSize={ICON_SIZE}
                      onArchive={(row) => {
                        removeServicePlanPublishTemplate(row);
                      }}
                    />
                  )}
                </div>
              </>
            ) : (
              <div style={{ display: "flex", justifyContent: "end", gap: 5 }}>
                <Actions
                  data={row}
                  hasEditAction={true}
                  iconSize={ICON_SIZE}
                  editHintMessage="Edit details"
                  onEdit={(row) => {
                    localStorage.setItem("triggerCurrentComId", JSON.stringify(row.communityIds));
                    row.isCreateServicePlan = true;
                    onEditItem(row);
                  }}
                />
                <Actions
                  data={row}
                  hasDeleteAction={true}
                  iconSize={ICON_SIZE}
                  editHintMessage="delete workflow details"
                  onDelete={(row) => {
                    onDeleteServicePlan(row);
                  }}
                />
              </div>
            )}
          </>
        );
      },
    },
  ];

  // 已上移并重写，见顶部

  const onClearSearchNameField = () => {
    setSearchName("");
  };

  const onAddWorkflowTemplate = () => {
    console.log("onAddWorkflowTemplate");
    setIsShowAddModal(true);
  };

  const onConfirmAddWorkflow = (data) => {
    localStorage.setItem("workflowData", JSON.stringify(data));
    history.push(path("/admin/workflowManagement/create"));
  };

  const onCancelAddWorkflow = () => {
    setIsShowAddModal(false);
  };

  const downloadPDF = (templateContent, workflowName) => {
    const pdfDocOptions = {
      fontSize: 12,
    };
    const questions = templateContent;

    const savePdf = function () {
      const surveyPdf = new SurveyPDF(questions, pdfDocOptions);

      // 下载的pdf 是否能编辑
      surveyPdf.mode = "display";
      surveyPdf.save(workflowName);
    };
    savePdf();
  };

  useEffect(() => {
    const isWorkflowData = localStorage.getItem("isWorkflow");
    if (isWorkflowData === "true" || (isWorkflow && isWorkflowData === null)) {
      setIsWorkflow(true);
      return;
    }

    setIsWorkflow(false);
  }, [localStorage.getItem("isWorkflow")]);

  useEffect(() => {
    setPage(1);
  }, [isWorkflow]);

  useEffect(() => {
    if (organizationId !== undefined && organizationId !== null) {
      localStorage.setItem("triggerCurrentOrgId", JSON.stringify(organizationId));
    }
    if (communityIds !== undefined && communityIds !== null) {
      localStorage.setItem("triggerCurrentComId", JSON.stringify(communityIds));
    }
  }, [organizationId, communityIds]);

  const confirmSuccess = () => {
    setIsShowSuccessDialog(false);
    setIsLoading(true);
    // Refetch data after success
    if (isWorkflow) {
      refetchWorkflow().finally(() => setIsLoading(false));
    } else {
      refetchServicePlan().finally(() => setIsLoading(false));
    }
  };

  return (
    <DocumentTitle title={"Simply Connect | Admin | workflow"}>
      <>
        <div className={"Workflow"}>
          <UpdateSideBarAction />
          <Breadcrumbs
            className={"margin-bottom-10"}
            items={[
              { title: "Admin", href: "/admin/workflowManagement" },
              {
                title: "Workflow Library",
                href: "/admin/workflowManagement",
                isActive: true,
              },
            ]}
          />

          <WorkflowPrimaryFilter {...primary} className={"margin-bottom-30"} />
          <Table
            hasHover
            hasOptions
            hasPagination
            keyField={"id"}
            noDataText={"No results"}
            title={"Workflow Library"}
            isLoading={false}
            className={"workflow-table-list"}
            containerClass={"workflow-table-list-container"}
            data={currentData}
            pagination={workflowDataListPagination}
            columns={isWorkflow ? SuperAdminColumns : servicePlanColumns}
            columnsMobile={["name", "communityIds"]}
            onRefresh={(num) => {
              setPage(num);
            }}
            renderCaption={(title) => {
              return (
                <>
                  <div className="workflow-table-CaptionHeader">
                    <Row>
                      <Col>
                        <div className="workflow-table-TitleText">{title}</div>
                      </Col>
                      <Col style={{ display: "flex", justifyContent: "flex-end" }}>
                        <Link to={path(`/admin/workflowManagement/categories/${organizationId}`)}>
                          <Button outline color="success" className={"analyticsButton"}>
                            Manage Categories
                          </Button>
                        </Link>

                        <Button color="success" onClick={onAddWorkflowTemplate}>
                          Add Template
                        </Button>
                      </Col>
                    </Row>

                    <Row className="workflow-table-search-box margin-top-10">
                      <Col md={4} lg={4} sm={7} xl={4}>
                        <div className="Workflow-Filters">
                          <Row>
                            <Col>
                              <ButtonGroup className="ButtonGroup">
                                {isWorkflow}
                                <Button
                                  className={isWorkflow ? "btnSuccess" : "btnSecondary"}
                                  onClick={() => {
                                    localStorage.setItem("isWorkflow", "true");
                                    setIsWorkflow(true);
                                  }}
                                >
                                  Workflow
                                </Button>
                                <Button
                                  className={!isWorkflow ? "btnSuccess" : "btnSecondary"}
                                  onClick={() => {
                                    localStorage.setItem("isWorkflow", "false");
                                    setIsWorkflow(false);
                                  }}
                                >
                                  Service Plan
                                </Button>
                              </ButtonGroup>
                            </Col>
                          </Row>
                        </div>
                      </Col>

                      <Col md={4} lg={4} sm={7} xl={4}>
                        <SearchField
                          name="content"
                          value={searchName}
                          placeholder="Search keywords"
                          onClear={onClearSearchNameField}
                          onChange={onChangeSearchNameFilterField}
                        />
                      </Col>
                    </Row>
                  </div>
                </>
              );
            }}
          />
        </div>
        {isShowAddModal && (
          <AddWorkflowTemplate
            isOpen={isShowAddModal}
            onConfirm={onConfirmAddWorkflow}
            onCancel={onCancelAddWorkflow}
            organizations={organizationOptions}
            propOrganizationId={organizationId}
            propCommunityId={communityIds[0]}
            isEdit={isEdit}
            editData={editData}
            setEditData={setEditData}
            setIsEdit={setIsEdit}
            editWorkflow={editWorkflow}
          />
        )}

        {copyWorkflowModel && (
          <CopyWorkflowLibraryTemplate
            isOpen={copyWorkflowModel}
            onClose={() => {
              setCopyWorkflowModel(false);
              setIsShowSuccessDialog(true);
              setSuccessTitle("Workflow template copied successfully.");
            }}
            onCancel={() => {
              setCopyWorkflowModel(false);
            }}
            organizations={organizationOptions}
            propOrganizationId={organizationId}
            propCommunityId={communityIds[0]}
            currentCopyData={currentCopyWorkflowData}
          />
        )}

        {copyModel && (
          <CopyCustomTemplate
            isOpen={copyModel}
            onCancel={() => setCopyModel(false)}
            onClose={() => {
              setCopyModel(false);
              setIsShowSuccessDialog(true);
              setSuccessTitle("Service plan template copied successfully.");
            }}
            organizations={organizationOptions}
            propCommunityId={communityIds}
            propOrganizationId={organizationId}
            currentCopyData={currentCopyData}
          />
        )}

        {publishWorkflowModel && (
          <PublishWorkflowToOtherOrganization
            isOpen={publishWorkflowModel}
            onClose={() => {
              setPublishWorkflowModel(false);
              setIsShowSuccessDialog(true);
              setSuccessTitle(
                `${!isWorkflow ? "Service Plan" : "Workflow"} published to other organization successfully.`,
              );
            }}
            onCancel={() => {
              setPublishWorkflowModel(false);
            }}
            currentWorkflowData={currentPublishWorkflowData}
            isServicePlan={!isWorkflow}
          />
        )}

        {errorMessage && (
          <ErrorDialog
            isOpen
            title={errorMessage}
            buttons={[
              {
                text: "Close",
                onClick: () => setErrorMessage(""),
              },
            ]}
          />
        )}
        <SuccessDialog
          isOpen={isShowSuccessDialog}
          title={successTitle}
          buttons={[{ text: "Ok", onClick: () => confirmSuccess() }]}
        />
        {shouldShowGlobalLoading && <Loader isCentered hasBackdrop />}
      </>
    </DocumentTitle>
  );
};

export default WorkflowManager;
