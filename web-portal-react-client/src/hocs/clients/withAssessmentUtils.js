import React, { useMemo, useCallback } from "react";

import { SurveyPDF } from "survey-pdf";

import { useSelector } from "react-redux";

import { useBoundActions } from "hooks/common/redux";

import { load as loadSurveyAction } from "redux/directory/assessment/survey/assessmentSurveyActions";

import { load as loadDetailsAction } from "redux/client/assessment/details/assessmentDetailsActions";

import { download as downloadDetailsAction } from "redux/client/assessment/details/assessmentDetailsActions";

import { ASSESSMENT_TYPES } from "lib/Constants";
import { lc, DateUtils } from "lib/utils/Utils";

const { format, formats } = DateUtils;

const { ARIZONA_SSM, COMPREHENSIVE, NOR_CAL_COMPREHENSIVE } = ASSESSMENT_TYPES;

const STANDARD_SURVEY_GENERAL_PANEL = {
  type: "panel",
  name: "General",
  title: "General",
  elements: [
    {
      type: "text",
      name: "Date started",
      title: "Date started",
      readOnly: true,
      hideNumber: true,
      width: "50%",
    },
    {
      type: "text",
      name: "Completed by",
      title: "Completed by",
      maxLength: 256,
      readOnly: true,
      hideNumber: true,
      startWithNewLine: false,
      width: "50%",
    },
  ],
};

const ARIZONA_SSM_SURVEY_GENERAL_PANEL = {
  type: "panel",
  name: "General",
  title: "General",
  elements: [
    {
      type: "text",
      name: "Assessment date",
      title: "Assessment date",
      readOnly: true,
      hideNumber: true,
      startWithNewLine: false,
      width: "50%",
    },
    {
      type: "text",
      name: "Date Updated",
      title: "Date Updated",
      readOnly: true,
      hideNumber: true,
      startWithNewLine: false,
      width: "50%",
    },
  ],
};

const SURVEY_COMMENT = {
  type: "comment",
  name: "comment",
  title: "Comment",
  maxLength: 5000,
  hideNumber: true,
};
// SurveyPDF样式
const SURVEY_PDF_OPTIONS = {
  mode: "display",
  fontSize: 14,
  margins: {
    left: 10,
    right: 10,
    top: 10,
    bot: 10,
  },
  format: [210, 297],
  commercial: true,
  textFieldRenderAs: "multiLine",
};

const selectClient = (state) => state.client.details.data ?? {};

const selectAssessmentTypes = (state) => state.directory.assessment.type.list.dataSource.data;

function prepareStandardAssessmentSurvey(json) {
  try {
    const parsed = JSON.parse(json);

    const page = parsed.pages[0];

    const title = page.title;
    const elements = page.elements;

    page.title = "";
    page.elements = [
      STANDARD_SURVEY_GENERAL_PANEL,
      {
        title,
        name: title,
        type: "panel",
        elements: [...elements, SURVEY_COMMENT],
      },
    ];

    return JSON.stringify(parsed);
  } catch (e) {
    return json;
  }
}

function prepareArizonaSSMAssessmentSurvey(json) {
  try {
    const parsed = JSON.parse(json);

    const page = parsed.pages[0];

    const title = page.title;
    const elements = page.elements;
    page.title = "";
    page.elements = [
      ARIZONA_SSM_SURVEY_GENERAL_PANEL,
      {
        title,
        name: title,
        type: "panel",
        elements: [...elements, SURVEY_COMMENT],
      },
    ];
    return JSON.stringify(parsed);
  } catch (e) {
    return json;
  }
}

const withAssessmentUtils = (Component) => {
  return function (props) {
    const client = useSelector(selectClient);
    const assessmentTypes = useSelector(selectAssessmentTypes);
    const loadSurvey = useBoundActions(loadSurveyAction); // 下载assessment
    const loadDetails = useBoundActions(loadDetailsAction);
    const downloadDetails = useBoundActions(downloadDetailsAction);

    const getTypeByName = useCallback(
      (name) => {
        return assessmentTypes
          .map((o) => o.types)
          .flat()
          .find((type) => type.name === name);
      },
      [assessmentTypes],
    );

    const getTypesByNames = useCallback(
      (names) => {
        return names.map(getTypeByName).flat();
      },
      [getTypeByName],
    );

    const downloadJson = useCallback(
      (id) => {
        return downloadDetails(client.id, id);
      },
      [client, downloadDetails],
    );

    // 下载pdf
    const downloadPdf = useCallback(
      (id, typeName) => {
        const type = getTypeByName(typeName);

        return Promise.all([
          loadSurvey({
            typeId: type.id,
            clientId: client.id,
          }),
          loadDetails(client.id, id),
        ]).then(([{ data: json }, { data }]) => {
          if (typeName === ARIZONA_SSM) {
            json = prepareArizonaSSMAssessmentSurvey(json);
          } else if (!typeName?.includes(COMPREHENSIVE)) {
            json = prepareStandardAssessmentSurvey(json);
          }
          const surveyPDF = new SurveyPDF(json, SURVEY_PDF_OPTIONS);

          surveyPDF.mode = "display";

          try {
            const parsed = JSON.parse(data.dataJson);
            surveyPDF.data = {
              ...parsed,
              comment: data.comment,
              ...(typeName === ARIZONA_SSM && {
                "Assessment date": format(data.dateCompleted, formats.longDateMediumTime12),
                "Date Updated": format(data.dateUpdated, formats.longDateMediumTime12),
              }),
              ...(typeName === NOR_CAL_COMPREHENSIVE && {
                "Date updated": format(data.dateUpdated, formats.longDateMediumTime12),
                "Date started": format(data.dateAssigned, formats.longDateMediumTime12),
              }),
            };

            surveyPDF.pages.forEach((page) => {
              if (page.questions?.every((q) => q.isEmpty())) page.visible = false;
            });

            surveyPDF.getAllPanels().forEach((panel) => {
              if (panel.questions?.every((q) => q.isEmpty())) panel.visible = false;
            });

            surveyPDF.getAllQuestions().forEach((question) => {
              if (question.isEmpty()) question.visible = false;
              if (question.name === "Date updated") question.visible = true;
            });

            const startDate = new Date(parsed["Date started"] || data.dateAssigned);

            const { firstName, lastName } = client;
            const clientInitials = firstName.charAt(0) + lastName.charAt(0);

            const typeText = typeName?.includes(COMPREHENSIVE) ? lc(typeName) : typeName;

            surveyPDF.save(`${clientInitials} ${typeText} assessment ${format(startDate, "MM-dd-YYYY")}`);
          } catch (e) {
            console.error("Assessment data from the server are not valid.");
          }
        });
      },
      [client, getTypeByName, loadDetails, loadSurvey],
    );

    const utils = useMemo(
      () => ({
        getTypesByNames,
        getTypeByName,
        downloadJson,
        downloadPdf,
      }),
      [getTypesByNames, getTypeByName, downloadJson, downloadPdf],
    );

    return <Component {...props} assessmentUtils={utils} />;
  };
};

export default withAssessmentUtils;
