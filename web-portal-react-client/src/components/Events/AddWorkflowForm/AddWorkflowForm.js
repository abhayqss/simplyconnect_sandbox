import { ErrorViewer, Loader } from "../../index";
import { Button, Col, Form, Row } from "reactstrap";
import { CheckboxField, RadioGroupField, SelectField, SelectFieldGroupTree } from "../../Form";
import React, { useEffect, useMemo, useState } from "react";
import { useScrollable } from "../../../hooks/common";
import "./AddWorkflowForm.scss";
import ClientEditor from "../../../containers/Clients/Clients/ClientEditor/ClientEditor";
import { first, isUnary } from "lib/utils/ArrayUtils";
import workflowService from "services/WorkflowService";
import service from "services/DirectoryService";
import { useHistory } from "react-router-dom";
import { path } from "lib/utils/ContextUtils";
import careTeamMemberService from "../../../services/CareTeamMemberService";
import { WarningDialog } from "../../dialogs";

const AddWorkflowForm = (props) => {
  const { Scrollable, scroll } = useScrollable();
  const history = useHistory();
  const { onConfirm, organizationId, onClose, communityIds, adminClientId } = props;

  // 分离不同的 loading 状态
  const [workflowData, setWorkflowData] = useState([]);
  const [isFetchingWorkflow, setIsFetchingWorkflow] = useState(false);
  const [isFetchingCareTeam, setIsFetchingCareTeam] = useState(false);
  const [isFetchingSubmit, setIsFetchingSubmit] = useState(false);
  let [nodeIds, setNodeIds] = useState([]);
  const [clientOptions, setClientOptions] = useState([]);
  const [defaultClientOptions, setClientDefaultOptions] = useState([]);
  const [isEditorOpen, setIsEditorOpen] = useState(false);

  // 计算总的 loading 状态
  const isFetching = isFetchingWorkflow || isFetchingCareTeam || isFetchingSubmit;
  const [workflowIds, setWorkflowIds] = useState([]);
  const [hasWorkflowError, setHasWorkflowError] = useState(false);
  const [clientId, setClientId] = useState(null);
  const [hasClientError, setHasClientError] = useState(null);
  const [careTeamContactId, setCareTeamContactId] = useState();
  const [careTeamContacts, setCareTeamContacts] = useState([]);
  const [fillCareTeamContactId, setFillCareTeamContactId] = useState();
  const [fillCareTeamId, setFillCareTeamId] = useState();
  const [clientCareTeamOptions, setClientCareTeamOptions] = useState([]);
  const [fillType, setFillType] = useState(null);
  const [hasFillTypeError, setHasFillTypeError] = useState(null);
  const [autoNewEvent, setAutoNewEvent] = useState(false);
  const [isCanShowHousingVoucher, setIsCanShowHousingVoucher] = useState(false);
  const [searchWorkflowName, setSearchWorkflowName] = useState("");
  const [searchClientName, setSearchClientName] = useState(null);
  const [isAddClient, setIsAddClient] = useState(false);
  const [hasError, setHasError] = useState(false);
  const [errorMessage, setErrorMessage] = useState();
  const [isWarningDialogShow, setIsWarningDialogShow] = useState(false);
  const [warningDialogError, setWarningDialogError] = useState("");

  const fillTypeOptions = useMemo(() => {
    // const isCareTeamMemberDisabled = isEmpty(primaryContacts?.data)
    return [
      { value: "FILLNOW", label: "Fill now" },
      {
        value: "SENDOUT",
        label: "Send out (Care team)",
      },
    ];
  }, []);

  // 将 transformDataOptions 移到 useMemo 中
  const transformDataOptions = (dataList) => {
    return dataList?.map((data) => {
      return {
        value: "CATEGORY" + data.id,
        text: data.categoryName,
        hasChild: data?.templateList?.length !== 0,
        isParent: true,
        childrenOptions: data?.templateList?.map((item) => {
          return {
            id: item.id,
            categoryId: "CATEGORY" + data.id,
            value: data.id + "NODE" + item.id,
            text: item.name,
            title: item.code,
            isChild: true,
          };
        }),
      };
    });
  };

  // 使用 useMemo 计算 workflowOptions
  const workflowOptions = useMemo(() => {
    if (!workflowData?.length) return [];
    return transformDataOptions(workflowData);
  }, [workflowData]);

  useEffect(() => {
    getClientOptions({
      filter: {
        recordStatuses: "ACTIVE",

        sort: "fullName,asc",
      },
      organizationId,
      communityIds,
    });
  }, [organizationId, communityIds]);

  useEffect(() => {
    if (isAddClient) {
      getClientOptions({
        filter: {
          recordStatuses: "ACTIVE",
          organizationId,
          communityIds,
        },
        page: 1,
        size: 99999,
      });
    }
  }, [isAddClient]);

  useEffect(() => {
    getWorkflowCategoryOptions({
      organizationId,
      communityIds: communityIds,
      name: searchWorkflowName,
    });
  }, [organizationId, communityIds, searchWorkflowName]);

  // 简化后的 getWorkflowCategoryOptions - 使用独立的 loading 状态
  const getWorkflowCategoryOptions = (params) => {
    setIsFetchingWorkflow(true);

    workflowService
      .findWorkflowCategoryOptions(params)
      .then((res) => {
        if (res.success) {
          // 只设置原始数据，workflowOptions 会通过 useMemo 自动计算
          setWorkflowData(res.data);
        }
      })
      .catch((error) => {
        console.error("❌ Workflow loading failed:", error);
      })
      .finally(() => {
        setIsFetchingWorkflow(false);
      });
  };

  const getClientOptions = (params) => {
    setIsAddClient(false);
    service.findClients(params).then((res) => {
      if (res.success) {
        const data = res?.data?.map((item) => ({
          text: item.fullName,
          value: item.id,
          isDisabled: item.hieConsentPolicyName !== "OPT_IN",
        }));
        setClientDefaultOptions(data);
        setClientOptions(data);
      }
    });
  };

  const findIdsByConditions = (conditions) => {
    let nodeIds = new Set();
    conditions.forEach((condition) => {
      const categoryMatch = condition.match(/CATEGORY(\d+)/);
      const nodeMatch = condition.match(/NODE(\d+)/);

      if (categoryMatch) {
        const categoryId = parseInt(categoryMatch[1], 10);
        const data001 = workflowData?.find((item) => item.id === categoryId);
        const idsFromCategory = data001?.templateList?.map((item) => item.id) || [];
        idsFromCategory.forEach((id) => nodeIds.add(id));
      } else if (nodeMatch) {
        const nodeId = parseInt(nodeMatch[1], 10);
        nodeIds.add(nodeId);
      }
    });

    return Array.from(nodeIds); // 将 Set 转换回数组
  };

  const changeWorkflowIds = (name, value) => {
    setWorkflowIds(value);
    if (!!value) {
      setHasWorkflowError(false);
    } else {
      setHasWorkflowError(true);
    }
  };

  const changeClientId = (name, value) => {
    setClientId(value);
    setClientOptions(defaultClientOptions);
    if (value) {
      setHasClientError(false);
    } else {
      setHasClientError(true);
    }
  };

  const getListValue = (value) => {
    return value.map((item) => {
      let matches = item?.match(/(.*?)&&&&(.*)/);
      return {
        fillCareTeamId: matches[2],
        fillCareTeamContactId: matches[1],
      };
    });
  };

  const changeFillCareTeamContactId = (name, value) => {
    let data = getListValue(value);
    setCareTeamContacts(_.cloneDeep(data));
    setCareTeamContactId(value);
  };

  const onAddNewClient = () => {
    setIsEditorOpen(true);
  };

  const changeEventValue = (name, value) => {
    setAutoNewEvent(value);
  };

  const onChangeFillType = (name, value) => {
    setFillType(value);
    setCareTeamContactId(null);
    setCareTeamContacts([]);
    if (value) {
      setHasFillTypeError(false);
      if (value === "FILLNOW") {
        setCareTeamContactId(null);
        setFillCareTeamId(null);
        setFillCareTeamContactId(null);
      }
    } else {
      setHasFillTypeError(true);
    }
  };

  const onCloseEditor = () => {
    setIsEditorOpen(false);
    setIsAddClient(true);
  };

  const onSaveSuccess = (isNew) => {
    onCloseEditor();
  };

  const onFormSubmit = () => {
    if (adminClientId) {
      // admin role from client
      localStorage.setItem("currentAddWorkflowClientId", adminClientId);
      setHasClientError(false);
      if (workflowIds === null || workflowIds === undefined || workflowIds.length === 0) {
        setHasWorkflowError(true);
      } else if (!fillType) {
        setHasFillTypeError(true);
      } else {
        let nodeData = findIdsByConditions(workflowIds);
        if (nodeData.length) {
          setIsFetchingSubmit(true);
          if (careTeamContacts.length) {
            let data = {
              fillType: fillType,
              clientId: adminClientId,
              workflowIds: nodeData,
              autoNewEvent: autoNewEvent, // autoNewEvent
              careTeamContacts,
            };
            workflowService
              .addWorkflowForClient(data)
              .then((res) => {
                if (res.success) {
                  setIsFetchingSubmit(false);
                  if (fillType === "FILLNOW") {
                    const id = res.data;
                    history.push(
                      path(`/clients/${adminClientId}/workflowDetail/${id[0]}/${adminClientId}?FN=true&FC=true`),
                    );
                  } else {
                    onConfirm();
                  }
                }
              })
              .catch((error) => {
                setIsFetchingSubmit(false);
                setIsWarningDialogShow(false);
                setWarningDialogError(error.message);
              });
          } else {
            let data = {
              fillType: fillType,
              clientId: adminClientId,
              workflowIds: nodeData,
              autoNewEvent: autoNewEvent, // autoNewEvent
            };
            workflowService
              .addWorkflowForClient(data)
              .then((res) => {
                if (res.success) {
                  setIsFetchingSubmit(false);
                  if (fillType === "FILLNOW") {
                    const id = res.data;
                    history.push(
                      path(`/clients/${adminClientId}/workflowDetail/${id[0]}/${adminClientId}?FN=true&FC=true`),
                    );
                  } else {
                    onConfirm();
                  }
                }
              })
              .catch((e) => {
                setIsFetchingSubmit(false);
                setIsWarningDialogShow(false);
                setWarningDialogError(error.message);
              });
          }
        }
      }
    } else {
      if (workflowIds === null || workflowIds === undefined || workflowIds.length === 0) {
        setHasWorkflowError(true);
      } else if (!clientId) {
        setHasClientError(true);
      } else if (!fillType) {
        setHasFillTypeError(true);
      } else {
        localStorage.setItem("currentAddWorkflowClientId", clientId);
        setIsFetchingSubmit(true);
        let nodeData = findIdsByConditions(workflowIds);
        if (nodeData.length) {
          if (careTeamContacts.length) {
            const data = {
              fillType: fillType,
              clientId: clientId,
              workflowIds: nodeData,
              autoNewEvent: autoNewEvent, // autoNewEvent
              careTeamContacts,
            };
            return workflowService
              .addWorkflowForClient(data)
              .then((res) => {
                if (res.success) {
                  setIsFetchingSubmit(false);
                  if (fillType === "FILLNOW") {
                    const id = res.data;
                    history.push(path(`/admin-events/workflowDetail/${id[0]}/null/${clientId}`));
                  } else {
                    onConfirm();
                  }
                }
              })
              .catch((error) => {
                setIsFetchingSubmit(false);
                setErrorMessage(error);
                setHasError(true);
              });
          } else {
            const data = {
              fillType: fillType,
              clientId: clientId,
              workflowIds: nodeData,
              autoNewEvent: autoNewEvent, // autoNewEvent
            };
            return workflowService
              .addWorkflowForClient(data)
              .then((res) => {
                setIsFetchingSubmit(false);
                if (res.success) {
                  if (fillType === "FILLNOW") {
                    const id = res.data;
                    history.push(path(`/admin-events/workflowDetail/${id[0]}/null/${clientId}`));
                  } else {
                    onConfirm();
                  }
                }
              })
              .catch((error) => {
                setIsFetchingSubmit(false);
                setErrorMessage(error);
                setHasError(true);
              });
          }
        }
      }
    }
  };

  const onClearSearchText = () => {
    setSearchWorkflowName("");
  };

  const onChangeSearchText = (value, searchText) => {
    setSearchWorkflowName(searchText);
  };

  const onClearClientSearchText = () => {
    setSearchClientName("");
  };

  const onChangeClientSearchText = (value, searchText) => {
    setSearchClientName(searchText);
    if (searchText) {
      const filteredData = clientOptions?.filter((item) => item.text?.includes(searchText));
      setClientOptions(filteredData);
    } else {
      setClientOptions(defaultClientOptions);
    }
  };

  const formatCareTeamOption = (data) => {
    return data.map((item) => {
      return {
        value: item.employeeId + "&&&&" + item.id,
        text: `${item.contactName} (${item.roleName})`,
      };
    });
  };

  const getDefaultMemberList = (params) => {
    setIsFetchingCareTeam(true);

    careTeamMemberService
      .find(params)
      .then((res) => {
        if (res.success) {
          const { data } = res;
          const option = formatCareTeamOption(data);
          setClientCareTeamOptions(option);
        }
      })
      .catch((error) => {
        console.error("❌ Care team loading failed:", error);
      })
      .finally(() => {
        setIsFetchingCareTeam(false);
      });
  };

  useEffect(() => {
    adminClientId && getDefaultMemberList({ clientId: adminClientId, affiliation: "REGULAR" });
    clientId && getDefaultMemberList({ page: 1, size: 999, clientId: clientId, affiliation: "REGULAR" });
  }, [clientId, adminClientId]);

  const closeErrorDialog = () => {
    setHasError(false);
  };

  return (
    <>
      <Form className="WorkflowForm is-invalid" onSubmit={onFormSubmit}>
        {isFetching && <Loader style={{ position: "fixed" }} hasBackdrop />}
        <Scrollable style={{ flex: 1 }}>
          <div className="WorkflowForm-Section">
            <Row>
              <Col lg={12} md={12}>
                <SelectFieldGroupTree
                  name="workflowIds"
                  value={workflowIds}
                  label="Workflow*"
                  optionType={"checkbox"}
                  isSectioned={false}
                  isMultiple={true}
                  hasAllOption={false}
                  hasSearchBox={false}
                  options={workflowOptions}
                  hasError={hasWorkflowError}
                  className="VendorsForm-TextField"
                  errorText={hasWorkflowError ? "Please fill in the required field" : ""}
                  onChange={changeWorkflowIds}
                  onClearSearchText={onClearSearchText}
                  onChangeSearchText={onChangeSearchText}
                />
              </Col>
            </Row>
            {!adminClientId && (
              <Row>
                <Col lg={12} md={12}>
                  <div className="second-line">
                    <SelectField
                      hasSearchBox={true}
                      name="clientId"
                      isMultiple={false}
                      hasAutoScroll={true}
                      value={clientId}
                      label="Client*"
                      options={clientOptions}
                      hasError={hasClientError}
                      className="workForm-clientId"
                      errorText={hasClientError ? "Please fill in the required field" : ""}
                      onChange={changeClientId}
                      onClearSearchText={onClearClientSearchText}
                      onChangeSearchText={onChangeClientSearchText}
                    />
                    <Button color={"success"} className={"mt-10px add-btn"} onClick={onAddNewClient}>
                      Add New Client
                    </Button>
                  </div>
                </Col>
              </Row>
            )}
            <Row>
              <Col lg={8} md={12}>
                <RadioGroupField
                  view="row"
                  name="fillType"
                  selected={fillType}
                  title="Fill Type*"
                  options={fillTypeOptions}
                  hasError={hasFillTypeError}
                  onChange={onChangeFillType}
                  errorText={hasFillTypeError ? "Please fill in the required field" : ""}
                  className=""
                />
              </Col>

              <Col lg={4} md={12}>
                <div className="RadioGroupField-Title form-label">Event</div>
                <CheckboxField
                  type="text"
                  name="autoNewEvent"
                  value={autoNewEvent}
                  label="Auto New Event"
                  onChange={changeEventValue}
                />
              </Col>
            </Row>
            <Row>
              <Col>
                <SelectField
                  hasSearchBox={true}
                  name="careTeamIds"
                  isMultiple={true}
                  hasAutoScroll={true}
                  value={careTeamContactId}
                  label="Care Team"
                  options={clientCareTeamOptions}
                  hasError={false}
                  className="workForm-clientId"
                  onChange={changeFillCareTeamContactId}
                  isDisabled={clientCareTeamOptions.length === 0 || fillType === "FILLNOW"}
                  onClearSearchText={onClearClientSearchText}
                  onChangeSearchText={onChangeClientSearchText}
                />
              </Col>
            </Row>
          </div>
        </Scrollable>

        <ClientEditor
          isOpen={isEditorOpen}
          organizationId={organizationId}
          communityId={isUnary(communityIds) ? first(communityIds) : null}
          isCanShowHousingVoucher={isCanShowHousingVoucher}
          onClose={onCloseEditor}
          onSaveSuccess={onSaveSuccess}
          isFromWorkflow={true}
        />
        <div className={"workflow-modal-bottom-btn"}>
          <Button onClick={onClose} color="success" outline className={"workflow-modal-bottom-btn-cancel"}>
            Cancel
          </Button>

          <Button color="success" className={"workflow-modal-bottom-btn-confirm"} onClick={onFormSubmit}>
            Confirm
          </Button>
        </div>
        {hasError && <ErrorViewer isOpen error={errorMessage} onClose={closeErrorDialog} />}
      </Form>
      {isWarningDialogShow && (
        <WarningDialog
          isOpen={isWarningDialogShow}
          title={warningDialogError}
          buttons={[
            {
              text: "OK",
              onClick: () => onConfirm(),
            },
          ]}
        />
      )}
    </>
  );
};

export default AddWorkflowForm;
