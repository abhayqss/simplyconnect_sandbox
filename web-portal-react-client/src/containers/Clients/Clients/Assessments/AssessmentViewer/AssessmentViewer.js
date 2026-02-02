import React, { Component } from "react";

import $ from "jquery";
import cn from "classnames";

import { renderToStaticMarkup } from "react-dom/server";

import { connect } from "react-redux";
import { bindActionCreators } from "redux";

import { withRouter } from "react-router-dom";

import { any, each, filter, find, findWhere, isNumber, map, reject, where } from "underscore";

import PropTypes from "prop-types";
import * as Survey from "survey-react";
import * as widgets from "surveyjs-widgets";
import { Model } from "survey-core";
import { Survey as SurveyUI } from "survey-react-ui";
import "survey-core/defaultV2.min.css";
import { PlainLight } from "survey-core/themes";

import { Button, Col, Row } from "reactstrap";

import { Alert, Dropdown, Loader, Modal, Table, Tabs } from "components";

import { DateField, TextField } from "components/Form";

import * as assessmentSurveyActions from "redux/directory/assessment/survey/assessmentSurveyActions";
import * as assessmentDetailsActions from "redux/client/assessment/details/assessmentDetailsActions";
import * as assessmentHistoryActions from "redux/client/assessment/history/assessmentHistoryActions";
import * as assessmentManagementActions from "redux/directory/assessment/management/assessmentManagementActions";

import { ASSESSMENT_STATUSES, ASSESSMENT_TYPES, PAGINATION } from "lib/Constants";

import { Response } from "lib/utils/AjaxUtils";
import { DateUtils, isEmpty } from "lib/utils/Utils";

import "./AssessmentViewer.scss";
import { questionList, ScoringSummaryData } from "../AssessmentEditor/AssessmentEditor";

const { format, formats } = DateUtils;

const DATE_FORMAT = formats.longDateMediumTime12;

const { FIRST_PAGE } = PAGINATION;

const {
  PHQ9,
  GAD7,
  HOUSING,
  CARE_MGMT,
  ARIZONA_SSM,
  COMPREHENSIVE,
  HMIS_ADULT_CHILD_INTAKE,
  HMIS_ADULT_CHILD_REASESSMENT,
  HMIS_ADULT_CHILD_REASESSMENT_EXIT,
  BENEFICIARY_COLORECTAL_CANCER_SCREENING,
  BENEFICIARY_MAMMOGRAM_SCREENING,
  BENEFICIARY_SCREENING_FOR_TYPE_II_DIABETES,
  BENEFICIARY_SCREENING_DIABETIC_FOOT_AND_EYE_EXAM,
  FAST,
  SCREEN,
  SHORT,
  REVISED,
} = ASSESSMENT_TYPES;

const { COMPLETED } = ASSESSMENT_STATUSES;

const STATUS_COLORS = {
  INACTIVE: "#e0e0e0",
  COMPLETED: "#d1ebfe",
  IN_PROCESS: "#d5f3b8",
};

const ASSESSMENT_TABS = ["Assessment Details", "Change History"];

const COMPREHENSIVE_ASSESSMENT_TABS = [
  "Guide",
  "Demographics",
  "Medical History",
  "WellRx",
  "Additional Questions",
  "Behavioral Health",
  "Engagement",
  "Change History",
];

let majorSurveyPanels = [];
let minorSurveyPanels = [];

const LEVELS_ALERT_TYPES = {
  LOW: "success",
  MEDIUM: "warning",
  HIGH: "danger",
};

function mapStateToProps(state) {
  return {
    assessment: state.client.assessment,
    directory: state.directory,
    client: state.client,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: {
      details: bindActionCreators(assessmentDetailsActions, dispatch),
      history: bindActionCreators(assessmentHistoryActions, dispatch),

      survey: bindActionCreators(assessmentSurveyActions, dispatch),
      management: bindActionCreators(assessmentManagementActions, dispatch),
    },
  };
}

class AssessmentViewer extends Component {
  static propTypes = {
    isOpen: PropTypes.bool,

    assessmentId: PropTypes.number,

    onView: PropTypes.func,
    onClose: PropTypes.func,
  };

  static defaultProps = {
    onView: function () {},
    onClose: function () {},
  };

  formRef = React.createRef();
  modalRef = React.createRef();

  state = {
    step: 0,
    scrollOffset: 0,
    isReviewMode: false,
    surveyModel: null,
    surveyData: null,
    invalidSurveyPageIndexes: [],
  };

  componentDidMount() {
    //  load assessment document information
    this.loadSurvey().then(
      Response(({ data }) => {
        const model = new Model(JSON.parse(data));
        model.applyTheme(PlainLight);
        model.mode = "display";

        this.setState(
          {
            surveyModel: model,
          },
          () => {
            // 初始化完成后设置页面和数据
            this.updateSurveyModelState();
          },
        );
      }),
    );

    this.refreshDetails();
    //  load assessment management information
    this.loadManagement();

    const type = this.getAssessmentType();
    //  COMPREHENSIVE 综合的
    if (type?.name?.includes(COMPREHENSIVE)) {
      this.setState({ step: 0 });
    }

    Survey.JsonObject.metaData.addProperty("panel", "isNavigable:boolean");
    Survey.JsonObject.metaData.addProperty("panel", "isExpandable:boolean");
    Survey.JsonObject.metaData.addProperty("panel", "isNavDestination:boolean");
    Survey.JsonObject.metaData.addProperty("panel", "panelAnchor:text");

    Survey.JsonObject.metaData.addProperty("question", "isPriority:boolean");

    widgets.prettycheckbox(Survey, $); // 自定义了多选框
  }

  componentDidUpdate(prevProps, prevState) {
    const {
      isAssessmentArchived,
      assessment: { details },
    } = this.props;

    if (!isAssessmentArchived && details.shouldReload) {
      this.refreshDetails();
    }

    const { step, surveyModel, surveyData } = this.state;

    if (step !== prevState.step) {
      const modal = this.modalRef.current;
      if (modal) modal.scrollTop(0);

      // 更新 surveyModel 的页面状态
      this.updateSurveyModelState();

      if (step === this.getTabs().length - 1) {
        this.refreshChangeHistory();
      }
    }

    // 如果 surveyData 发生变化，更新模型数据
    if (surveyData !== prevState.surveyData && surveyModel && surveyData) {
      surveyModel.data = surveyData;
    }
  }

  componentWillUnmount() {
    if (this.props.isAssessmentArchived) {
      // 评估存档
      this.actions.details.clear(true);
    } else {
      this.actions.survey.clear();
      this.actions.details.clear();
      this.actions.management.clear();
    }
  }

  onClose = () => {
    this.props.onClose();
  };

  onNext = () => {
    const { step } = this.state;
    const tabs = this.getTabs();
    if (step < tabs.length - 1) this.changeStep(step + 1);
  };

  onBack = () => {
    const { step } = this.state;
    if (step > 0) this.changeStep(step - 1);
  };

  onView = (data) => {
    this.props.onView(data);
  };

  onChangeTab = (step) => {
    this.changeStep(step);
  };

  onAfterRenderSurveyPage = (survey, options) => {
    const { page, htmlElement } = options;

    const $page = $(htmlElement);
    $page.addClass("SurveyPage");

    if (this.state.isReviewMode) {
      const $nav = $(renderToStaticMarkup(<div className="SurveyPage-Nav" />));

      each(where(majorSurveyPanels, { isExpandable: true }), ({ name }) => {
        $nav.append(
          renderToStaticMarkup(
            <a href={`#${name}`} data-nav-target={name} className="SurveyPage-NavLink">
              {name}
            </a>,
          ),
        );
      });

      $page.prepend($nav);
    } else {
      const $nav = $page.find(".SurveyPage-Nav");
      $nav.remove();
    }

    const $expandAll = $page.find(".sv-expand-all-sections-btn");
    const $collapseAll = $page.find(".sv-collapse-all-sections-btn");

    $expandAll.closest("div").addClass("sv-expand-collapse-btns");

    $page.find(".ExpandableSurveyPanel");

    $expandAll.on("click", () => {
      $.each(page.rows, (i, row) => {
        $.each(row.elements, (j, elem) => {
          if (elem.isPanel) {
            elem.expand();
            this.areAllPanelsExpanded = true;
            this.areAllPanelsCollapsed = false;

            $page.find(".SurveyPanel-Nav").show();
          }
        });
      });
    });

    $collapseAll.on("click", () => {
      $.each(page.rows, (i, row) => {
        $.each(row.elements, (j, elem) => {
          if (elem.isPanel) {
            elem.collapse();
            this.areAllPanelsCollapsed = true;
            this.areAllPanelsExpanded = false;

            $page.find(".SurveyPanel-Nav").hide();
          }
        });
      });
    });
  };

  onAfterRenderSurveyPanel = (survey, options) => {
    const { panel, htmlElement } = options;

    const { name, title, isNavigable, isExpandable, isNavDestination } = panel;

    const $panel = $(htmlElement);
    $panel.addClass("SurveyPanel");

    const $title = $panel.find(".SurveyPanel-Title").eq(0);

    $title.on("click", (e) => {
      if (isReviewMode) e.stopPropagation();
    });

    const { isReviewMode } = this.state;

    if (isExpandable || isNavigable) {
      let panel = findWhere(majorSurveyPanels, { name });

      if (!panel) {
        panel = {
          name,
          title,
          isNavigable,
          isExpandable,
          isNavDestination,
          element: htmlElement,
        };

        majorSurveyPanels.push(panel);
      }

      each(minorSurveyPanels, (p) => {
        if (!p.navigableParent) {
          p.navigableParent = panel;
        }
      });
    }

    if (isExpandable) {
      $panel.addClass("ExpandableSurveyPanel");
    }

    if (isNavigable) {
      const hasNav = $panel.find(".SurveyPanel-Nav").length > 0;

      const navDestinations = filter(minorSurveyPanels, (p) => p.navigableParent.name === name);

      if (!hasNav && navDestinations.length > 0) {
        $panel.addClass("NavigableSurveyPanel");

        const $nav = $(renderToStaticMarkup(<div style={{ display: "none" }} className="SurveyPanel-Nav" />));

        each(navDestinations, ({ name, title }) => {
          $nav.append(
            renderToStaticMarkup(
              <a href={`#${name}`} data-nav-target={name} className="SurveyPanel-NavLink">
                {title}
              </a>,
            ),
          );
        });

        $title.after($nav);

        if (isExpandable) {
          const isExpanded = () => {
            return $title.hasClass("sv_p_title_expanded");
          };

          if (isExpanded()) $nav.show();

          $title.on("click", (e) => {
            if (isReviewMode) e.stopPropagation();
            else
              setTimeout(() => {
                if (isExpanded()) $nav.show();
                else $nav.hide();
              }, 100);
          });
        }
      }
    }

    if (isNavDestination) {
      if (!any(minorSurveyPanels, (p) => p.name === name)) {
        minorSurveyPanels.push({
          name,
          title,
          isNavigable,
          isExpandable,
          isNavDestination,
          element: htmlElement,
        });
      }

      $panel.attr("id", name).addClass("NavDestinationSurveyPanel");

      const borderClass = "SurveyMinorPanel-DecoratedBorder";

      if (!$panel.has(`.${borderClass}`).length) {
        $panel.prepend(renderToStaticMarkup(<div className={borderClass} />));
      }
    }
  };

  onAfterRenderSurveyQuestion = (survey, options) => {
    const { question, htmlElement } = options;

    const type = question.getType();

    const $elem = $(htmlElement);

    $elem
      // .css({ paddingRight: 0 })
      .addClass("SurveyQuestion_type_" + type)
      .closest(".SurveyRow")
      .addClass(type !== "boolean" ? "d-flex flex-row" : "");

    $elem
      .parent()
      .addClass("SurveyCell")
      .addClass(type === "boolean" ? "no-flex auto-width auto-min-width" : "");
  };

  onRefreshChangeHistory = (page) => {
    this.refreshChangeHistory(page);
  };

  get actions() {
    return this.props.actions;
  }

  get clientId() {
    return +this.props.match.params.clientId;
  }

  getHeaderHeight() {
    const o = this.modalRef.current;
    return o ? o.getHeaderHeight() : 73;
  }

  changeStep(step) {
    majorSurveyPanels = [];
    minorSurveyPanels = [];

    this.setState({ step });
  }

  updateSurveyModelState() {
    const { surveyModel, surveyData, step } = this.state;
    if (surveyModel) {
      // 设置数据
      if (surveyData) {
        surveyModel.data = surveyData;
      }

      // 设置当前页面 - 只有在 comprehensive 类型且有多页时才设置
      const type = this.getAssessmentType();
      if (type?.name?.includes(COMPREHENSIVE) && surveyModel.pages.length > step) {
        surveyModel.currentPageNo = step;
      } else if (!type?.name?.includes(COMPREHENSIVE)) {
        // 非 comprehensive 类型始终显示第一页
        surveyModel.currentPageNo = 0;
      }
    }
  }

  loadDetails() {
    return this.actions.details.load(this.clientId, this.props.assessmentId);
  }

  refreshDetails() {
    this.loadDetails().then(
      Response(({ data }) => {
        const { comment, statusTitle } = data;
        const surveyData = {
          ...JSON.parse(data.dataJson),
          Demographics_General_Status: statusTitle,
          Demographics_General_Comment: comment,
        };

        this.setState(
          {
            surveyData,
          },
          () => {
            // 数据加载完成后，更新 surveyModel 的状态
            this.updateSurveyModelState();
          },
        );
      }),
    );
  }

  loadSurvey() {
    return this.actions.survey.load({
      clientId: this.clientId,
      typeId: this.props.assessmentTypeId,
    });
  }

  loadManagement() {
    this.actions.management.load({
      clientId: this.clientId,
      typeId: this.props.assessmentTypeId,
    });
  }

  setScrollOffset(offset) {
    this.modalRef.current.setScrollOffset(offset);
  }

  updateChangeHistory(isReload, page) {
    const { isFetching, shouldReload, dataSource: ds } = this.props.assessment.history;

    if (isReload || shouldReload || (!isFetching && isEmpty(ds.data))) {
      const { size, page: p } = ds.pagination;

      this.actions.history.load({
        size,
        page: page || p,
        clientId: this.clientId,
        assessmentId: this.props.assessmentId,
      });
    }
  }

  refreshChangeHistory(page) {
    this.updateChangeHistory(true, page || FIRST_PAGE);
  }

  getSurveyQuestionByName(name) {
    return this.state.surveyModel.getQuestionByName(name);
  }

  getAssessmentType() {
    let type = null;

    const { directory, assessment, assessmentTypeId } = this.props;

    const id = assessmentTypeId || assessment.form.fields.typeId;

    if (id) {
      each(directory.assessment.type.list.dataSource.data, (o) => {
        const t = findWhere(o.types, { id });
        if (t) type = t;
      });
    }
    return type;
    /* canAdd true,
       id: 28;
       name:"ARIZONA_SSM";
       resultTitle: ;
       shortTitle: "Arizona Self-sufficiency Matrix Assessment";
       title:"Arizona Self-sufficiency Matrix Assessment"*/
  }

  getTabs() {
    return this.getAssessmentType()?.name?.includes(COMPREHENSIVE) ? COMPREHENSIVE_ASSESSMENT_TABS : ASSESSMENT_TABS;
  }

  getScoringAlert() {
    const { directory, assessment } = this.props;

    const { management: mg } = directory.assessment;

    const { score } = assessment.details.data || {};

    if (isNumber(score) && mg.data) {
      const item = find(mg.data.scale, (o) => score >= o.scoreLow && score <= o.scoreHigh);

      return item
        ? {
            text: item.severity,
            type: LEVELS_ALERT_TYPES[item.highlighting.toUpperCase()],
          }
        : {
            text: "Severe anxiety disorder.",
            type: LEVELS_ALERT_TYPES.HIGH,
          };
    }

    return null;
  }

  getScreeningResultsAlert() {
    const { directory, assessment } = this.props;

    const { management } = directory.assessment;
    const { score } = assessment.details.data || {};

    if (isNumber(score) && management.data) {
      const item = find(management.data.scale, (o) => score >= o.scoreLow && score <= o.scoreHigh);

      if (item.highlighting === "High") {
        return {
          ...item,
          type: "redmark",
        };
      } else if (item.highlighting === "Medium") {
        return {
          ...item,
          type: "yellowmark",
        };
      } else if (item.highlighting === "Low") {
        return {
          ...item,
          type: "greenmark",
        };
      }
    }
  }

  getMammogramResultsAlert() {
    const { assessment } = this.props;

    const { score } = assessment.details.data || {};
    if (score > 0) {
      return {
        degree: "Schedule Mammogram",
        type: "redmark",
      };
    } else if (score === 0) {
      return {
        degree: "Schedule Mammogram",
        type: "greenmark",
      };
    }
  }

  render() {
    const { step, surveyData, surveyModel, scrollOffset } = this.state;

    const {
      isOpen,
      client,
      assessmentId,
      assessment: { details, history },
      directory: {
        assessment: { management },
      },
      isAssessmentArchived,
    } = this.props;

    const tabs = this.getTabs();
    const type = this.getAssessmentType();

    const statusName = details.data?.statusName;

    const scoringAlert = [GAD7, PHQ9, SHORT, SCREEN, REVISED].includes(type?.name) && this.getScoringAlert();

    const colorectalAlert =
      [BENEFICIARY_COLORECTAL_CANCER_SCREENING].includes(type?.name) && this.getScreeningResultsAlert();

    const mammogramAlert = [BENEFICIARY_MAMMOGRAM_SCREENING].includes(type?.name) && this.getMammogramResultsAlert();

    const scaleList = management.data?.scale.map((o, i) => ({ ...o, id: i }));

    const { id, score, comment, contactName, dateAssigned, dateCompleted } = details.data || {};
    return (
      <Modal
        hasCloseBtn
        isOpen={isOpen}
        ref={this.modalRef}
        onClose={this.onClose}
        scrollOffset={scrollOffset}
        className={cn("AssessmentViewer", `AssessmentViewer-Assessment_type_${type.name.toLowerCase()}`)}
        title={`View ${type.title}`}
        headerClassName="AssessmentViewer-Header"
        footerClassName="AssessmentViewer-Footer"
        renderFooter={() => (
          <div className="AssessmentViewer-FooterBtns">
            <div>
              {type?.name?.includes(COMPREHENSIVE) && (
                <>
                  {step > 0 && (
                    <Button color="link" className="AssessmentViewer-BackBtn" onClick={this.onBack}>
                      Back
                    </Button>
                  )}
                  {step < tabs.length - 1 && (
                    <Button color="link" className="AssessmentViewer-NextBtn" onClick={this.onNext}>
                      Next
                    </Button>
                  )}
                </>
              )}
            </div>
            <Button outline color="success" onClick={this.onClose}>
              Close
            </Button>
          </div>
        )}
      >
        <>
          {/*COMPREHENSIVE type 和    非  COMPREHENSIVE type*/}
          {type?.name?.includes(COMPREHENSIVE) ? (
            <>
              {/* 有 surveyModel 的数据*/}
              {surveyModel ? (
                <div className="AssessmentViewer-TabsWrapper">
                  <Tabs
                    items={map(
                      reject(tabs, (t, i) => isAssessmentArchived && i === tabs.length - 1),
                      (title, i) => ({
                        title,
                        isActive: i === step,
                      }),
                    )}
                    onChange={this.onChangeTab}
                    className="AssessmentViewer-Tabs"
                    containerClassName="AssessmentViewer-TabsContainer"
                  />
                  <Dropdown
                    value={step}
                    items={map(COMPREHENSIVE_ASSESSMENT_TABS, (title, i) => ({
                      value: i,
                      text: title,
                      isActive: i === step,
                      onClick: () => this.onChangeTab(i),
                    }))}
                    toggleText={COMPREHENSIVE_ASSESSMENT_TABS[step]}
                    className="AssessmentViewer-Dropdown Dropdown_theme_blue"
                  />
                </div>
              ) : null}
              {/*  历史记录的data Source是空的 */}
              {assessmentId === id && details.isFetching && isEmpty(history.dataSource.data) ? (
                <div className="AssessmentViewer-Loading">
                  <Loader />
                </div>
              ) : (
                step < tabs.length - 1 &&
                surveyModel &&
                surveyData && (
                  <div>
                    <SurveyUI
                      key={`survey-comprehensive-${step}-${surveyModel ? surveyModel.currentPageNo : 0}`}
                      model={surveyModel}
                      showNavigationButtons="none"
                      onAfterRenderPage={this.onAfterRenderSurveyPage}
                      onAfterRenderPanel={this.onAfterRenderSurveyPanel}
                      // onAfterRenderQuestion={this.onAfterRenderSurveyQuestion}
                    />
                  </div>
                )
              )}
            </>
          ) : (
            <>
              {!isAssessmentArchived && (
                <div className="AssessmentViewer-TabsWrapper">
                  <Tabs
                    className="AssessmentViewer-Tabs"
                    items={map(tabs, (title, i) => ({
                      title,
                      isActive: i === step,
                    }))}
                    onChange={this.onChangeTab}
                  />
                </div>
              )}

              {/*    简单的 Management 的结果展示*/}
              {step === 0 &&
                (assessmentId === id && details.isFetching ? (
                  <div className="AssessmentViewer-Loading">
                    <Loader />
                  </div>
                ) : (
                  surveyModel &&
                  surveyData && (
                    <div>
                      <div className="AssessmentViewer-Section">
                        <div className="AssessmentViewer-SectionTitle font-size-26">General</div>
                        {/*  assessment的顶部样式 */}
                        <div className="margin-top-20">
                          {(() => {
                            if (
                              [
                                HMIS_ADULT_CHILD_INTAKE,
                                HMIS_ADULT_CHILD_REASESSMENT,
                                HMIS_ADULT_CHILD_REASESSMENT_EXIT,
                              ].includes(type?.name)
                            )
                              return;
                            // ARIZONA_SSM
                            if (type?.name === ARIZONA_SSM)
                              return (
                                <>
                                  <Row form>
                                    {/* <Col md={statusName === COMPLETED ? 6 : 4} sm={6} xs={12}>
                                      <TextField
                                        isDisabled
                                        type="text"
                                        label="Participant name"
                                        className="AssessmentForm-Field"
                                        value={client.details.data?.fullName}
                                      />
                                    </Col>
                                    <Col md={statusName === COMPLETED ? 6 : 4} sm={6} xs={12}>
                                      <TextField
                                        isDisabled
                                        type="text"
                                        label="Date of Birth"
                                        className="AssessmentForm-Field"
                                        value={client.details.data?.birthDate}
                                      />
                                    </Col>*/}
                                    {statusName !== COMPLETED && (
                                      <Col md={6} sm={12}>
                                        <DateField
                                          isDisabled
                                          label="Assessment date*"
                                          name="dateAssigned"
                                          className="AssessmentViewer-Field"
                                          value={dateAssigned}
                                          timeFormat="hh:mm aa"
                                          dateFormat="MM/dd/yyyy hh:mm a"
                                        />
                                      </Col>
                                    )}
                                  </Row>
                                  <Row form>
                                    {statusName === COMPLETED && (
                                      <>
                                        <Col md={4} sm={6} xs={12}>
                                          <DateField
                                            isDisabled
                                            label="Assessment date*"
                                            name="dateAssigned"
                                            className="AssessmentViewer-Field"
                                            value={dateAssigned}
                                            timeFormat="hh:mm aa"
                                            dateFormat="MM/dd/yyyy hh:mm a"
                                          />
                                        </Col>
                                        <Col md={4} sm={6} xs={12}>
                                          <DateField
                                            isDisabled
                                            label="Completed date*"
                                            name="dateCompleted"
                                            className="AssessmentViewer-Field"
                                            value={dateCompleted}
                                            timeFormat="hh:mm aa"
                                            dateFormat="MM/dd/yyyy hh:mm a"
                                          />
                                        </Col>
                                        <Col md={4} sm={12} xs={12}>
                                          <TextField
                                            isDisabled
                                            type="text"
                                            label="Total score"
                                            className="AssessmentForm-Field"
                                            value={score}
                                          />
                                        </Col>
                                      </>
                                    )}
                                  </Row>
                                </>
                              );
                            //  其他assessment的样式
                            return (
                              <Row form>
                                <Col md={6}>
                                  <DateField
                                    isDisabled
                                    label="Date Completed*"
                                    name="dateCompleted"
                                    className="AssessmentViewer-Field"
                                    value={dateCompleted}
                                    timeFormat="hh:mm aa"
                                    dateFormat="MM/dd/yyyy hh:mm a"
                                  />
                                </Col>
                                <Col md={6}>
                                  <TextField
                                    isDisabled
                                    type="text"
                                    label="Completed By*"
                                    className="AssessmentViewer-Field"
                                    value={contactName}
                                  />
                                </Col>
                              </Row>
                            );
                          })()}
                        </div>
                        {/* 模板样式*/}
                        <SurveyUI
                          key={`survey-simple-${step}-${surveyModel ? surveyModel.currentPageNo : 0}`}
                          model={surveyModel}
                          showNavigationButtons="none"
                          onAfterRenderPage={this.onAfterRenderSurveyPage}
                          onAfterRenderPanel={this.onAfterRenderSurveyPanel}
                          // onAfterRenderQuestion={this.onAfterRenderSurveyQuestion}
                        />
                        {![
                          HOUSING,
                          CARE_MGMT,
                          ARIZONA_SSM,
                          HMIS_ADULT_CHILD_INTAKE,
                          HMIS_ADULT_CHILD_REASESSMENT,
                          HMIS_ADULT_CHILD_REASESSMENT_EXIT,
                          GAD7,
                          PHQ9,
                          FAST,
                          SHORT,
                          SCREEN,
                          REVISED,
                        ].includes(type?.name) && (
                          <div className="margin-top-20">
                            <TextField
                              isDisabled
                              type="textarea"
                              label="Comment"
                              name="comment"
                              value={comment}
                              className="AssessmentViewer-Field AssessmentViewer-CommentField"
                            />
                          </div>
                        )}
                      </div>
                      {/*GAD7, PHQ9  topScoring show */}
                      {[GAD7, PHQ9].includes(type?.name) && (
                        <div className="AssessmentViewer-Section">
                          <div className="AssessmentViewer-SectionTitle">{type.shortTitle} Scoring</div>
                          {scoringAlert && (
                            <Alert
                              className="ScoringAlert"
                              type={scoringAlert.type}
                              title={isNumber(details.data.score) ? `${details.data.score} Points` : ""}
                              text={scoringAlert.text}
                            />
                          )}
                        </div>
                      )}

                      {[BENEFICIARY_COLORECTAL_CANCER_SCREENING].includes(type?.name) && (
                        <div className="AssessmentViewer-Section">
                          <div className="AssessmentViewer-SectionTitle">Screening results</div>
                          {colorectalAlert && (
                            <Alert
                              className="ScoringAlert"
                              type={colorectalAlert.type}
                              title={colorectalAlert.severity}
                              text={colorectalAlert.comments}
                            />
                          )}
                        </div>
                      )}

                      {[BENEFICIARY_MAMMOGRAM_SCREENING].includes(type?.name) && (
                        <div className="AssessmentViewer-Section">
                          <div className="AssessmentViewer-SectionTitle">Screening results</div>
                          {mammogramAlert && (
                            <Alert className="ScoringAlert" type={mammogramAlert.type} title={mammogramAlert.degree} />
                          )}
                          <div className="AssessmentEditor-mammogram-education-headerTitle">What is a mammogram?</div>
                          <div className="AssessmentEditor-mammogram-Information">
                            A mammogram is an X-ray of the breast. For many women, mammograms are the best way to find
                            breast cancer early, when it is easier to treat and before it is big enough to feel or cause
                            symptoms. Having regular mammograms can lower the risk of dying from breast cancer. At this
                            time, a mammogram is the best way to find breast cancer for most women of screening age.
                          </div>
                        </div>
                      )}

                      {/* GAD7, PHQ9  management */}
                      {[GAD7, PHQ9].includes(type?.name) && management.data && (
                        <div className="AssessmentViewer-Section">
                          <div className="AssessmentViewer-SectionTitle">Management</div>
                          <div className="AssessmentViewer-SectionText">{management.data.message}</div>
                          <Table
                            isLoading={false}
                            className="AssessmentScoringManagement"
                            containerClass="AssessmentScoringManagementContainer"
                            data={scaleList}
                            columns={[
                              {
                                dataField: "scoreLow",
                                text: "Score",
                                formatter: (v, row) => {
                                  return `${row.scoreLow}-${row.scoreHigh}`;
                                },
                              },
                              {
                                dataField: "severityShort",
                                text: `${type.name === GAD7 ? "Symptom" : "Depression"} Severity`,
                              },
                              {
                                dataField: "comments",
                                text: "Comments",
                              },
                            ]}
                            columnsMobile={["scoreLow"]}
                          />
                        </div>
                      )}
                      {[FAST].includes(type?.name) && (
                        <div className="AssessmentScoring">
                          <div className="AssessmentEditor-Section AssessmentEditor-ManagementSection">
                            <div className="AssessmentEditor-SectionTitle">Management</div>
                            <Table
                              className={`${type?.name}Scale`}
                              containerClass={`${type?.name}ScaleContainer`}
                              data={ScoringSummaryData}
                              keyField={"id"}
                              columns={[
                                {
                                  dataField: "LMV",
                                  text: "Likely Maintaining Variable",
                                  headerStyle: {
                                    width: "350px",
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
                                          const isActive = surveyData[item.QId] === "Yes";
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
                                className="AssessmentForm-Field AssessmentForm-CommentField"
                                isDisabled
                              />
                            </div>
                          </div>
                        </div>
                      )}
                      {[SHORT, REVISED, SCREEN].includes(type?.name) && (
                        <div className="AssessmentScoring">
                          <div className="AssessmentEditor-Section">
                            <div className="AssessmentEditor-SectionTitle">Suggested Scoring</div>
                            {scoringAlert && (
                              <Alert
                                className="ScoringAlert"
                                type={scoringAlert.type}
                                title={isNumber(details.data.score) ? `${details.data.score} Points` : ""}
                                text={scoringAlert.text}
                              />
                            )}
                          </div>
                          {/*// management data score*/}
                          {management.data && (
                            <div className="AssessmentEditor-Section AssessmentEditor-ManagementSection">
                              <div className="AssessmentEditor-SectionTitle">Management</div>
                              <div className="AssessmentEditor-SectionText">{management.data.message}</div>
                              <Table
                                className={`${type?.name}Scale`}
                                containerClass={`${type?.name}ScaleContainer`}
                                data={management.data.scale}
                                columns={[
                                  {
                                    dataField: "score",
                                    text: "Score",
                                    headerStyle: {
                                      width: "100px",
                                    },
                                    formatter: (v, row) => {
                                      return `${row.scoreLow}-${row.scoreHigh}`;
                                    },
                                  },
                                  {
                                    dataField: "comments",
                                    text: "Comments",
                                  },
                                ]}
                              />
                            </div>
                          )}
                        </div>
                      )}
                      {management.data && [BENEFICIARY_COLORECTAL_CANCER_SCREENING].includes(type?.name) && (
                        <div className="AssessmentViewer-Section">
                          <div className="AssessmentViewer-SectionTitle">Management</div>
                          <Table
                            className={"AssessmentScoringManagement"}
                            containerClass={"AssessmentScoringManagementContainer"}
                            data={management.data.scale}
                            columns={[
                              {
                                dataField: "severity",
                                text: "Degree",
                                headerStyle: {
                                  width: "260px",
                                },
                              },
                              {
                                dataField: "severityShort",
                                text: `Age`,
                                headerStyle: {
                                  width: "130px",
                                },
                              },
                              {
                                dataField: "highlighting",
                                text: `Level`,
                                headerStyle: {
                                  width: "100px",
                                },
                                formatter: (v, row) => {
                                  if (v === "High") {
                                    return 1;
                                  } else if (v === "Medium") {
                                    return 2;
                                  } else return 3;
                                },
                              },
                              {
                                dataField: "comments",
                                text: "Diagnosis",
                              },
                            ]}
                          />
                        </div>
                      )}
                    </div>
                  )
                ))}
            </>
          )}
          {/* Change History */}
          {step === tabs.length - 1 && (
            <Table
              hasPagination
              keyField="id"
              title="Change History"
              isLoading={history.isFetching}
              className="AssessmentChangeHistory"
              containerClass="AssessmentChangeHistoryContainer"
              data={history.dataSource.data}
              pagination={history.dataSource.pagination}
              columns={[
                {
                  dataField: "modifiedDate",
                  text: "Date",
                  align: "right",
                  headerAlign: "right",
                  formatter: (v) => format(v, DATE_FORMAT),
                },
                {
                  dataField: "statusName",
                  text: "Status",
                  formatter: (v, row) => {
                    return (
                      <span
                        style={{ backgroundColor: STATUS_COLORS[row.statusName] }}
                        className="AssessmentChangeHistory-AssessmentStatus"
                      >
                        {row.statusTitle}
                      </span>
                    );
                  },
                },
                {
                  dataField: "author",
                  text: "Author",
                  formatter: (v, row) => `${row.author}, ${row.authorRole}`,
                },
                {
                  dataField: "@actions",
                  text: "Updates",
                  formatter: (v, row) => {
                    return row.isArchived ? (
                      <Button
                        color="link"
                        onClick={() => {
                          this.onView(row);
                        }}
                        className="AssessmentChangeHistory-ViewDetailsBtn"
                      >
                        View Details
                      </Button>
                    ) : null;
                  },
                },
              ]}
              columnsMobile={["modifiedDate"]}
              renderCaption={(title) => {
                return (
                  <div className="AssessmentChangeHistory-Caption">
                    <div className="AssessmentChangeHistory-Title">{title}</div>
                  </div>
                );
              }}
              onRefresh={this.onRefreshChangeHistory}
            />
          )}
        </>
      </Modal>
    );
  }
}

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(AssessmentViewer));
