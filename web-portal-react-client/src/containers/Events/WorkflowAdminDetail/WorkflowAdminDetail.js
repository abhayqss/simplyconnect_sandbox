import "./WorkflowAdminDetail.scss";
import { useHistory, useLocation, useParams } from "react-router-dom";
import clientWorkflowService from "services/ClientWorkflowService";
import WorkflowService from "services/WorkflowService";
import ServicePlanService from "services/AdminWorkflowCreateService";

import React, { useCallback, useEffect, useState } from "react";
import Footer from "components/Footer/Footer";
import { SuccessDialog, WarningDialog } from "components/dialogs";
import { Loader, Table } from "components";
import waringImg from "images/workflow/waring.svg";
import { useSideBarUpdate } from "hooks/business/client";
import { questionList, ScoringSummaryData } from "../../Clients/Clients/Assessments/AssessmentEditor/AssessmentEditor";
import { TextField } from "../../../components/Form";
import { Button } from "reactstrap";
import { ReactComponent as DownLoadBtn } from "images/workflow/downloadRadios.svg";

import { SurveyPDF } from "survey-pdf";
import { Model } from "survey-core";
import { Survey } from "survey-react-ui";
import "survey-core/defaultV2.min.css";
import { PlainLight } from "survey-core/themes";

import initSurveyCustomComponent, {
  uninstallSurveyCustomComponent,
} from "../../Admin/Workflow/WorkflowManagementCreate/CustomComponent/initSurveyCustomComponent";
import editImg from "../../../images/workflow/edit.svg";
import deleteImg from "../../../images/workflow/delete-icon.svg";

import _, { debounce } from "lodash";

const WorkflowAdminDetail = (props) => {
  const { workflowId, clientId, questionId, fillClientId } = useParams();

  const history = useHistory();
  const location = useLocation();
  const searchParams = new URLSearchParams(location.search);
  const isFromClients = searchParams.get("FC") !== "false" || false;
  // 模板内容
  const [templateContent, setTemplateContent] = useState("");

  // 获取的当前workflow 的所有信息
  const [detailData, setDetailData] = useState(null);
  const [surveyAnswerData, setSurveyAnswerData] = useState(null);
  const [triggerList, setTriggerList] = useState([]);

  const [isFetching, setIsFetching] = useState(false);
  // 是否保存为草稿
  const [isSaveDraft, setIsSaveDraft] = useState(false);

  // 保存操作类型：'draft' 表示暂存并关闭，'draftAndContinue' 表示暂存不关闭
  const [saveActionType, setSaveActionType] = useState("");

  const [isFromBack, setIsFromBack] = useState(false);

  /*  FAST 评论 */
  const FAST = "Functional Assessment Screening Tool (Fast)";
  const [comment, setComment] = useState("");
  const [isFASTDialogShow, setIsFASTDialogShow] = useState(false);

  // 是否可以编辑workflow
  const [canEditWorkflow, setCanEditWorkflow] = useState(false);

  /* Feedback */
  const [feedbackJson, setFeedbackJson] = useState();

  /* 弹窗 */
  const [isSaveWorkflowSuccessDialog, setIsSaveWorkflowSuccessDialog] = useState(false);
  const [isShowWarningDialog, setIsShowWarningDialog] = useState(false);
  const [warningError, setWarningError] = useState("");

  const updateSideBar = clientId ? useSideBarUpdate({ clientId }) : {};

  const [workflowName, setWorkflowName] = useState(null);
  const [workflowCode, setWorkflowCode] = useState(null);

  // const survey = new Model(templateContent);

  const [survey, setSurvey] = useState(new Model(null));

  useEffect(() => {
    setSurvey(new Model(templateContent));
  }, [templateContent, isFromBack]);

  survey.applyTheme(PlainLight);

  useEffect(() => {
    initSurveyCustomComponent();

    return () => {
      uninstallSurveyCustomComponent();
    };
  }, []);

  // 初始化survey

  if (!canEditWorkflow) {
    survey.mode = "display";
  }

  useEffect(() => {
    if (isFromClients && clientId) {
      updateSideBar();
    }
  }, [updateSideBar, isFromClients]);

  // 接口请求获取当前workflow 信息
  useEffect(() => {
    setTriggerList([]);
    getWorkflowDetail({ clientWorkflowId: workflowId });
  }, [workflowId]);

  useEffect(() => {
    const savedAnswers = !isFromBack ? surveyAnswerData : JSON.parse(localStorage.getItem("Fast")) || null;

    if (savedAnswers) {
      setSurveyAnswerData({ ...savedAnswers });

      setComment(savedAnswers?.comment);
    } else {
      if (workflowCode) {
        getLastCommitData(workflowCode, fillClientId);
      }
    }
  }, [isFromBack, canEditWorkflow]);

  useEffect(() => {
    // 清除之前的反馈展示
    document.querySelectorAll(".feedbackMessageContent").forEach((el) => el.remove());

    // 缓存所有问题的属性
    const questions = survey.getAllQuestions().map((item) => item.propertyHash);

    feedbackJson?.forEach((data) => {
      const { feedbackType, content, question: questionName } = data;

      // 根据问题名称查找对应问题
      const question = questions.find((item) => item.name === questionName);
      if (!question) return;

      const { id: questionId } = question;
      const questionElement = document.getElementById(questionId);

      if (questionElement) {
        // 隐藏反馈按钮
        const feedbackBtn = document.getElementById(`feedback-btn-question_${questionId}`);
        if (feedbackBtn) {
          feedbackBtn.closest(".feedback-container").style.display = "none";
        }

        const rowDiv = questionElement.closest(".sd-row");
        if (rowDiv) {
          // 创建反馈消息内容元素
          const feedbackElement = document.createElement("div");
          feedbackElement.className = "feedbackMessageContent";

          const leftDiv = document.createElement("div");
          leftDiv.className = "left";
          const imgWarning = document.createElement("img");
          imgWarning.src = waringImg || "";
          const textDiv = document.createElement("div");
          textDiv.textContent = feedbackType === "All" ? `Feedback Content: ${content}` : "";

          const rightDiv = document.createElement("div");
          rightDiv.className = "right";
          const imgEdit = document.createElement("img");
          imgEdit.src = editImg || "";
          const imgDelete = document.createElement("img");
          imgDelete.src = deleteImg || "";

          // 构建元素结构
          leftDiv.appendChild(imgWarning);
          leftDiv.appendChild(textDiv);
          feedbackElement.appendChild(leftDiv);

          // 插入反馈消息内容
          rowDiv.parentNode.insertBefore(feedbackElement, rowDiv.nextSibling);
        }
      }
    });
  }, [feedbackJson, survey]);
  useEffect(() => {
    if (isSaveDraft) {
      // 禁用所有保存按钮
      const saveButtons = document.querySelectorAll(".sv_nav_btn");
      saveButtons.forEach((button) => {
        button.disabled = true;
        button.style.opacity = "0.6";
        button.style.cursor = "not-allowed";
      });

      clientWorkflowService
        .saveDraftClientWorkflowResult({
          id: detailData.id,
          workflowId: detailData.templateId,
          residentId: detailData.residentId,
          result: JSON.stringify({ ...survey.data, comment: comment }),
          workflowStatus: "INPROCESS",
          dateAssigned: detailData.createTime,
          comment: comment,
          alreadyTriggered: triggerList,
        })
        .then((res) => {
          if (res.success) {
            setIsSaveWorkflowSuccessDialog(true);
            // 启用所有保存按钮
            saveButtons.forEach((button) => {
              button.disabled = false;
              button.style.opacity = "1";
              button.style.cursor = "pointer";
            });
            // 重置 isSaveDraft，使按钮可以再次点击
            setIsSaveDraft(false);
            if (saveActionType === "draftAndContinue") {
              setCanEditWorkflow(true);
            } else {
              setCanEditWorkflow(false);
            }
          }
        })
        .catch(() => {
          // 发生错误时也启用按钮
          const saveButtons = document.querySelectorAll(".sv_nav_btn");
          saveButtons.forEach((button) => {
            button.disabled = false;
            button.style.opacity = "1";
            button.style.cursor = "pointer";
          });
          setIsSaveDraft(false);
        });
    }
  }, [isSaveDraft]);

  // 回填

  const getAllFieldNames = (elements) => {
    let result = [];
    elements.forEach((el) => {
      if (el.type === "html") return;
      if (el.name) result.push(el.name);
      if (Array.isArray(el.elements)) {
        result = result.concat(getAllFieldNames(el.elements));
      }
    });
    return result;
  };

  const getFilterJson = () => {
    if (!templateContent || !Array.isArray(templateContent.pages)) return [];
    // 平铺所有page下的elements递归抓取
    return _.flatMap(templateContent.pages, (page) => getAllFieldNames(page.elements || []));
  };

  const getPreFillData = (answerContent) => {
    clientWorkflowService
      .getPreFillMessage({
        clientId: fillClientId,
        answerContent: answerContent,
      })
      .then((res) => {
        setSurveyAnswerData(res.data);
      });
  };

  const getPreFillJson = () => {
    const jsonArray = getFilterJson();
    const jsonObj = {};

    jsonArray.forEach((key) => {
      jsonObj[key] = "";
    });
    if (Object.keys(jsonObj).length !== 0) {
      getPreFillData(jsonObj);
    }
  };

  const getLastCommitData = (code, clientId) => {
    clientWorkflowService.getLastCommitHistory(code, clientId).then((res) => {
      if (res.data.resultContent !== null) {
        setSurveyAnswerData({ ...(JSON.parse(res.data.resultContent) || null), ...survey.data });
        setComment(JSON.parse(res.data.resultContent)?.comment || "");
      } else {
        getPreFillJson();
      }
    });
  };

  if (surveyAnswerData) {
    survey.data = { ...survey.data, ...surveyAnswerData };
  }

  useEffect(() => {
    survey.onValueChanging.add((survey, { name, question, oldValue, value }) => {
      addSaveButton();
      addSaveAndContinue();
    });

    survey.onValueChanged.add((survey, { name, question, oldValue, value }) => {
      setSurveyAnswerData(survey.data);
    });

    survey.onComplete.add((survey) => {
      const filterJson = getFilterJson();

      setIsSaveDraft(false);

      if (detailData.workflowName !== FAST) {
        setIsSaveDraft(false);
        survey.checkErrorsMode = "onComplete";

        // Gather triggerWorkflowNames and triggerServicePlanNames
        const triggerWorkflowNames = _.flatMap(templateContent.pages, (page) =>
          _.filter(page.elements, { type: "triggerworkflow" }).map((element) => element.name),
        );

        const triggerServicePlanNames = _.flatMap(templateContent.pages, (page) =>
          _.filter(page.elements, { type: "triggerserviceplan" }).map((element) => element.name),
        );

        // 从 triggerWorkflowNames 中去除 triggerLists 的值
        const filteredWorkflowNames = _.difference(triggerWorkflowNames, triggerList);

        const filteredServicePlanNames = _.difference(triggerServicePlanNames, triggerList);

        // 获取 surveyData 中对应的值
        const workflowResult = filteredWorkflowNames
          .map((name) => survey.data[name])
          .filter((value) => value !== null && value !== undefined);

        const servicePlanResult = filteredServicePlanNames
          .map((name) => survey.data[name])
          .filter((value) => value !== null && value !== undefined);

        const triggerNames = _.flatMap(templateContent.pages, (page) =>
          _.map(
            _.filter(page.elements, (element) => ["triggerworkflow", "triggerserviceplan"].includes(element.type)),
            "name",
          ),
        );

        const nextTriggered = _.filter(triggerNames, (name) => !_.isUndefined(survey.data[name]));

        // Create an array to hold promises for asynchronous operations
        const promises = [];

        if (workflowResult.length > 0) {
          const body = {
            autoNewEvent: false,
            clientId: fillClientId,
            fillType: "FILLNOW",
            workflowIds: workflowResult,
          };

          promises.push(
            WorkflowService.addWorkflowForClient(body).catch(() => {
              setIsShowWarningDialog(true);
              setWarningError("Add associated workflow exception");
              return Promise.reject("Failed to add workflow"); // Ensures that the promise chain is broken
            }),
          );
        }

        if (servicePlanResult.length > 0) {
          servicePlanResult.forEach((templateId) => {
            if (templateId !== null && templateId !== undefined) {
              const body = {
                clientId: fillClientId,
                scoring: [],
                result: "",
                templateId: templateId,
              };

              promises.push(
                ServicePlanService.saveServicePlanDetail(body).catch(() => {
                  setIsShowWarningDialog(true);
                  setWarningError("Add associated service plan exception");
                  return Promise.reject("Failed to save service plan"); // Ensures that the promise chain is broken
                }),
              );
            }
          });
        }

        // Wait for all promises to complete
        try {
          Promise.all(promises);

          submitSurvey(filterJson, nextTriggered);
        } catch (error) {
          console.error("An error occurred during the async operations:", error);
          // Handle error if needed, e.g., show a generic error message
        }
      } else {
        setSurveyAnswerData({ ...survey.data });
        setIsFASTDialogShow(true);
      }
    });
  }, [survey]);

  const submitSurvey = (filterJson, nextTriggered) => {
    clientWorkflowService
      .submitClientWorkflowResult({
        id: detailData.id,
        workflowId: detailData.templateId,
        residentId: detailData.residentId,
        result: JSON.stringify({ ...survey.data, comment: comment }),
        workflowStatus: "SUBMITTED",
        dateAssigned: detailData.createTime,
        filterJson: JSON.stringify(filterJson),
        alreadyTriggered: nextTriggered,
      })
      .then(() => {
        setIsSaveWorkflowSuccessDialog(true);
        setCanEditWorkflow(false);
      })
      .catch(() => {
        setIsShowWarningDialog(true);
        setWarningError("The Workflow saved exception.");
      });
  };

  // 一开始获取workflow data
  const getWorkflowDetail = (params) => {
    setIsFetching(true);
    clientWorkflowService
      .findClientWorkflowDetail(params)
      .then((res) => {
        console.log(res.data, "workflow detail");
        setIsFetching(false);
        console.log(JSON.parse(res.data.templateContent), "JSON.parse(res.data.templateContent");
        setTemplateContent(JSON.parse(res.data.templateContent));
        setSurveyAnswerData(JSON.parse(res.data.resultContent));
        setDetailData(res.data);
        setCanEditWorkflow(res?.data?.canEdit);
        setWorkflowName(res?.data?.workflowName);
        setWorkflowCode(res?.data?.templateCode);

        setComment(JSON.parse(res.data.resultContent)?.comment); // feedback json
        setFeedbackJson(JSON.parse(res?.data?.feedbackjson));
        setTriggerList(res.data.triggerList);
      })
      .catch(() => {
        setIsFetching(false);
        setWarningError("Workflow acquisition exception.");
      });
  };

  const saveDraft = useCallback(
    debounce(() => {
      // 保存草稿逻辑
      setSaveActionType("draftAndClose");
      setIsSaveDraft(true);
    }, 300),
    [],
  );

  const saveDraftNoClose = useCallback(
    debounce(() => {
      // 保存草稿逻辑
      setSaveActionType("draftAndContinue");
      setIsSaveDraft(true);
    }, 300),
    [],
  );
  const addSaveButton = () => {
    // 检查页面上是否已经存在具有指定类名的按钮
    const existingButton = document.querySelector(".sv_save_btn");

    // 如果不存在具有指定类名的按钮，则添加新按钮
    if (!existingButton) {
      let parentDiv = document.getElementById("sv-nav-complete");

      let actionContentDiv = parentDiv.querySelector(".sv-action__content");
      let saveButton = document.createElement("input");
      saveButton.setAttribute("type", "button");
      saveButton.setAttribute("value", "Save & Close");
      saveButton.classList.add("sv_nav_btn", "sv_save_btn");
      saveButton.onclick = saveDraft;
      actionContentDiv?.appendChild(saveButton);
    }
  };

  const addSaveAndContinue = () => {
    // 检查页面上是否已经存在具有指定类名的按钮
    const existingButton = document.querySelector(".sv_save_and_continue_btn");

    // 如果不存在具有指定类名的按钮，则添加新按钮
    if (!existingButton) {
      let parentDiv = document.getElementById("sv-nav-complete");

      let actionContentDiv = parentDiv.querySelector(".sv-action__content");
      let draftButton = document.createElement("input");
      draftButton.setAttribute("type", "button");
      draftButton.setAttribute("value", "Save & Continue");
      draftButton.classList.add("sv_nav_btn", "sv_save_and_continue_btn");
      draftButton.onclick = saveDraftNoClose;
      actionContentDiv?.appendChild(draftButton);
    }
  };

  const downloadPDF = () => {
    const pdfDocOptions = {
      fontSize: 12,
    };
    const questions = JSON.stringify(templateContent);

    const savePdf = function () {
      const surveyPdf = new SurveyPDF(questions, pdfDocOptions);

      surveyPdf.mode = "display";
      surveyPdf.data = surveyAnswerData;
      surveyPdf.save(workflowName);
    };
    savePdf();
  };

  const changeComment = (fields, value) => {
    setComment(value);
  };

  const goBackForFAST = () => {
    localStorage.setItem("Fast", JSON.stringify(surveyAnswerData));
    setIsFromBack(true);
    setIsFASTDialogShow(false);
  };

  const onSubmitFast = () => {
    const filterJson = getFilterJson();
    submitSurvey(filterJson, []);
    localStorage.removeItem("Fast");
  };

  const onCloseSuccessDialog = () => {
    setIsSaveWorkflowSuccessDialog(false);

    const shouldNavigate = saveActionType === "draftAndClose" || saveActionType === "";

    // 延迟重置 saveActionType，确保对话框完全关闭后再重置
    setTimeout(() => {
      setSaveActionType("");
    }, 300);

    if (shouldNavigate) {
      history.go(-1);
    }
  };

  return (
    <>
      <div className="workflow-admin-detail-content">
        {isFetching && <Loader isCentered />}

        {!isFetching && (
          <>
            <div className="workflow-detail-top">
              <div className="workflow-detail-top-title">
                {workflowName || "Beneficiary Colorectal Cancer Screening"}
              </div>
              <div className="workflow-detail-top-desc">
                Community Name:{detailData?.communityName} &nbsp;Client Name: {detailData?.fullName || "Aaron Yang"}
              </div>
              <div className={"workflow-right-btn-list"}>
                {(detailData?.status === "SUBMITTED" || detailData?.status === "APPROVED") && (
                  <div onClick={downloadPDF}>
                    <DownLoadBtn className="workflow-detail-down-load" />
                  </div>
                )}
              </div>
            </div>
            {!isFASTDialogShow && (
              <div className="workflow-detail-survey">
                <Survey
                  model={survey}
                  goNextPageAutomatic={false}
                  showCompletedPage={false}
                  showNavigationButtons={canEditWorkflow && !isFASTDialogShow}
                  completeText={`${workflowName === FAST ? "Next" : "Submit"}`}
                />
              </div>
            )}

            {((workflowName === FAST && canEditWorkflow && isFASTDialogShow) ||
              (detailData?.status === "SUBMITTED" && workflowName === FAST)) && (
              <div className="managementSection">
                <div className="AssessmentEditor-SectionTitle">Management</div>
                <Table
                  className={`FAST Scale`}
                  containerClass={`ScaleContainer`}
                  data={ScoringSummaryData}
                  keyField={"id"}
                  columns={[
                    {
                      dataField: "LMV",
                      text: "Likely Maintaining Variable",
                      headerStyle: {
                        width: "380px",
                      },
                      formatter: (v, row) => {
                        return <div>{v}</div>;
                      },
                    },
                    {
                      dataField: "question",
                      text: "",
                      formatter: (v, row) => {
                        return (
                          <div className={"QId-list"}>
                            {questionList[v]?.map((item, index) => {
                              const isActive = surveyAnswerData[item.QId] === "Yes";
                              return (
                                <div className={`QId-list-item ${isActive && "is-active"}`} key={index}>
                                  {item.text}
                                </div>
                              );
                            })}
                          </div>
                        );
                      },
                    },
                  ]}
                />
                <div className="padding-left-10 padding-right-10">
                  <TextField
                    type="textarea"
                    label="Comments/Notes:"
                    name="comment"
                    value={comment}
                    isDisabled={!canEditWorkflow}
                    className="AssessmentForm-Field AssessmentForm-CommentField"
                    onChange={changeComment}
                  />
                </div>
                {canEditWorkflow && (
                  <div>
                    <Button color="success" className="margin-right-25 width-170" onClick={goBackForFAST}>
                      Back
                    </Button>

                    <Button color="success" className="margin-right-25 width-170" onClick={onSubmitFast}>
                      Submit
                    </Button>
                  </div>
                )}
              </div>
            )}
          </>
        )}
      </div>
      <Footer theme="gray" className={"event-workflow-admin-footer"} />

      <SuccessDialog
        isOpen={isSaveWorkflowSuccessDialog}
        title={
          saveActionType === "draftAndContinue" || saveActionType === "draftAndClose"
            ? `Workflow has been saved.`
            : ` Workflow has been submitted.`
        }
        buttons={[
          {
            text: saveActionType === "draftAndContinue" ? "OK" : "Close",
            onClick: () => {
              onCloseSuccessDialog();
            },
          },
        ]}
      />

      <WarningDialog
        isOpen={isShowWarningDialog}
        title={warningError}
        buttons={[
          {
            text: "Ok",
            onClick: () => {
              setIsShowWarningDialog(false);
            },
          },
        ]}
      />
    </>
  );
};

export default WorkflowAdminDetail;
