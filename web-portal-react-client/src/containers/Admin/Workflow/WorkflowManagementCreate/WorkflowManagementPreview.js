import React, { useEffect, useState } from "react";
import { Loader } from "../../../../components";
import adminWorkflowCreateService from "../../../../services/AdminWorkflowCreateService";
import { ErrorDialog } from "../../../../components/dialogs";
import { useHistory, useParams } from "react-router-dom";
import initSurveyCustomComponent, { uninstallSurveyCustomComponent } from "./CustomComponent/initSurveyCustomComponent";
import { UpdateSideBarAction } from "../../../../actions/admin";
import { ReactComponent as TemporaryStorageImg } from "images/workflow/temporaryStorage.svg";
import { Model, setLicenseKey, SvgRegistry } from "survey-core";
import ReactDOMServer from "react-dom/server";

import { Survey } from "survey-react-ui";
import "survey-core/defaultV2.min.css";
// import "./survey-create.css";

import Breadcrumbs from "../../../../components/Breadcrumbs/Breadcrumbs";
import DocumentTitle from "react-document-title";
import "./WorkflowManagementPreview.scss";
import { Button } from "reactstrap";

const svg = ReactDOMServer.renderToString(<TemporaryStorageImg />);
SvgRegistry.registerIconFromSvg("icon-defaultfile", svg);

const WorkflowManagementPreview = () => {
  const history = useHistory();
  const { previewId: templateId, isFromWorkflow } = useParams();
  const [isFetching, setIsFetching] = useState(false);
  const [errorMessage, setErrorMessage] = useState();
  const [workflowDetailData, setWorkflowDetailData] = useState("");
  const [workflowTitle, setWorkflowTitle] = useState("");
  const [survey, setSurvey] = useState(new Model());

  useEffect(() => {
    initSurveyCustomComponent();

    return () => {
      uninstallSurveyCustomComponent();
    };
  }, []);

  const getWorkflowDetailData = (templateId) => {
    adminWorkflowCreateService
      .getWorkflowDetail({ templateId })
      .then((res) => {
        setIsFetching(false);
        setWorkflowDetailData(res?.data?.content);
        setWorkflowTitle(res?.data?.name);
        setSurvey(new Model(res?.data?.content));
      })
      .catch((error) => {
        setIsFetching(false);
        setErrorMessage(error.message);
      });
  };
  const getServiceDetailData = (templateId) => {
    adminWorkflowCreateService
      .getServicePlanDetail({ templateId })
      .then((res) => {
        setIsFetching(false);
        setWorkflowDetailData(res?.data?.content);
        setWorkflowTitle(res?.data?.name);
        setSurvey(new Model(res?.data?.content));
      })
      .catch((error) => {
        setIsFetching(false);
        setErrorMessage(error.message);
      });
  };
  useEffect(() => {
    setIsFetching(true);
    isFromWorkflow === "1" ? getWorkflowDetailData(templateId) : getServiceDetailData(templateId);
  }, [templateId, isFromWorkflow]);

  useEffect(() => {
    survey.mode = "display";
  }, [survey]);

  const goBack = () => {
    history.goBack();
  };

  return (
    <>
      <DocumentTitle title={"Simply Connect | Admin | Workflow | Workflow Preview"}>
        <div className={"Workflow-Preview-Page"}>
          {isFetching && <Loader isCentered hasBackdrop />}
          <UpdateSideBarAction />
          <Breadcrumbs
            className={"margin-bottom-10"}
            items={[
              { title: "Admin", href: "/admin/organizations", isEnabled: true },
              { title: "Workflow Library", href: "/admin/workflowManagement", isEnabled: true },
              {
                title: isFromWorkflow === "1" ? "Workflow Preview" : "Service Preview",
                href: "/admin/workflowManagement/preview",
                isActive: true,
              },
            ]}
          />
          <div className="Workflow-Preview-Caption">
            <div className="Workflow-Preview-CaptionHeader">
              <span className="Workflow-Preview-TitleText">
                {isFromWorkflow === "1" ? `Workflow` : `Service`} Preview
              </span>
            </div>
            <Button color="success" onClick={goBack}>
              Go Back
            </Button>
          </div>
          <div className="Workflow-Preview-Title">{workflowTitle}</div>
          <div className={"Admin-Workflow-Preview"}>
            <Survey
              model={survey}
              data={workflowDetailData}
              goNextPageAutomatic={false}
              showCompletedPage={false}
              showNavigationButtons={false}
            />
          </div>

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
        </div>
      </DocumentTitle>
    </>
  );
};

export default WorkflowManagementPreview;
