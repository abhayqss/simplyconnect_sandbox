import React, { useEffect, useState } from "react";
import initSurveyCustomComponent from "../../../../Admin/Workflow/WorkflowManagementCreate/CustomComponent/initSurveyCustomComponent";
import { Model } from "survey-core";
import { Survey } from "survey-react-ui";
import "survey-core/defaultV2.min.css";
import { PlainLight } from "survey-core/themes";
import "./ShowCustomerServicePlan.scss";
import moment from "moment";

function removeServicePlan(template) {
  const haveCustomServicePlan = template.pages.some((page) =>
    page.elements.some((element) => element.name === "servicePlan"),
  );

  template.pages.forEach((page) => {
    page.elements = page.elements.filter((element) => element.name !== "servicePlan");
  });
  return { template, haveCustomServicePlan };
}

function extractSignatureAndRemoveServicePlan(data) {
  let signatures = [];
  let mentalAcuityLevel = "";
  let narratives = "";
  let careTeam = "";
  let consentToReceiveCarePlan = "";

  if (data?.servicePlan?.signature) {
    signatures = data.servicePlan.signature;
  }

  if (data?.servicePlan) {
    mentalAcuityLevel = data.servicePlan.mentalAcuityLevel;
    narratives = data.servicePlan.narratives;
    careTeam = data.servicePlan.careTeam;
    consentToReceiveCarePlan = data.servicePlan.consentToReceiveCarePlan;
  }

  const { servicePlan, ...restOfData } = data || {};

  return {
    signatures,
    dataWithoutServicePlan: restOfData,
    mentalAcuityLevel,
    narratives,
    careTeam,
    consentToReceiveCarePlan,
  };
}

const ShowCustomerServicePlan = ({
  template,
  result,
  changeHaveCustomServicePlan,
  changeMentalAcuityLevel,
  changeNarratives,
  changeCareTeam,
  changeConsentToReceiveCarePlan,
}) => {
  const [allRole, setAllRole] = useState([]);
  const apiServiceUrl = process.env.REACT_APP_REMOTE_SERVER_URL;

  useEffect(() => {
    fetch(`${apiServiceUrl}/directory/system-roles/data`)
      .then((res) => res.json())
      .then((data) => {
        setAllRole(data);
      });
  }, []);

  const { template: filterTemplate, haveCustomServicePlan } = removeServicePlan(JSON.parse(template));

  const { signatures, dataWithoutServicePlan, mentalAcuityLevel, narratives, careTeam, consentToReceiveCarePlan } =
    extractSignatureAndRemoveServicePlan(result ? JSON.parse(result) : {});

  useEffect(() => {
    changeHaveCustomServicePlan(haveCustomServicePlan);
  }, [haveCustomServicePlan]);

  useEffect(() => {
    changeNarratives(narratives);
  }, [narratives]);

  useEffect(() => {
    changeCareTeam(careTeam);
  }, [careTeam]);

  useEffect(() => {
    changeConsentToReceiveCarePlan(consentToReceiveCarePlan);
  }, [consentToReceiveCarePlan]);

  useEffect(() => {
    changeMentalAcuityLevel(mentalAcuityLevel);
  }, [mentalAcuityLevel]);

  const survey = new Model(JSON.parse(template));

  useEffect(() => {
    initSurveyCustomComponent();

    survey.applyTheme(PlainLight);
    survey.mode = "display";
  }, [survey]);

  return (
    <>
      {filterTemplate.pages[0].elements.length > 0 && <Survey model={survey} data={result ? JSON.parse(result) : {}} />}

      {haveCustomServicePlan && signatures.length > 0 && (
        <div className={"servicePlanViewSignatureBox"}>
          {signatures.map((item, index) => {
            const matchedRole = allRole.find((role) => role.id === item.role);
            const roleTitle = matchedRole ? matchedRole.title : "";

            return (
              <div className={"servicePlanViewSignature"} key={index}>
                <div>Signature</div>
                <div>Name: {item.name}</div>
                <div>Role: {roleTitle}</div>
                <div>Date: {moment(item.data).format("DD/MM/YYYY HH:mm:ss")}</div>
                <img src={`${item.signature}`} alt="" />
              </div>
            );
          })}
        </div>
      )}
    </>
  );
};

export default ShowCustomerServicePlan;
