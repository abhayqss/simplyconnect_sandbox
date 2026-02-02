import React, { useEffect, useState } from "react";
import WorkflowFeedbackAddModal from "./WorkflowFeedbackAddModal";
import waringImg from "images/workflow/waring.svg";
import editImg from "images/workflow/edit.svg";
import deleteImg from "images/workflow/deleteImg.svg";
import service from "services/QAEventsService";
import { useParams } from "react-router-dom";
import { ConfirmDialog, SuccessDialog, WarningDialog } from "components/dialogs";
import { useAuthUser } from "hooks/common/redux";
import { Loader, Table } from "components";
import {
  questionList,
  ScoringSummaryData,
} from "../../../Clients/Clients/Assessments/AssessmentEditor/AssessmentEditor";
import { TextField } from "components/Form";
import { FAST } from "../../../Clients/Clients/WorkflowDetail/WorkflowDetail";

import { ReactComponent as DownLoadBtn } from "images/workflow/downloadRadios.svg";

import { SurveyPDF } from "survey-pdf";
import { Model } from "survey-core";
import { Survey } from "survey-react-ui";
import "survey-core/defaultV2.min.css";
import { PlainLight } from "survey-core/themes";

import "./WorkflowFeedback.scss";
import Footer from "../../../../components/Footer/Footer";
import initSurveyCustomComponent, {
  uninstallSurveyCustomComponent,
} from "../../../Admin/Workflow/WorkflowManagementCreate/CustomComponent/initSurveyCustomComponent";

const WorkflowFeedback = () => {
  const params = useParams();

  const user = useAuthUser();

  const [questionJson, setQuestionJson] = useState();
  const [answerJson, setAnswerJson] = useState();

  const [survey, setSurvey] = useState(new Model(questionJson)); // json 是你的问卷定义

  const [showEditModal, setShowEditModal] = useState(false);

  const [question, setQuestion] = useState("");
  const [confirmData, setConfirmData] = useState([]);

  const [resultId, setResultId] = useState("");

  const [showSuccessDialog, setShowSuccessDialog] = useState(false);
  const [successTitle, setSuccessTitle] = useState("");
  const [waringDialog, setWaringDialog] = useState(false);
  const [otherWaringDialog, setOtherWaringDialog] = useState(false);

  const [workflowName, setWorkflowName] = useState("");
  const [fullName, setFullName] = useState("");
  const [communityName, setCommunityName] = useState("");

  const [edit, setEdit] = useState(false);

  const [status, setStatus] = useState();

  const [loading, setLoading] = useState(false);

  const [sendFeedBackWaringDialog, setSendFeedBackWaringDialog] = useState(false);
  const [noFeedBackWaringDialog, setNoFeedBackWaringDialog] = useState(false);
  const filterArray = (array, filterValue) => {
    return array.filter((item) => item !== filterValue);
  };

  useEffect(() => {
    initSurveyCustomComponent();

    return () => {
      uninstallSurveyCustomComponent();
    };
  }, []);

  useEffect(() => {
    if (params.eventId) {
      setLoading(true);
      service.findWorkflowDetail(params.eventId).then((res) => {
        const {
          resultContent,
          templateContent,
          resultId,
          feedbackjson,
          status,
          workflowName,
          fullName,
          communityName,
        } = res;
        setQuestionJson(JSON.parse(templateContent));
        setAnswerJson(JSON.parse(resultContent));
        setSurvey(new Model(JSON.parse(templateContent)));
        setResultId(resultId);
        setStatus(status);
        setWorkflowName(workflowName);
        setFullName(fullName);
        setCommunityName(communityName);

        if (feedbackjson) {
          setConfirmData(JSON.parse(feedbackjson));
          setEdit(true);
        } else if (status === "APPROVED" || status === "PENDING" || status === "INPROCESS") {
          setEdit(true);
        } else {
          setEdit(false);
        }

        setLoading(false);
      });
    }
  }, []);

  useEffect(() => {
    survey.applyTheme({ PlainLight });
    const savedAnswers = answerJson;
    if (savedAnswers) {
      survey.data = savedAnswers;
    }
    survey.mode = "display"; // 设置问卷为只读模式
  }, [survey, answerJson]);

  useEffect(() => {
    // 清除之前的反馈展示
    document.querySelectorAll(".feedbackMessageContent").forEach((el) => el.remove());

    // 缓存所有问题的属性
    const questions = survey.getAllQuestions().map((item) => item.propertyHash);

    confirmData.forEach((data) => {
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
          rightDiv.appendChild(imgEdit);
          rightDiv.appendChild(imgDelete);
          feedbackElement.appendChild(leftDiv);
          feedbackElement.appendChild(rightDiv);

          // 编辑按钮点击事件
          imgEdit.onclick = () => {
            setShowEditModal(true);
            setQuestion(questionName);
          };

          // 删除按钮点击事件
          imgDelete.onclick = () => {
            setQuestion("");
            const updatedConfirmData = confirmData.filter((item) => item.question !== questionName);
            setConfirmData(updatedConfirmData);

            // 显示反馈按钮
            const feedbackBtn = document.getElementById(`feedback-btn-question_${questionId}`);
            if (feedbackBtn) {
              feedbackBtn.closest(".feedback-container").style.display = "flex";
            }
          };

          // 插入反馈消息内容
          rowDiv.parentNode.insertBefore(feedbackElement, rowDiv.nextSibling);
        }
      }
    });
  }, [confirmData, survey]);

  const sendBack = () => {
    if (confirmData.length === 0) {
      setNoFeedBackWaringDialog(true);
    } else {
      setSendFeedBackWaringDialog(true);
    }
  };

  const sendBackApi = () => {
    setLoading(true);
    const JsonData = JSON.stringify(confirmData);
    const operatorId = user.id;
    const clientWorkflowId = Number(params.eventId);

    service.notPassThroughFeedback(clientWorkflowId, resultId, operatorId, JsonData).then((res) => {
      if (res.success) {
        setLoading(false);
        setSuccessTitle("feedback added successfully");
        setShowSuccessDialog(true);
        history.back();
      }
    });
  };

  const handleGlobalConfirmClick = () => {
    if (confirmData.length === 0) {
      service.approveFeedback(params.eventId).then((res) => {
        if (res) {
          setSuccessTitle("Workflow has been approved");
          setShowSuccessDialog(true);
        }
      });
    } else {
      setOtherWaringDialog(true);
    }
  };

  // 定义一个函数，在该函数中处理渲染问题后的逻辑
  const onAfterRenderQuestion = (sender, options) => {
    // 获取当前问题的信息
    const questionIndex = sender.currentPage.questions.indexOf(options.question);
    const question = sender.currentPage.questions[questionIndex];

    if (question) {
      const questionId = question.id;
      const questionName = question.name;

      // 找到当前问题所在的行元素
      const rowDiv = options.htmlElement.closest(".sd-row");

      // 检查当前行的下一个元素是否已经有了反馈容器
      const nextElement = rowDiv.nextElementSibling;
      const isExited = nextElement?.classList.contains("feedback-container");

      if (!isExited) {
        // 创建反馈容器
        const container = createFeedbackContainer(questionId, questionName);

        // 将反馈容器插入到当前行的下方
        const parentDiv = rowDiv.parentNode;
        parentDiv.insertBefore(container, rowDiv.nextSibling);
      }
    }
  };

  // 创建反馈容器的函数，包括左侧虚线、反馈按钮和右侧虚线
  function createFeedbackContainer(questionId, questionName) {
    const container = document.createElement("div");
    container.className = "feedback-container";

    // 创建左侧虚线
    const leftDashedLine = document.createElement("div");
    leftDashedLine.className = "dashed-line left-line";

    // 创建右侧虚线
    const rightDashedLine = document.createElement("div");
    rightDashedLine.className = "dashed-line right-line";

    // 创建反馈按钮
    const feedbackBtn = document.createElement("button");
    feedbackBtn.innerText = "Add feedback"; // 根据需要调整文本
    feedbackBtn.className = "feedback-btn";
    feedbackBtn.id = `feedback-btn-question_${questionId}`;

    feedbackBtn.onclick = function () {
      setShowEditModal(true);
      setQuestion(questionName);
    };

    // 将左侧虚线、反馈按钮和右侧虚线添加到容器中
    container.appendChild(leftDashedLine);
    container.appendChild(feedbackBtn);
    container.appendChild(rightDashedLine);

    return container;
  }

  // 在页面加载完成后，使用 MutationObserver 监听可能的动态添加的必填项
  document.addEventListener("DOMContentLoaded", function () {
    // 初始化 MutationObserver
    const observer = new MutationObserver(function (mutationsList) {
      // 遍历每个变化
      for (let mutation of mutationsList) {
        if (mutation.type === "childList" && mutation.addedNodes.length > 0) {
          // 检查新增的节点中是否有必填项
          mutation.addedNodes.forEach((node) => {
            if (node.nodeType === Node.ELEMENT_NODE && node.matches('[aria-required="true"]')) {
              // 找到了新增的必填项，为其添加反馈容器
              const parentDiv = node.parentNode;
              const container = createFeedbackContainer(node.id, node.name);
              parentDiv.insertBefore(container, node.nextSibling);
            }
          });
        }
      }
    });

    // 开始观察整个文档的变化，特别是子节点的变化
    observer.observe(document.body, { childList: true, subtree: true });
  });

  useEffect(() => {
    if (edit) {
      // 异步操作确保DOM已更新
      setTimeout(() => {
        const feedbackButtons = document.getElementsByClassName("feedback-btn");
        const feedbackButtonsArray = Array.from(feedbackButtons);
        feedbackButtonsArray.forEach((button) => {
          button.style.display = "none";
        });

        const Img = document.querySelectorAll(".feedbackMessageContent .right");
        const ImgArr = Array.from(Img);
        ImgArr.forEach((div) => {
          div.style.display = "none";
        });
      }, 20);
    }
  }, [survey, edit]);

  const downloadPDF = () => {
    const pdfDocOptions = {
      fontSize: 12,
    };
    const questions = JSON.stringify(questionJson);

    const savePdf = function () {
      const surveyPdf = new SurveyPDF(questions, pdfDocOptions);

      // 下载的pdf 是否能编辑
      surveyPdf.mode = "display";
      surveyPdf.data = answerJson;
      surveyPdf.save(workflowName);
    };
    savePdf();
  };

  return (
    <>
      {loading ? (
        <Loader />
      ) : (
        <div className={"workflowFeedbackWrap"}>
          <div className="workflow-detail-top">
            <div className={"workflow-right-btn-list"}>
              {(status === "SUBMITTED" || status === "APPROVED") && (
                <div onClick={downloadPDF}>
                  <DownLoadBtn className="workflow-detail-down-load" />
                </div>
              )}

              {/*  <div>
                      <ShareBtn className="workflow-detail-share"></ShareBtn>
                    </div>*/}
            </div>

            <div className="workflow-detail-top-title">{workflowName}</div>
            <div className="workflow-detail-top-desc">
              Community Name:{communityName} &nbsp;Client Name: {fullName}
            </div>
          </div>

          <div className="workflowFeedbackBox">
            <Survey model={survey} onAfterRenderQuestion={onAfterRenderQuestion} />
            {workflowName === FAST && (
              <div className="WorkflowFeedback-ManagementSection padding-left-28 padding-right-28">
                <div className="WorkflowFeedback-AssessmentEditor-SectionTitle">Management</div>
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
                              const isActive = answerJson[item.QId] === "Yes";
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
                <div className="WorkflowFeedback-Bottom-Comments">
                  <TextField
                    type="textarea"
                    label="Comments/Notes:"
                    name="comment"
                    value={answerJson?.comment}
                    isDisabled={true}
                    className=" WorkflowFeedback-AssessmentForm-CommentField"
                  />
                </div>
              </div>
            )}
          </div>
          {!edit && status !== "APPROVED" && (
            <div className={"workflowFeedbackWrapFooter"}>
              <div onClick={sendBack}>Send Back</div>
              <div onClick={handleGlobalConfirmClick}>Approve</div>
            </div>
          )}
          <Footer theme="gray" className={"event-footer"} />
        </div>
      )}

      {showEditModal && (
        <WorkflowFeedbackAddModal
          isOpen={showEditModal}
          close={() => {
            setShowEditModal(false);
            // showFeedbackInfo();
          }}
          question={question}
          confirmData={confirmData}
          setConfirmData={setConfirmData}
        />
      )}

      {showSuccessDialog && (
        <SuccessDialog
          isOpen
          title={successTitle}
          buttons={[
            {
              text: "Ok",
              onClick: () => {
                setShowSuccessDialog(false);
                history.back();
              },
            },
          ]}
        />
      )}

      {sendFeedBackWaringDialog && (
        <ConfirmDialog
          isOpen={sendFeedBackWaringDialog}
          title="Confirm send feedback?"
          onConfirm={() => {
            setSendFeedBackWaringDialog(false);
            sendBackApi();
          }}
          onCancel={() => setSendFeedBackWaringDialog(false)}
        />
      )}

      {noFeedBackWaringDialog && (
        <WarningDialog
          isOpen
          title="Please add at least one feedback before operation."
          buttons={[
            {
              text: "OK",
              onClick: () => {
                setNoFeedBackWaringDialog(false);
              },
            },
          ]}
        />
      )}

      {waringDialog && (
        <ConfirmDialog
          isOpen
          title="Confirm adding feedback?"
          onConfirm={() => {
            setWaringDialog(false);
            sendBackApi();
          }}
          onCancel={() => setWaringDialog(false)}
        />
      )}

      {otherWaringDialog && (
        <WarningDialog
          isOpen
          title="Please remove all feedback before proceeding with the operation."
          buttons={[
            {
              text: "OK",
              onClick: () => {
                setOtherWaringDialog(false);
              },
            },
          ]}
        />
      )}
    </>
  );
};

export default WorkflowFeedback;
