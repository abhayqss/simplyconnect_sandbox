import { ComponentCollection, Serializer } from "survey-core";

const apiServiceUrl = process.env.REACT_APP_REMOTE_SERVER_URL;

export function initServicePlan() {
  if (!ComponentCollection.Instance.getCustomQuestionByName("serviceplan")) {
    ComponentCollection.Instance.add({
      name: "serviceplan",
      title: "Service Plan",
      elementsJSON: [
        {
          type: "html",
          name: "rendered-html",
          html: "<b style='font-size: 26px'>Summary</b>",
        },
        {
          title: "Date Created",
          name: "dateCreated",
          type: "text",
          inputType: "datetime-local",
          readOnly: true,
          isRequired: true,
          // defaultValue: new Date(),
        },
        {
          title: "Created By",
          name: "createdBy",
          type: "text",
          readOnly: true,
          isRequired: true,
          // defaultValue: userInfo.fullName,
          startWithNewLine: false,
        },

        {
          name: "mentalAcuityLevel",
          title: "Acuity Level",
          type: "dropdown",
          placeholder: "Select",
          choices: [
            {
              text: "High",
              value: 3,
            },
            {
              text: "Medium",
              value: 2,
            },
            {
              text: "Low",
              value: 1,
            },
          ],
        },

        {
          type: "comment",
          name: "narratives",
          title: "Narratives",
          rows: 2,
          autoGrow: true,
          allowResize: false,
        },
        {
          type: "text",
          name: "careTeam",
          title: "Care Team",
          hideNumber: true,
        },
        {
          type: "radiogroup",
          name: "consentToReceiveCarePlan",
          title: "Member received care plan",
          hideNumber: true,
          choices: [
            {
              value: "Yes",
              text: "Yes",
            },
            {
              value: "No",
              text: "No",
            },
          ],
        },
        {
          type: "boolean",
          name: "isCompleted",
          titleLocation: "hidden",
          hideNumber: true,
          renderAs: "checkbox",
          label: "Mark service plan as completed",
        },
        {
          type: "html",
          name: "needsTitle",
          html: "<b style='font-size: 26px'>Needs / Opportunities</b>",
        },

        {
          type: "paneldynamic",
          name: "needs",
          title: "Needs / Opportunities",
          panelCount: 0,
          confirmDelete: true,
          state: "expanded",
          templateTitle: "Needs / Opportunities {panelIndex}",
          noEntriesText: "Click the button below to add a new need.",
          panelAddText: "Add New",
          templateElements: [
            {
              name: "domainId",
              title: "Domain",
              type: "dropdown",
              isRequired: true,
              searchEnabled: true,
              placeholder: "Select a Domain...",
              choicesByUrl: {
                url: `${apiServiceUrl}/directory/service-plan-domains/data`,
                valueName: "id",
                titleName: "title",
              },
            },

            {
              name: "programTypeId",
              title: "Program Type",
              type: "dropdown",
              visibleIf: true,
              isRequired: false,
              searchEnabled: true,
              placeholder: "Select a Program Type...",
              choicesByUrl: {
                url: `${apiServiceUrl}/directory/service-plan-program-types/data?domainId={panel.domainId}`,
                valueName: "id",
                titleName: "title",
              },
            },

            {
              name: "programSubTypeId",
              title: "Program Sub Type",
              type: "dropdown",
              visibleIf: true,
              isRequired: false,
              searchEnabled: true,
              placeholder: "Select a Program Sub Type...",
              choicesByUrl: {
                url: `${apiServiceUrl}/directory/service-plan-program-subtypes/data?programType={panel.programTypeId}`,
                valueName: "id",
                titleName: "title",
              },
            },

            {
              name: "priorityId",
              title: "Priority",
              type: "dropdown",
              isRequired: true,
              placeholder: "Select a priority...",
              choices: [
                { value: 3, name: "HIGH", text: "High-3 months" },
                { value: 2, name: "MEDIUM", text: "Medium- 6 months" },
                { value: 1, name: "LOW", text: "Low- 12 months" },
              ],
              // choicesByUrl: {
              //   url: `${apiServiceUrl}/directory/service-plan-priorities/data`,
              //   valueName: "id",
              //   titleName: "title",
              // },
            },
            {
              type: "text",
              name: "needOpportunity",
              title: "Need / Opportunity",
              isRequired: true,
            },

            {
              type: "text",
              name: "proficiencyGraduationCriteria",
              title: "Proficiency / Graduation Criteria",
            },
            {
              type: "paneldynamic",
              name: "goals",
              title: "Goals",
              // panelCount: 0,
              confirmDelete: true,
              state: "expanded",
              templateTitle: "Goals {panelIndex}",
              noEntriesText: "Click the button below to add a new goal.",
              panelAddText: "Add New",
              templateElements: [
                {
                  type: "text",
                  name: "goal",
                  title: "Goal",
                  isRequired: true,
                },
                {
                  type: "comment",
                  name: "barriers",
                  title: "Barriers",
                  rows: 2,
                  autoGrow: true,
                  allowResize: false,
                },
                {
                  type: "comment",
                  name: "strengths",
                  title: "Strengths",
                  rows: 2,
                  autoGrow: true,
                  allowResize: false,
                },
                {
                  type: "comment",
                  name: "interventionAction",
                  title: "Intervention/Action",
                  rows: 2,
                  autoGrow: true,
                  allowResize: false,
                },
                {
                  type: "html",
                  name: "rendered-html",
                  html: "<b style='font-size: 20px'>Service</b>",
                },
                {
                  type: "text",
                  name: "providerName",
                  title: "Provider Name",
                },
                {
                  type: "text",
                  name: "providerEmail",
                  title: "Provider Email",
                  inputType: "email",
                  startWithNewLine: false,
                },
                {
                  type: "text",
                  name: "providerPhone",
                  title: "Provider Phone",
                  inputType: "tel",
                  placeholder: "+{Country Code} {National Number}",
                  startWithNewLine: false,
                },
                {
                  type: "text",
                  name: "providerAddress",
                  title: "Address",
                },
                {
                  type: "boolean",
                  name: "wasPreviouslyInPlace",
                  titleLocation: "hidden",
                  hideNumber: true,
                  renderAs: "checkbox",
                  label: "Service was previously in place",
                },
                {
                  type: "text",
                  name: "resourceName",
                  title: "Resource Name",
                },
                {
                  type: "boolean",
                  name: "isOngoingService",
                  title: "Ongoing Service?",
                  valueTrue: 1,
                  valueFalse: 0,
                  renderAs: "radio",
                  isRequired: true,
                },

                {
                  type: "text",
                  name: "contactName",
                  title: "Contact Name",
                },
                {
                  name: "serviceCtrlReqStatusId",
                  title: "Request Status",
                  type: "dropdown",
                  placeholder: "Select",
                  choices: [
                    {
                      text: "Accepted",
                      value: 1,
                    },
                    {
                      text: "Declined",
                      value: 2,
                    },
                  ],
                  startWithNewLine: false,
                },
                {
                  name: "serviceStatusId",
                  title: "Service Status",
                  type: "dropdown",
                  placeholder: "Select",
                  choices: [
                    // {
                    //   text: "Pending",
                    //   value: 1,
                    // },
                    {
                      text: "In process",
                      value: 2,
                    },
                    {
                      text: "Completed",
                      value: 3,
                    },
                    {
                      text: "Other",
                      value: 4,
                    },
                  ],
                  startWithNewLine: false,
                },
                {
                  title: "Target Completion Date",
                  name: "targetCompletionDate",
                  type: "text",
                  inputType: "datetime-local",
                  isRequired: true,
                },
                {
                  title: "Completion Date",
                  name: "completionDate",
                  type: "text",
                  inputType: "datetime-local",
                  startWithNewLine: false,
                },
                {
                  type: "text",
                  inputType: "number",
                  name: "goalCompletion",
                  title: "Goal Completion, %",
                  startWithNewLine: false,
                },
              ],
            },
          ],
        },
        {
          type: "html",
          name: "needsTitle",
          html: "<b style='font-size: 26px'>Signature</b>",
        },
        {
          type: "paneldynamic",
          name: "signature",
          title: "Signature",
          panelCount: 0,
          confirmDelete: true,
          hideNumber: true,
          state: "expanded",
          templateTitle: "Signature {panelIndex}",
          noEntriesText: "Click the button below to add a new signature.",
          panelAddText: "Add New",
          templateElements: [
            {
              type: "signaturepad",
              name: "signature",
              title: "Signature",
              hideNumber: true,
              signatureWidth: 600,
              isRequired: true,
            },
            {
              type: "text",
              name: "name",
              title: "Name",
              isRequired: true,
            },
            {
              name: "role",
              title: "Role",
              type: "dropdown",
              placeholder: "Select",
              isRequired: true,
              choicesByUrl: {
                url: `${apiServiceUrl}/directory/system-roles/data`,
                valueName: "id",
                titleName: "title",
              },
            },
            {
              title: "Date",
              name: "date",
              type: "text",
              inputType: "datetime-local",
              readOnly: false,
              isRequired: true,
              // defaultValue: new Date(),
            },
          ],
        },
      ],
    });

    const isHiddenTitle = Serializer.getProperty("serviceplan", "titleLocation");
    isHiddenTitle.defaultValue = "hidden";
  }
}
