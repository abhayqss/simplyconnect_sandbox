import React, { Component } from "react";

import PropTypes from "prop-types";

import { map, sortBy, isNumber } from "underscore";

import "./ServicePlanScoring.scss";

import Table from "components/Table/Table";
import RangeSlider from "components/RangeSlider/RangeSlider";

import { SERVICE_STATUSES, SERVICE_PLAN_NEED_DOMAINS } from "lib/Constants";

import { isNotEmpty, DateUtils as DU } from "lib/utils/Utils";

const COLORS = ["#53b865", "#ffd529", "#f36c32"];

const { PENDING, IN_PROCESS, COMPLETED, OTHER } = SERVICE_STATUSES;

const SERVICE_STATUS_COLORS = {
  [PENDING]: "#e0e0e0",
  [IN_PROCESS]: "#fff1ca",
  [COMPLETED]: "#d5f3b8",
  [OTHER]: "#c9e5ff",
};

const { format, formats } = DU;

const DATE_FORMAT = formats.americanMediumDate;

const { EDUCATION_TASK } = SERVICE_PLAN_NEED_DOMAINS;

export default class ServicePlanScoring extends Component {
  static propTypes = {
    isDisabled: PropTypes.bool,
    onChangeScore: PropTypes.func,
  };

  static defaultProps = {
    isDisabled: false,
    onChangeScore: () => {},
  };

  render() {
    const { data, isDisabled, onChangeScore } = this.props;

    return (
      <div className="ServicePlanScoring">
        {map(data, ({ domainId, domainName, domainTitle, score, needs }) => (
          <div className="ServicePlanScoring-Section">
            {domainName === EDUCATION_TASK ? (
              <>
                <div className="ServicePlanScoring-SectionTitle">{domainTitle}</div>
                <div className="ServicePlanActivationOrEducationTasks">
                  <Table
                    keyField="id"
                    className="ServicePlanActivationOrEducationTaskList"
                    data={needs}
                    columns={[
                      {
                        dataField: "id",
                        text: "",
                        headerStyle: {
                          width: "32px",
                          padding: "15px 12px",
                        },
                        style: (cell, row) => {
                          const color = COLORS[row.priorityId - 1];

                          return {
                            fontWeight: 600,
                            padding: "15px 12px",
                            borderLeftColor: color,
                            backgroundColor: color,
                            borderBottomColor: color,
                          };
                        },
                        formatter: (v, row, rowIndex) => rowIndex + 1,
                      },
                      {
                        dataField: "activationOrEducationTask",
                        text: "Activation or Education Task",
                        headerStyle: {
                          verticalAlign: "top",
                        },
                      },
                      {
                        dataField: "programTypeId",
                        text: "Program type / Sub type",
                        headerStyle: {
                          verticalAlign: "top",
                        },
                        style: {
                          verticalAlign: "top",
                        },
                        formatter: (v, row) =>
                          isNumber(v) ? (
                            <>
                              <div>
                                {row.programTypeTitle +
                                  (isNumber(row.programSubTypeId) ? " / " + row.programSubTypeTitle : "")}
                              </div>
                              {isNumber(row.programSubTypeId) && (
                                <div>
                                  {row.programSubTypeZCode}: {row.programSubTypeZCodeDesc}
                                </div>
                              )}
                            </>
                          ) : (
                            ""
                          ),
                      },
                      {
                        dataField: "targetCompletionDate",
                        text: "Target Completion Date",
                        headerStyle: {
                          width: "105px",
                          textAlign: "right",
                          verticalAlign: "top",
                        },
                        style: {
                          textAlign: "center",
                        },
                        formatter: (v) => v && format(v, DATE_FORMAT),
                      },
                      {
                        dataField: "completionDate",
                        text: "Completion Date",
                        headerStyle: {
                          width: "105px",
                          textAlign: "right",
                          verticalAlign: "top",
                        },
                        style: {
                          textAlign: "center",
                        },
                        formatter: (v) => v && format(v, DATE_FORMAT),
                      },
                    ]}
                  />
                </div>
              </>
            ) : (
              <>
                <div className="d-flex flex-row justify-content-between">
                  <div className="ServicePlanScoring-SectionTitle">{domainTitle}</div>
                  <RangeSlider
                    min={0}
                    max={5}
                    step={1}
                    value={score}
                    isDisabled={isDisabled}
                    onChange={(v) => {
                      onChangeScore(domainId, v);
                    }}
                    className="ServicePlanScoring-ScoreSlider"
                  />
                </div>
                <div className="padding-top-5">
                  {map(needs, (need, i) => (
                    <div className="ServicePlanNeedDetails">
                      <div className="ServicePlanNeedDetails-Summary">
                        <div
                          className="ServicePlanNeedDetails-Priority"
                          style={{ backgroundColor: COLORS[need.priorityId - 1] }}
                        >
                          {i + 1}
                        </div>
                        <div className="flex-1">
                          {need.programTypeId && (
                            <div className="ServicePlanNeedDetail">
                              <div className="ServicePlanNeedDetail-Title">Program type / Sub type</div>
                              <div className="ServicePlanNeedDetail-Text">
                                {need.programTypeTitle}
                                {need.programSubTypeId ? ` / ${need.programSubTypeTitle}` : ""}
                              </div>
                              {need.programSubTypeZCode && (
                                <div className="ServicePlanNeedDetail-Text">
                                  {need.programSubTypeZCode}: {need.programSubTypeZCodeDesc}
                                </div>
                              )}
                            </div>
                          )}
                          <div className="ServicePlanNeedDetail">
                            <div className="ServicePlanNeedDetail-Title">Need / Opportunity</div>
                            <div className="ServicePlanNeedDetail-Text">{need.needOpportunity}</div>
                          </div>
                          {need.proficiencyGraduationCriteria && (
                            <div className="ServicePlanNeedDetail">
                              <div className="ServicePlanNeedDetail-Title">Proficiency / Graduation Criteria</div>
                              <div className="ServicePlanNeedDetail-Text">{need.proficiencyGraduationCriteria}</div>
                            </div>
                          )}
                        </div>
                      </div>
                      {isNotEmpty(need.goals) && (
                        <div className="ServicePlanNeedDetails-Goals">
                          <Table
                            keyField="id"
                            className="ServicePlanGoalList"
                            data={sortBy(need.goals, (o) => -(o.targetCompletionDate || o.goalCompletion))}
                            columns={[
                              {
                                dataField: "goal",
                                text: "Goal",
                                headerStyle: {
                                  verticalAlign: "top",
                                },
                              },
                              {
                                dataField: "goalCompletion",
                                text: "%",
                                headerStyle: {
                                  textAlign: "right",
                                  verticalAlign: "top",
                                },
                                style: {
                                  fontWeight: 600,
                                  textAlign: "right",
                                },
                              },
                              {
                                dataField: "barriers",
                                text: "Barriers",
                                headerStyle: {
                                  verticalAlign: "top",
                                },
                              },
                              {
                                dataField: "strengths",
                                text: "Strengths",
                                headerStyle: {
                                  verticalAlign: "top",
                                },
                              },
                              {
                                dataField: "interventionAction",
                                text: "Intervention / Action",
                                headerStyle: {
                                  verticalAlign: "top",
                                },
                              },
                              {
                                dataField: "resourceName",
                                text: "Service",
                                headerStyle: {
                                  verticalAlign: "top",
                                },
                                formatter: (v, row) => (
                                  <>
                                    <div>{v}</div>
                                    <div className="margin-top-10">{row.providerName}</div>
                                    {row.serviceStatusName && (
                                      <div
                                        className="ServicePlanGoal-Status margin-top-10"
                                        style={{
                                          backgroundColor: SERVICE_STATUS_COLORS[row.serviceStatusName],
                                        }}
                                      >
                                        {row.serviceStatusTitle}
                                      </div>
                                    )}
                                    {row.wasPreviouslyInPlace && (
                                      <div className="margin-top-10">Service was previously in place</div>
                                    )}
                                  </>
                                ),
                              },
                              {
                                dataField: "targetCompletionDate",
                                text: "Target Completion Date",
                                headerStyle: {
                                  textAlign: "right",
                                  verticalAlign: "top",
                                },
                                style: {
                                  textAlign: "right",
                                },
                                formatter: (v) => v && format(v, DATE_FORMAT),
                              },
                              {
                                dataField: "completionDate",
                                text: "Completion Date",
                                headerStyle: {
                                  textAlign: "right",
                                  verticalAlign: "top",
                                },
                                style: {
                                  textAlign: "right",
                                },
                                formatter: (v) => v && format(v, DATE_FORMAT),
                              },
                            ]}
                            columnsMobile={["goal", "resourceName"]}
                          />
                        </div>
                      )}
                    </div>
                  ))}
                </div>
              </>
            )}
          </div>
        ))}
      </div>
    );
  }
}
