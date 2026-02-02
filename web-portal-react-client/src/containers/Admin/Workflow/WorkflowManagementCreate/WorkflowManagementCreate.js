import React, { useEffect, useState, useRef } from "react";
import { Loader } from "../../../../components";
import adminWorkflowCreateService from "../../../../services/AdminWorkflowCreateService";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import Modal from "../../../../components/Modal/Modal";
import TextField from "../../../../components/Form/TextField/TextField";
import Button from "../../../../components/buttons/Button/Button";
import { ErrorDialog, SuccessDialog } from "../../../../components/dialogs";
import { useHistory } from "react-router-dom";
import { path } from "lib/utils/ContextUtils";
import initSurveyCustomComponent, {
  customComponents,
  customComponentsNotRequire,
  fieldDisplayNames,
  oldPropertyList,
  oldPropertyListNotShow,
  onlyList,
  propertyList,
  uninstallSurveyCustomComponent,
} from "./CustomComponent/initSurveyCustomComponent";
import { UpdateSideBarAction } from "../../../../actions/admin";
import { ReactComponent as TemporaryStorageImg } from "images/workflow/temporaryStorage.svg";

import { SurveyCreator, SurveyCreatorComponent } from "survey-creator-react";
import { Action, ComputedUpdater, Serializer, setLicenseKey, SvgRegistry } from "survey-core";
import ReactDOMServer from "react-dom/server";

import "survey-core/defaultV2.min.css";
import "survey-creator-core/survey-creator-core.min.css";
import "./survey-create.css";

import "./WorkflowManagementCreate.scss";

import { EchoQuestions, OrdinaryQuestions, ServicePlanModelQuestions, TriggerQuestions } from "./surveyToobox";
import { saveSurvey, openDatabase, deleteSurvey, getSurvey } from "../../../../lib/utils/surveyJson";

const svg = ReactDOMServer.renderToString(<TemporaryStorageImg />);
SvgRegistry.registerIconFromSvg("icon-defaultfile", svg);

setLicenseKey(process.env.REACT_APP_SURVEY_LICENSEKEY);

const WorkflowManagementCreate = () => {
  const history = useHistory();
  const queryClient = useQueryClient();
  const [errorMessage, setErrorMessage] = useState();
  const [isShowSuccessDialog, setIsShowSuccessDialog] = useState(false);
  const [successTitle, setSuccessTitle] = useState("");
  const [showCodeModal, setShowCodeModal] = useState(false);
  const [newCode, setNewCode] = useState("");
  const [codeModalError, setCodeModalError] = useState("");
  const [lastSaveStatus, setLastSaveStatus] = useState(undefined);

  // Create Workflow Mutation
  const createWorkflowMutation = useMutation({
    mutationFn: (data) => adminWorkflowCreateService.createWorkflow(data),
    onSuccess: (res) => {
      if (res.success) {
        openDatabase().then((db) => {
          return deleteSurvey(db);
        });

        setIsShowSuccessDialog(true);
        setSuccessTitle(
          lastSaveStatus === "DRAFT"
            ? "The workflow template has been successfully saved as a draft."
            : "The workflow template published successfully.",
        );
      }
      queryClient.invalidateQueries({ queryKey: ["workflow-templates"] });
    },
    onError: (error) => {
      if (error?.message?.toLowerCase().includes("template code already existed")) {
        setShowCodeModal(true);
        setNewCode("");
        setCodeModalError("");
      } else {
        setErrorMessage(error?.message);
      }
    },
  });

  // Save Service Plan Mutation
  const saveServicePlanMutation = useMutation({
    mutationFn: (serviceInfo) => adminWorkflowCreateService.saveServicePlanTemplate(serviceInfo),
    onSuccess: () => {
      setIsShowSuccessDialog(true);
      setSuccessTitle(
        lastSaveStatus === "PUBLISHED"
          ? "Service plan template published successfully."
          : "Service plan template has been successfully saved as a draft.",
      );
      openDatabase().then((db) => {
        return deleteSurvey(db);
      });
      queryClient.invalidateQueries({ queryKey: ["service-plan-templates"] });
    },
    onError: (error) => {
      setErrorMessage(error?.message);
    },
  });

  // 用 useRef 保留 SurveyCreator 实例，仅初始化一次
  const creatorRef = useRef(null);
  const [creatorInitialized, setCreatorInitialized] = useState(false);
  const addData = JSON.parse(window.localStorage.getItem("workflowData") || "{}");

  // 用于保证每次加载最新survey内容，无论是新建还是编辑
  function loadWorkflowData(newWorkflowData) {
    if (!creatorRef.current) return;
    if (typeof newWorkflowData === "string") {
      creatorRef.current.text = newWorkflowData;
    } else if (typeof newWorkflowData === "object" && newWorkflowData !== null) {
      creatorRef.current.JSON = newWorkflowData;
    }
  }

  useEffect(() => {
    if (!creatorRef.current) {
      const creatorOptions = {
        showLogicTab: false,
        isAutoSave: true,
        allowEditExpressionsInTextEditor: true,
        pageEditMode: "single",
        previewOrientation: "landscape",
        activeTab: ["designer", "test", "editor", "logic"],
        showJSONEditorTab: true,
        showObjectTitles: false,
        showSaveButton: true,
        showSimulatorInPreviewTab: false,
        showTranslationTab: false,
        showSurveyTitle: false,
        showDesignerTab: true,
        showDefaultLanguageInPreviewTab: false,
        showThemeTab: false,
        showInvisibleElementsInPreviewTab: !addData.isCreateServicePlan,
      };
      creatorRef.current = new SurveyCreator(creatorOptions);
      setCreatorInitialized(true);
    }
  }, []);

  useEffect(() => {
    if (!creatorInitialized) return;
    const creator = creatorRef.current;

    initSurveyCustomComponent();

    creator.toolbox.defineCategories(
      [
        {
          category: "Service Plan Templates",
          items: ServicePlanModelQuestions,
        },
        {
          category: "Auto-Filled Fields",
          items: EchoQuestions,
        },
        {
          category: "Manual Fields",
          items: OrdinaryQuestions,
        },
        {
          category: "Trigger Fields",
          items: TriggerQuestions,
        },
      ],
      true,
    );

    creator.toolbox.allowExpandMultipleCategories = true;
    creator.toolbox.showCategoryTitles = true;
    creator.toolbox.forceCompact = false;

    creator.toolbox.showTitleOnCategoryChange = false;

    creator.onQuestionAdded.add((sender, options) => {
      onlyList.forEach((field) => {
        if (fieldDisplayNames.hasOwnProperty(field)) {
          handleQuestionAdded(sender, options, field, fieldDisplayNames[field].title, fieldDisplayNames[field].name);
        }
      });
    });

    creator.onShowingProperty.add(function (sender, options) {
      if (onlyList.includes(options.obj.getType())) {
        options.canShow = propertyList[options.obj.getType()].includes(options.property.name);
      }

      if (customComponents.includes(options.obj.getType())) {
        options.canShow = propertyList[options.obj.getType()].includes(options.property.name);
      }

      if (oldPropertyList.includes(options.obj.getType())) {
        options.canShow = !oldPropertyListNotShow[options.obj.getType()].includes(options.property.name);
      }
    });

    Serializer.getProperty("survey", "widthMode").defaultValue = "responsive";
    Serializer.getProperty("survey", "widthMode").visible = false;

    creator.onElementAllowOperations.add((_, options) => {
      if (onlyList.includes(options.obj.getType())) {
        options.allowCopy = false;
        options.allowChangeRequired = false;
      }

      if (customComponentsNotRequire.includes(options.obj.getType())) {
        options.allowChangeRequired = false;
      }
    });

    // 通过 loadWorkflowData 保证每次 survey 内容都是最新的
    openDatabase()
      .then((db) => {
        return getSurvey(db);
      })
      .then((surveyData) => {
        if (surveyData) {
          loadWorkflowData(surveyData);
        }
      });

    creator.saveSurveyFunc = () => {
      openDatabase().then((db) => {
        return saveSurvey(db, creator.text);
      });
    };

    // 按需添加 toolbar 按钮和 footer
    for (let i = creator.toolbarItems.length - 1; i >= 0; i--) {
      const item = creator.toolbarItems[i];

      if (item.id === "svd-settings") {
        creator.toolbarItems.splice(i, 1);
      }
    }

    if (!addData.isCreateServicePlan) {
      // Add the custom button to the top toolbar
      creator.toolbarItems.push(temporaryStorageWorkflowAction);
      creator.toolbarItems.push(saveWorkflowAction);

      // Add the custom button to the bottom toolbar (visible only on mobile devices)
      creator.footerToolbar.actions.push(temporaryStorageWorkflowAction);
      creator.footerToolbar.actions.push(saveWorkflowAction);
    } else {
      //   create service plan

      creator.toolbarItems.push(servicePlanDraftAction);
      creator.toolbarItems.push(servicePlanSaveAction);

      creator.footerToolbar.actions.push(servicePlanDraftAction);
      creator.footerToolbar.actions.push(servicePlanSaveAction);
    }

    return () => {
      uninstallSurveyCustomComponent();
    };
  }, [creatorInitialized]);

  function handleQuestionAdded(sender, options, type, title, name) {
    if (options.question.getType() === type) {
      // Set the name of the question
      options.question.title = title;
      options.question.name = name;
      const allQuestions = sender.survey.getAllQuestions(false, false, true);

      // Filter out all questions of the specified type
      const specificTypeQuestions = allQuestions.filter((question) => question.getType() === type);

      // Check if the number of specified type problems is greater than 1
      if (specificTypeQuestions.length > 1) {
        // Delete the currently added question
        options.question.delete();
      }
    }
  }

  function saveWorkflowFc(status) {
    // 新增：记住最后一次保存的status，便于code重新提交
    setLastSaveStatus(status);
    const addData = JSON.parse(window.localStorage.getItem("workflowData") || "{}");
    const creator = creatorRef.current;
    // Invalidate workflowDetail query
    if (addData.id) {
      queryClient.invalidateQueries({ queryKey: ["workflowDetail", addData.id] });
    }
    saveWorkflow({
      ...addData,
      status,
      content: JSON.stringify({
        logoPosition: "right",
        widthMode: "responsive",
        code: addData.code,
        pages: [
          {
            name: "page1",
            ...JSON.parse(creator.text), // 解析为对象后再展开
          },
        ],
      }),
    });
  }

  function saveServicePlanFc(serviceInfo) {
    const creator = creatorRef.current;
    const body = {
      content: JSON.stringify({
        logoPosition: "right",
        widthMode: "responsive",
        pages: [
          {
            name: "page1",
            ...JSON.parse(creator.text), // 解析为对象后再展开
          },
        ],
      }),
      id: serviceInfo.id,
      name: serviceInfo.servicePlanName,
      communityIds: serviceInfo.communityIds,
      organizationId: serviceInfo.organizationId,
      status: serviceInfo.status,
    };

    saveServicePlanMutation.mutate(body);
  }

  function saveServicePlan(status) {
    const addData = JSON.parse(window.localStorage.getItem("workflowData") || "{}");

    // Invalidate workflowDetail query
    if (addData.id) {
      queryClient.invalidateQueries({ queryKey: ["workflowDetail", addData.id] });
    }

    const serviceInfo = {
      id: addData.id,
      organizationId: addData.organizationId,
      communityIds: addData.communityIds,
      servicePlanName: addData.id ? addData.name : addData.servicePlanName,
      status,
    };

    saveServicePlanFc(serviceInfo);
  }

  // 创建 action 需要在外部定义，这样 effect 内可以直接引用
  const saveWorkflowAction = new Action({
    id: "toolbox-saveWorkflow",
    iconName: "icon-save",
    title: "Save Workflow",
    visible: new ComputedUpdater(() => {
      const creator = creatorRef.current;
      return creator && creator.activeTab === "designer";
    }),
    showTitle: false,
    enabled: true,
    action: function () {
      saveWorkflowFc("PUBLISHED");
    },
  });

  const temporaryStorageWorkflowAction = new Action({
    id: "toolbox-temporaryStorage",
    iconName: "icon-defaultfile",
    title: "Temporary Storage Workflow",
    visible: new ComputedUpdater(() => {
      const creator = creatorRef.current;
      return creator && creator.activeTab === "designer";
    }),
    showTitle: false,
    enabled: true,
    action: function () {
      saveWorkflowFc("DRAFT");
    },
  });

  const servicePlanDraftAction = new Action({
    id: "toolbox-servicePlanDraft",
    iconName: "icon-defaultfile",
    title: "Draft Service Plan",
    visible: new ComputedUpdater(() => {
      const creator = creatorRef.current;
      return creator && creator.activeTab === "designer";
    }),
    showTitle: false,
    enabled: true,
    action: function () {
      saveServicePlan("DRAFT");
    },
  });

  const servicePlanSaveAction = new Action({
    id: "toolbox-servicePlanSave",
    iconName: "icon-save",
    title: "Save Service Plan",
    visible: new ComputedUpdater(() => {
      const creator = creatorRef.current;
      return creator && creator.activeTab === "designer";
    }),
    showTitle: false,
    enabled: true,
    action: function () {
      saveServicePlan("PUBLISHED");
    },
  });

  const saveWorkflow = (data, status) => {
    createWorkflowMutation.mutate(data);
  };

  const confirmSuccess = () => {
    setIsShowSuccessDialog(false);
    window.localStorage.removeItem("workflowData");
    history.push(path("/admin/workflowManagement"));
  };

  return (
    <>
      {(createWorkflowMutation.isPending || saveServicePlanMutation.isPending) && <Loader isCentered hasBackdrop />}
      <UpdateSideBarAction />
      {creatorInitialized && <SurveyCreatorComponent creator={creatorRef.current} />}

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

      {/* code 冲突输入弹窗 */}
      {showCodeModal && (
        <Modal
          isOpen
          title="Template code already exists. Please input a new code"
          className="reTemplateCode"
          isCentered={true}
          hasFooter={true}
          hasCloseBtn={true}
          onClose={() => setShowCodeModal(false)}
          renderFooter={() => (
            <div style={{ display: "flex", justifyContent: "flex-end", gap: 8 }}>
              <Button outline color="success" onClick={() => setShowCodeModal(false)}>
                Cancel
              </Button>
              <Button
                color="success"
                onClick={() => {
                  if (!newCode) {
                    setCodeModalError("Please input a code");
                    return;
                  }
                  // 更新本地 code
                  const current = JSON.parse(window.localStorage.getItem("workflowData") || "{}");
                  current.code = newCode;
                  window.localStorage.setItem("workflowData", JSON.stringify(current));
                  setShowCodeModal(false);
                  setCodeModalError("");
                  // 再次提交
                  saveWorkflowFc(lastSaveStatus);
                }}
              >
                Confirm
              </Button>
            </div>
          )}
        >
          <div style={{ padding: 24 }}>
            <div style={{ marginBottom: 10, color: "#888", fontSize: 14 }}>
              <span>Current code: </span>
              <span style={{ fontWeight: "bold" }}>
                {JSON.parse(window.localStorage.getItem("workflowData") || "{}").code || ""}
              </span>
            </div>
            <TextField
              name="workflowCode"
              label="Workflow Template Code*"
              value={newCode}
              hasError={!!codeModalError}
              errorText={codeModalError}
              onChange={(fields, value) => {
                setNewCode(value.replace(/\./g, ""));
                setCodeModalError("");
              }}
            />
          </div>
        </Modal>
      )}

      <SuccessDialog
        isOpen={isShowSuccessDialog}
        title={successTitle}
        buttons={[{ text: "Ok", onClick: () => confirmSuccess() }]}
      />
    </>
  );
};

export default WorkflowManagementCreate;
