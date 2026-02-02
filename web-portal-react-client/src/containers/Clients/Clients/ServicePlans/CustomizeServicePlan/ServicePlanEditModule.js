import React, { useEffect, useState } from "react";
import { Loader, Modal } from "../../../../../components";

import { Model } from "survey-core";
import { Survey } from "survey-react-ui";
import "survey-core/defaultV2.min.css";
import { PlainLight } from "survey-core/themes";

import initSurveyCustomComponent, {
  uninstallSurveyCustomComponent,
} from "../../../../Admin/Workflow/WorkflowManagementCreate/CustomComponent/initSurveyCustomComponent";

import adminWorkflowCreateService from "../../../../../services/AdminWorkflowCreateService";
import directoryService from "../../../../../services/DirectoryService";

import ScoringInfoHint from "../ServicePlanEditor/ScoringInfoHint/ScoringInfoHint";
import { ReactComponent as Info } from "images/info.svg";
import ServicePlanScoring from "../ServicePlanScoring/ServicePlanScoring";
import { Button } from "reactstrap";
import { useAuthUser } from "../../../../../hooks/common";

const ServicePlanEditModule = (props) => {
  const {
    servicePlanTemplate,
    setShowServicePlanEditModal,
    showServicePlanEditModal,
    clientId,
    templateId,
    setShowSuccessDialog,
    isEditServicePlan,
    servicePlanTemplatesData,
    editServicePlanId,
    setEditServicePlanId,
    setEditServicePlanTemplateId,
    editServiceScoring,
    showErrorDialog,
    clientDetailData,
  } = props;

  const userInfo = useAuthUser();
  const [surveyAnswerData, setSurveyAnswerData] = useState();

  const [completeButtonTitle, setCompleteButtonTitle] = useState("Submit");
  const [domainData, setDomainData] = useState([]);
  const [programTypeData, setProgramTypeData] = useState([]);
  const [programSubTypeData, setProgramSubTypeData] = useState([]);

  const [scoreData, setScoreData] = useState([]);

  const [showSurvey, setShowSurvey] = useState(true);
  const [isScoringHintOpen, setIsScoringHintOpen] = useState(false);

  const [isFetching, setIsFetching] = useState(false);

  useEffect(() => {
    initSurveyCustomComponent();

    return () => {
      uninstallSurveyCustomComponent();
    };
  }, []);

  const survey = new Model(JSON.parse(servicePlanTemplate?.content));
  survey.applyTheme(PlainLight);

  function formatDate(dateString) {
    // 将日期字符串分割为月和日
    const parts = dateString.split("/");
    // 构造新的日期对象（月份要减去 1，因为 JavaScript 的月份是从 0 开始的）
    const dateObject = new Date(parts[2], parts[0] - 1, parts[1]);
    // 使用 Date 对象的方法获取年、月、日，并格式化为 YYYY-MM-DD    const formattedDate = `${dateObject.getFullYear()}-${(dateObject.getMonth() + 1).toString().padStart(2, "0")}-${dateObject.getDate().toString().padStart(2, "0")}`;
    return `${dateObject.getFullYear()}-${(dateObject.getMonth() + 1).toString().padStart(2, "0")}-${dateObject.getDate().toString().padStart(2, "0")}`;
  }

  function formatTimestamp(timestamp) {
    if (!timestamp) {
      return "";
    }

    const date = new Date(timestamp);

    const year = date.getFullYear();
    const month = String(date.getMonth() + 1).padStart(2, "0");
    const day = String(date.getDate()).padStart(2, "0");
    const hours = String(date.getHours()).padStart(2, "0");
    const minutes = String(date.getMinutes()).padStart(2, "0");

    return `${year}-${month}-${day}T${hours}:${minutes}`;
  }

  function extractAndMerge(detailData, fields) {
    let extractedData = {};

    fields.forEach((field) => {
      if (field in detailData) {
        // 对于 birthDate 字段，使用 formatDate 函数进行格式转换
        if (field === "birthDate") {
          extractedData[field] = formatDate(detailData[field]);
        } else {
          extractedData[field] = detailData[field];
        }
      } else if (field === "city" || field === "street" || field === "state" || field === "zip") {
        if (detailData.address && detailData.address[field]) {
          extractedData[field] = detailData.address[field];
        }
      }
    });

    return extractedData;
  }

  useEffect(() => {
    const allFillArr = [
      "servicePlan",
      "fullName",
      "firstName",
      "middleName",
      "lastName",
      "birthDate",
      "gender",
      "race",
      "marital",
      "city",
      "street",
      "state",
      "zip",
      "phone",
      "cellPhone",
      "email",
    ];

    const effectData = extractAndMerge(clientDetailData, allFillArr);

    // if (!isEditServicePlan) {
    effectData.servicePlan = {
      createdBy: userInfo?.fullName,
      dateCreated: formatTimestamp(new Date().getTime()),
    };
    // }

    setSurveyAnswerData(effectData);
  }, []);

  useEffect(() => {
    directoryService.findDomains().then((res) => {
      setDomainData(res.data);
    });

    directoryService.findProgramTypes().then((res) => {
      setProgramTypeData(res.data);
    });

    directoryService.findProgramSubTypes().then((res) => {
      setProgramSubTypeData(res.data);
    });

    if (isEditServicePlan && servicePlanTemplatesData) {
      const data = JSON.parse(servicePlanTemplatesData);

      if (data.servicePlan) {
        data?.servicePlan?.needs?.forEach((need) => {
          need?.goals?.forEach((goal) => {
            goal.targetCompletionDate = formatTimestamp(goal?.targetCompletionDate);
            goal.completionDate = formatTimestamp(goal?.completionDate);
          });
        });
      }

      setSurveyAnswerData(data);
    }
  }, [isEditServicePlan, servicePlanTemplatesData, isScoringHintOpen]);

  const clearData = () => {
    setCompleteButtonTitle("Submit");
    setScoreData([]);
    setDomainData([]);
    setSurveyAnswerData({});
    setShowSurvey(true);
    setShowServicePlanEditModal(false);
    setEditServicePlanTemplateId("");
    setEditServicePlanId("");
  };

  const onAfterRenderPage = (survey) => {
    if (survey.data?.servicePlan?.needs?.length > 0) {
      survey.completeText = "Next";
    } else {
      survey.completeText = "Submit";
    }
  };

  const transformAndMergeData = (originalData) => {
    // Extract needs array from original data
    const needs = originalData.servicePlan.needs;

    // Map to store merged needs
    const transformedData = [];

    // Iterate over needs and merge based on domainId
    needs?.forEach((need) => {
      // Find matching item in matchingArray based on domainId
      const matchedItem = domainData.find((item) => item.id === need.domainId);

      const matchedProgramItem = programTypeData.find((item) => item.id === need.programTypeId);
      need.programTypeName = matchedProgramItem?.name;
      need.programTypeTitle = matchedProgramItem?.title;

      const matchedProgramSubTypeItem = programSubTypeData.find((item) => item.id === need.programSubTypeId);
      need.programSubTypeName = matchedProgramSubTypeItem?.name;
      need.programSubTypeTitle = matchedProgramSubTypeItem?.title;
      need.programSubTypeZCode = matchedProgramSubTypeItem?.zcode;
      need.programSubTypeZCodeDesc = matchedProgramSubTypeItem?.zcodeDesc;

      // Find corresponding score from scoreData based on domainId
      const scoreEntry = scoreData.find((entry) => entry.domainId === need.domainId);

      // Check if domainId already exists in transformedData
      const existingNeed = transformedData.find((item) => item.domainId === need.domainId);

      // const matchedProgramType =

      if (existingNeed) {
        // If domainId exists, push to existing needs array
        existingNeed.needs.push({
          ...need,

          domainName: matchedItem ? matchedItem.name : "",
          domainTitle: matchedItem ? matchedItem.title : "",
          score: scoreEntry ? scoreEntry.score : 0,
        });
      } else {
        // If domainId does not exist, create new entry
        transformedData.push({
          domainId: need.domainId,
          domainName: matchedItem ? matchedItem.name : "",
          domainTitle: matchedItem ? matchedItem.title : "",
          score: scoreEntry ? scoreEntry.score : 0,
          needs: [
            {
              ...need,
              domainName: matchedItem ? matchedItem.name : "",
              domainTitle: matchedItem ? matchedItem.title : "",
            },
          ],
        });
      }
    });

    return transformedData;
  };

  const onChangeScore = (domainId, score) => {
    const filterData = scoreData.find((item) => item.domainId === domainId);

    filterData.score = score;
  };

  const onComplete = (sender, options) => {
    if (survey.completeText === "Next") {
      setSurveyAnswerData({ ...survey.data });

      const result = transformAndMergeData(survey.data);

      if (isEditServicePlan) {
        editServiceScoring.forEach((scoreObj) => {
          const domainId = scoreObj.domainId;
          const score = scoreObj.score;

          result.forEach((data) => {
            if (data.domainId === domainId) {
              data.score = score;
            }
          });
        });
      }

      result.forEach((domain) => {
        domain?.needs?.forEach((need) => {
          need?.goals?.forEach((goal) => {
            goal.targetCompletionDate = new Date(goal?.targetCompletionDate).getTime();
            goal.completionDate = new Date(goal?.completionDate).getTime();
          });
        });
      });

      setScoreData(result);

      setShowSurvey(false);
    } else {
      const body = {
        templateId,
        clientId,
        result: JSON.stringify(survey.data),
        scoring: [],
      };

      if (isEditServicePlan) {
        body.id = editServicePlanId;
      }

      adminWorkflowCreateService
        .saveServicePlanDetail(body)
        .then(() => {
          setIsFetching(false);
          clearData();
          setShowSuccessDialog(true);
        })
        .catch(() => {
          setIsFetching(false);
          showErrorDialog(true);
        });
    }
  };

  const surveyValueChange = (survey, question) => {
    if (question.value?.needs?.length > 0) {
      survey.completeText = "Next";
    } else {
      survey.completeText = "Submit";
    }
  };

  const backToEdit = () => {
    setShowSurvey(true);
  };

  const saveServicePlanDetail = () => {
    setIsFetching(true);

    const scoring = scoreData.map((item) => {
      return {
        domainId: item.domainId,
        score: item.score,
      };
    });

    const body = {
      templateId,
      clientId,
      result: JSON.stringify(surveyAnswerData),
      scoring,
    };

    if (isEditServicePlan) {
      body.id = editServicePlanId;
    }

    adminWorkflowCreateService
      .saveServicePlanDetail(body)
      .then(() => {
        setIsFetching(false);
        clearData();
        setShowSuccessDialog(true);
      })
      .catch(() => {
        setIsFetching(false);
        showErrorDialog(true);
      });
  };

  return (
    <>
      <Modal
        isOpen={showServicePlanEditModal}
        className="serviceplanModel"
        renderHeader={() => {
          return (
            <div style={{ display: "flex" }}>
              {showSurvey ? `${isEditServicePlan ? "Edit" : "Add New"} Plan` : "Scoring"}
              {!showSurvey && (
                <div className="position-relative">
                  <Info
                    className="ScoringHint-Icon"
                    onMouseEnter={() => setIsScoringHintOpen(true)}
                    onMouseLeave={() => setIsScoringHintOpen(false)}
                  />
                  {isScoringHintOpen && <ScoringInfoHint />}
                </div>
              )}
            </div>
          );
        }}
        renderFooter={() => {
          return (
            <>
              {showSurvey ? (
                <>
                  {/*<Button color="success" disabled={isFetching} onClick={saveServicePlan}>*/}
                  {/*  {completeButtonTitle}*/}
                  {/*</Button>*/}
                </>
              ) : (
                <>
                  <div>
                    <Button outline color="success" disabled={isFetching} onClick={backToEdit}>
                      Back
                    </Button>

                    <Button color="success" disabled={isFetching} onClick={saveServicePlanDetail}>
                      Save
                    </Button>
                  </div>
                </>
              )}
            </>
          );
        }}
        hasCloseBtn={true}
        onClose={() => {
          setShowServicePlanEditModal(false);
        }}
      >
        {isFetching && (
          <Loader
            hasBackdrop
            style={{
              position: "fixed",
            }}
          />
        )}

        {showSurvey && (
          <Survey
            model={survey}
            data={surveyAnswerData}
            goNextPageAutomatic={false}
            showCompletedPage={false}
            showCompleteButton={true}
            onAfterRenderSurvey={onAfterRenderPage}
            onValueChanged={surveyValueChange}
            onComplete={onComplete}
          />
        )}

        {!showSurvey && <ServicePlanScoring data={scoreData} onChangeScore={onChangeScore} />}
      </Modal>
    </>
  );
};

export default ServicePlanEditModule;
