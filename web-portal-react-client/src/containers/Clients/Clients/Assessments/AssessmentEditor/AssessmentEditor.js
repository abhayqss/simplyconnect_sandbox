import React, { Component } from "react";

import $ from "jquery";
import cn from "classnames";
import PropTypes from "prop-types";

import {
  all,
  any,
  compact,
  each,
  filter,
  find,
  findIndex,
  findWhere,
  first,
  flatten,
  isArray,
  isNumber,
  isObject,
  isString,
  keys,
  map,
  noop,
  partition,
  pick,
  reject,
  uniq,
  where,
} from "underscore";

import { renderToStaticMarkup } from "react-dom/server";

import { connect } from "react-redux";
import { bindActionCreators } from "redux";

import { withRouter } from "react-router-dom";

import * as Survey from "survey-react";
import { Model } from "survey-core";
import { Survey as SurveyUI } from "survey-react-ui";
import * as widgets from "surveyjs-widgets";
import "survey-core/defaultV2.min.css";
import { PlainLight } from "survey-core/themes";

import "pc-bootstrap4-datetimepicker";

import { Button, Col, Row, UncontrolledTooltip as Tooltip } from "reactstrap";

import "pc-bootstrap4-datetimepicker/build/css/bootstrap-datetimepicker.min.css";

import { withAutoSave, withDirectoryData } from "hocs";

import { Action, Alert, Dropdown, ErrorViewer, Loader, Modal, Table, Tabs } from "components";

import { CheckboxField, DateField, TextField } from "components/Form";

import { SuccessDialog, WarningDialog } from "components/dialogs";

import {
  LoadServicePlanDomainsAction,
  LoadServicePlanProgramSubTypesAction,
  LoadServicePlanProgramTypesAction,
} from "actions/directory";

import { LoadAssessmentDefaultDataAction } from "actions/clients";

import * as assessmentFormActions from "redux/client/assessment/form/assessmentFormActions";
import * as assessmentDetailsActions from "redux/client/assessment/details/assessmentDetailsActions";
import servicePlanNeedIdentificationActions from "redux/client/assessment/servicePlan/needIdentification/assessmentServicePlanNeedIdentificationActions";

import * as servicePlanDetailsActions from "redux/client/servicePlan/details/servicePlanDetailsActions";

import * as assessmentScoreActions from "redux/directory/assessment/score/assessmentScoreActions";
import * as assessmentSurveyActions from "redux/directory/assessment/survey/assessmentSurveyActions";
import * as assessmentManagementActions from "redux/directory/assessment/management/assessmentManagementActions";

import { DateUtils as DU, defer, isEmpty, isInteger, isNotEmpty, snakeToTitle, Time, uc } from "lib/utils/Utils";

import { path } from "lib/utils/ContextUtils";

import store from "lib/stores/BaseStore";

import { Response } from "lib/utils/AjaxUtils";

import {
  ASSESSMENT_STATUSES,
  ASSESSMENT_TYPES,
  COLONOSCOPY_DECISION_AND_EDUCATION,
  DisadvantagesRisksList,
} from "lib/Constants";

import { ReactComponent as Diskette } from "images/diskette.svg";
import { ReactComponent as Risks } from "images/screening/close.svg";
import { ReactComponent as Benefits } from "images/screening/correct.svg";

import * as SCORING from "./Scoring";

import { SURVEY_QUESTIONS_TO_SERVICE_PLAN_NEEDS, SURVEY_SECTIONS_TO_SERVICE_PLAN_NEEDS } from "./Mapping";

import prepareAssessmentData from "./utils/AssessmentDataUtils";

import AssessmentTypeForm from "../AssessmentTypeForm/AssessmentTypeForm";
import AssessmentToServicePlanEditor from "../AssessmentToServicePlanEditor/AssessmentToServicePlanEditor";

import "./AssessmentEditor.scss";

/**
 * 日期格式常量：中美日期格式
 * @type {string}
 */
const DATE_FORMAT = DU.formats.americanMediumDate;
/**
 * 日期时间带时区格式常量：长日期、中等时间、12小时制时区
 * @type {string}
 */
const DATE_TIME_ZONE_FORMAT = DU.formats.longDateMediumTime12TimeZone;

/**
 * 问题列表对象，包含多个调查问卷的问题数组
 * @type {Object}
 */
export const questionList = {
  SRA1: [
    {
      QId: "P1-1",
      text: 1,
    },
    {
      QId: "P1-2",
      text: 2,
    },
    {
      QId: "P1-3",
      text: 3,
    },
    {
      QId: "P2-4",
      text: 4,
    },
    {
      QId: "P2-5",
      text: 5,
    },
    {
      QId: "P2-6",
      text: 6,
    },
    {
      QId: "P2-7",
      text: 7,
    },
    {
      QId: "P2-8",
      text: 8,
    },
  ],
  SRA2: [
    {
      QId: "P1-1",
      text: 1,
    },
    {
      QId: "P1-2",
      text: 2,
    },
    {
      QId: "P1-3",
      text: 3,
    },
    {
      QId: "P2-9",
      text: 9,
    },
    {
      QId: "P2-10",
      text: 10,
    },
    {
      QId: "P2-11",
      text: 11,
    },
    {
      QId: "P2-12",
      text: 12,
    },
    {
      QId: "P2-13",
      text: 13,
    },
  ],
  SRA3: [
    {
      QId: "P1-1",
      text: 1,
    },
    {
      QId: "P1-2",
      text: 2,
    },
    {
      QId: "P1-3",
      text: 3,
    },
    {
      QId: "P2-14",
      text: 14,
    },
    {
      QId: "P2-15",
      text: 15,
    },
    {
      QId: "P2-16",
      text: 16,
    },
    {
      QId: "P2-17",
      text: 17,
    },
    {
      QId: "P2-18",
      text: 18,
    },
  ],
  SRA4: [
    {
      QId: "P3-19",
      text: 19,
    },
    {
      QId: "P3-20",
      text: 20,
    },
    {
      QId: "P3-21",
      text: 21,
    },
    {
      QId: "P3-22",
      text: 22,
    },
    {
      QId: "P3-23",
      text: 23,
    },
    {
      QId: "P3-24",
      text: 24,
    },
  ],
  SRA5: [
    {
      QId: "P3-19",
      text: 19,
    },
    {
      QId: "P3-20",
      text: 20,
    },
    {
      QId: "P3-24",
      text: 24,
    },
    {
      QId: "P3-25",
      text: 25,
    },
    {
      QId: "P3-26",
      text: 26,
    },
    {
      QId: "P3-27",
      text: 27,
    },
  ],
};

/**
 * 得分汇总数据数组，包含多个调查问卷的得分信息
 * @type {Object[]}
 */
export const ScoringSummaryData = [
  {
    id: 1,
    LMV: "Social Reinforcement (attention)",
    question: "SRA1",
  },
  {
    id: 2,
    LMV: "Social Reinforcement (access to specific activities/items)",
    question: "SRA2",
  },
  {
    id: 3,
    LMV: "Social Reinforcement (escape)",
    question: "SRA3",
  },
  {
    id: 4,
    LMV: "Automatic Reinforcement (sensory stimulation)",
    question: "SRA4",
  },
  {
    id: 5,
    LMV: "Automatic Reinforcement (pain attenuation)",
    question: "SRA5",
  },
];

/**
 * 综合标签数组，包含多个标签名称
 * @type {string[]}
 */
const COMPREHENSIVE_TABS = [
  "Guide",
  "Demographics",
  "Medical History",
  "WellRx",
  "Additional Questions",
  "Behavioral Health",
  "Engagement",
];

const time = new Time();

let surveyPages = [];
let majorSurveyPanels = [];
let minorSurveyPanels = [];
let surveyRenderCount = 0;

/**
 * 空调查问卷文本常量
 * @type {string}
 */
const EMPTY_SURVEY_TEXT = "No questions for review";
/**
 * 调查问卷面板名称常量对象
 * @type {Object}
 */
const SURVEY_PANEL_NAME = {
  PHQ_2: "PHQ_2",
  PHQ_9: "PHQ_9",
  GAD_7: "GAD_7",
  CAGE_AID: "CAGE_AID",
  PATIENT_EDUCATION: "PATIENT_EDUCATION",
  CAREGIVER: "CAREGIVER",
};

/**
 * 调查问卷面板标题常量对象
 * @type {Object}
 */
const SURVEY_PANEL_TITLE = {
  [SURVEY_PANEL_NAME.CAREGIVER]: "Do you currently have any concerns about your caregiving (in the past week)?",
  [SURVEY_PANEL_NAME.PHQ_2]: "Patient Health Questionnaire-2 (PHQ-2)",
  [SURVEY_PANEL_NAME.PHQ_9]: "Patient Health Questionnaire-9 (PHQ-9)",
  [SURVEY_PANEL_NAME.GAD_7]: "Generalized Anxiety Disorder (GAD-7)",
  [SURVEY_PANEL_NAME.CAGE_AID]: "CAGE Substance Abuse Screening tool",
  [SURVEY_PANEL_NAME.PATIENT_EDUCATION]: "Patient Education",
};

/**
 * 各种评估类型常量
 */
const {
  GAD7,
  PHQ9,
  IN_HOME,
  HOUSING,
  CARE_MGMT,
  ARIZONA_SSM,
  IN_HOME_CARE,
  COMPREHENSIVE,
  NOR_CAL_COMPREHENSIVE,
  HMIS_ADULT_CHILD_INTAKE,
  HMIS_ADULT_CHILD_REASESSMENT,
  HMIS_ADULT_CHILD_REASESSMENT_EXIT,
  /**
   * add screening
   */
  BENEFICIARY_COLORECTAL_CANCER_SCREENING,
  BENEFICIARY_MAMMOGRAM_SCREENING,
  BENEFICIARY_SCREENING_FOR_TYPE_II_DIABETES,
  BENEFICIARY_SCREENING_DIABETIC_FOOT_AND_EYE_EXAM,
  FAST,
  SHORT,
  REVISED,
  SCREEN,
} = ASSESSMENT_TYPES;

/**
 * 启用草稿的评估类型数组常量
 * @type {string[]}
 */
const ASSESSMENT_TYPES_WITH_ENABLED_DRAFT = [
  HOUSING,
  ARIZONA_SSM,
  COMPREHENSIVE,
  NOR_CAL_COMPREHENSIVE,
  HMIS_ADULT_CHILD_INTAKE,
  HMIS_ADULT_CHILD_REASESSMENT,
  HMIS_ADULT_CHILD_REASESSMENT_EXIT,
];

/**
 * 自动保存的评估类型数组常量
 * @type {string[]}
 */
const ASSESSMENT_TYPES_FOR_AUTO_SAVE = [
  COMPREHENSIVE,
  NOR_CAL_COMPREHENSIVE,
  GAD7,
  PHQ9,
  ARIZONA_SSM,
  HMIS_ADULT_CHILD_INTAKE,
  HMIS_ADULT_CHILD_REASESSMENT,
  HMIS_ADULT_CHILD_REASESSMENT_EXIT,
  /**
   * add new screening
   */
  BENEFICIARY_COLORECTAL_CANCER_SCREENING,
  BENEFICIARY_MAMMOGRAM_SCREENING,
  BENEFICIARY_SCREENING_FOR_TYPE_II_DIABETES,
  BENEFICIARY_SCREENING_DIABETIC_FOOT_AND_EYE_EXAM,
  FAST,
  SHORT,
  REVISED,
  SCREEN,
];

/**
 * 评估状态常量
 */
const { INACTIVE, COMPLETED, IN_PROCESS } = ASSESSMENT_STATUSES;

/**
 * 等级警报类型常量对象
 * @type {Object}
 */
const LEVELS_ALERT_TYPES = {
  LOW: "success",
  MEDIUM: "warning",
  HIGH: "danger",
};
// Evaluating edit status
/**
 * 存储状态键常量
 * @type {string}
 */
const STORED_STATES_KEY = "ASSESSMENT_EDITOR_STATES";
// marital status table

/**
 * 婚姻状态映射对象
 * @type {Object}
 */
const MARITAL_STATUS_MAP = {
  Annulled: null,
  Divorced: "Divorced",
  "Domestic Partner": "Living with partner",
  Interlocutory: null,
  "Legally Separated": "Separated",
  Married: "Married",
  "Never Married": "Never married",
  Polygamous: null,
  Widowed: "Widow",
};

/**
 * 存储状态函数
 * @param {string} key - 状态键
 * @param {any} state - 状态值
 */
function storeState(key, state) {
  store.save(STORED_STATES_KEY, { ...store.get(STORED_STATES_KEY), [key]: state });
}

/**
 * 恢复状态函数
 * @param {string} key - 状态键
 * @returns {any} - 恢复的状态值
 */
function restoreState(key) {
  return (store.get(STORED_STATES_KEY) || {})[key] || {};
}

/**
 * 将 Redux state 映射到组件的 props 上
 * @param {Object} state - Redux state 对象
 * @returns {Object} - 组件的 props 对象
 */
function mapStateToProps(state) {
  const { auth, client, directory } = state;

  const { form, details } = client.assessment;

  return {
    form,
    details,
    client,

    auth,
    directory,
    servicePlan: client.servicePlan,
  };
}

// Maps Redux actions to props.
/**
 * 将 Redux actions 映射到组件的 props 上
 * @param {function} dispatch - Redux dispatch 函数
 * @returns {Object} - 包含映射的 actions 的对象
 */
function mapDispatchToProps(dispatch) {
  return {
    actions: {
      form: bindActionCreators(assessmentFormActions, dispatch), // 使用bindActionCreators把assessmentFormActions里定义的所有action creator函数绑定到dispatch方法上,赋值给form属性。
      details: bindActionCreators(assessmentDetailsActions, dispatch), //

      servicePlan: {
        details: bindActionCreators(servicePlanDetailsActions, dispatch),
        needIdentification: bindActionCreators(servicePlanNeedIdentificationActions, dispatch),
      },

      score: bindActionCreators(assessmentScoreActions, dispatch),
      survey: bindActionCreators(assessmentSurveyActions, dispatch),
      management: bindActionCreators(assessmentManagementActions, dispatch),
    },
    // * this.props.actions.form.load()
  };
}

class AssessmentEditor extends Component {
  /**
   * AssessmentEditor 组件的属性类型。
   * @type {Object}
   */
  static propTypes = {
    isOpen: PropTypes.bool, // 指示编辑器是否打开。
    isCopying: PropTypes.bool, // 指示是否正在进行复制。
    assessmentId: PropTypes.number, // 评估的ID。
    autoSaveAdapter: PropTypes.shape({
      init: PropTypes.func, // 初始化自动保存的函数。
    }),
    assessmentTypeId: PropTypes.number, // 评估类型的ID。
    parentAssessmentId: PropTypes.number, // 父评估的ID。
    onClose: PropTypes.func, // 当编辑器关闭时调用的函数。
    onSaveSuccess: PropTypes.func, // 当保存成功时调用的函数。
    onCompleteSuccess: PropTypes.func, // 当完成成功时调用的函数。
    onChangeActivitySuccess: PropTypes.func, // 当活动更改成功时调用的函数。
  };
  static defaultProps = {
    isOpen: false,
    isCopying: false,

    onClose: noop,
    onSaveSuccess: noop,
    onCompleteSuccess: noop,
    onChangeActivitySuccess: noop,
  };

  formRef = React.createRef();
  modalRef = React.createRef();
  surveyRef = React.createRef();

  state = {
    clientFirstName: this.client?.details?.data?.firstName,
    clientMiddleName: "",
    clientLastName: this.client?.details?.data?.lastName,
    clientBirthDate: this.client?.details?.data?.birthDate,
    fastComments: "",
    step: this.props.isCopying || this.isEditMode() ? 1 : 0,

    typeId: null,

    surveyKey: 0,

    isChanged: false,
    isInactive: false,

    shouldCancelValidation: false,

    scrollOffset: 0,

    isReviewMode: false,
    isReviewModeTransition: false,

    isToServicePlanEditorOpen: false,
    isNoIdentifiedNeedsDialogOpen: false,
    isServicePlanSaveSuccessDialogOpen: false,
    isQuestionsExcludeSuccessDialogOpen: false,

    isNewServicePlanCreated: false,
    servicePlanNeedIdentification: {
      data: [],
      excludedSections: [],
      excludedQuestions: [],
    },

    surveyData: null,
    surveyModel: null,
    isSurveyReady: false,
    surveyReviewModel: null,

    invalidSurveyPageIndexes: [],
  };

  areAllPanelsExpanded = false;
  areAllPanelsCollapsed = false;

  constructor(props) {
    super(props);
    // Initialization of component automation saves
    if (this.props.autoSaveAdapter && this.isEditMode()) {
      this.props.autoSaveAdapter.init({
        onSave: () => this.onAutoSave(),
      });
    }
  }

  get actions() {
    return this.props.actions;
  }

  get clientId() {
    return +this.props.match.params.clientId;
  }

  get assessmentId() {
    return this.props.assessmentId;
  }

  get authUser() {
    return this.props.auth.login.user.data;
  }

  get error() {
    const { form, servicePlan } = this.props.client.assessment;

    return form.error || servicePlan.needIdentification.error;
  }

  /**
   * 在组件挂载后调用的生命周期函数。
   * 此函数用于执行一些初始化操作和加载数据。
   * @returns {void}
   */
  componentDidMount() {
    time.save(); // 保存时间信息

    this.loadServicePlanDetails("in-development"); // 加载服务计划详情

    // 设置日期时间选择器的图标样式
    $.extend(true, $.fn.datetimepicker.defaults, {
      icons: {
        up: "fa fa-angle-up",
        down: "fa fa-angle-down",
        previous: "fa fa-angle-left",
        next: "fa fa-angle-right",
      },
    });

    // 设置调查问卷的空文本
    Survey.surveyStrings.emptySurvey = EMPTY_SURVEY_TEXT;

    // 修改调查问卷的元数据，设置面板属性
    Survey.JsonObject.metaData.removeProperty("panel", "readOnly");
    Survey.JsonObject.metaData.addProperty("panel", "isNavigable:boolean");
    Survey.JsonObject.metaData.addProperty("panel", "isExpandable:boolean");
    Survey.JsonObject.metaData.addProperty("panel", "isNavDestination:boolean");
    Survey.JsonObject.metaData.addProperty("panel", "panelAnchor:text");

    // 修改调查问卷的元数据，设置问题属性
    Survey.JsonObject.metaData.addProperty("question", "calendarFlag:text");
    Survey.JsonObject.metaData.addProperty("question", "disablePast:boolean");
    Survey.JsonObject.metaData.addProperty("question", "disableFuture:boolean");
    Survey.JsonObject.metaData.addProperty("question", "customdatepicker:boolean");
    Survey.JsonObject.metaData.addProperty("question", "isPriority:boolean");
    Survey.JsonObject.metaData.addProperty("question", "relatedComment:text");

    // 初始化调查问卷的输入掩码和美化复选框
    widgets.inputmask(Survey, $);
    widgets.prettycheckbox(Survey, $);

    if (this.isEditMode()) {
      // 如果处于编辑模式
      const { isCopying } = this.props;
      const { step } = restoreState(this.assessmentId);

      if (!isCopying && step > 1) {
        this.setState({ step });
      }

      // 加载评估详情
      this.loadDetails().then(
        Response(({ data }) => {
          // 改变表单字段
          this.changeFormFields({
            ...pick(data, "typeId", "comment", "contactId", "hasErrors", "dateAssigned", "dateCompleted"),
            ...(isCopying && {
              dateCompleted: null,
              dateAssigned: Date.now(),
              contactId: this.authUser.id,
            }),
          });

          const { comment, statusTitle } = data;
          let type = this.getAssessmentTypeById(data.typeId);

          // 设置调查问卷数据
          this.setState({
            surveyData: {
              ...JSON.parse(data.dataJson),
              Demographics_General_Status: statusTitle,
              Demographics_General_Comment: comment,
              ...(isCopying && {
                "Completed by": this.authUser.fullName,
                "Date started": DU.format(Date.now(), DATE_TIME_ZONE_FORMAT),
                ...(type?.name === HOUSING && {
                  Case_manager: this.authUser.fullName,
                }),
              }),
            },
            isInactive: data.statusName === INACTIVE,
          });

          // 设置服务计划需要识别
          this.setServicePlanNeedIdentification({
            excludedSections: data.servicePlanNeedIdentificationExcludedSections,
            excludedQuestions: data.servicePlanNeedIdentificationExcludedQuestions,
          });

          // 如果是第一步(Guide标签页)，确保正确定位
          if (this.state.step === 1) {
            setTimeout(() => {
              this.ensureGuideTabVisible();
            }, 100);
          }
        }),
      );
    } else if (this.props.assessmentTypeId) {
      // 如果存在评估类型ID
      this.changeFormField("typeId", this.props.assessmentTypeId).then(() => {
        this.changeStep(1);
        // 确保在首次打开时定位到Guide标签页
        setTimeout(() => {
          this.ensureGuideTabVisible();
        }, 100);
      });
    }
  }

  componentWillUnmount() {
    surveyPages = [];
    majorSurveyPanels = [];
    minorSurveyPanels = [];
    surveyRenderCount = 0;

    this.actions.form.clear();
    this.actions.survey.clear();
    this.actions.details.clear();
    this.actions.management.clear();
    this.actions.servicePlan.details.clear();
  }

  componentDidUpdate(prevProps, prevState) {
    const { surveyData } = this.state;

    // 如果 surveyData 发生变化，更新模型数据
    if (surveyData !== prevState.surveyData) {
      this.updateSurveyModelData();
    }
  }

  onResetError = () => {
    this.actions.form.clearError();
    this.actions.servicePlan.needIdentification.clearError();
  };

  onReview = () => {
    this.setReviewMode();
  };

  onCancelReview = () => {
    this.cancelReviewMode();
  };

  onClose = () => {
    storeState(this.assessmentId, {
      step: this.state.step,
    });

    this.props.onClose(this.state.isChanged);
  };

  /**
   * 处理点击下一步按钮的事件。
   * @param {Event} e - 事件对象
   * @returns {void}
   */
  onNext = (e) => {
    e?.preventDefault() || noop(); // 防止默认事件或者调用空操作

    const { step } = this.state; // 获取当前步骤

    const type = this.getAssessmentType(); // 获取评估类型

    if (type?.name?.includes(COMPREHENSIVE)) {
      // 如果评估类型包含综合评估
      this.changeStep(step + 1); // 切换至下一步
    } else {
      // 否则
      if (step > 0) {
        // 如果当前步骤大于0
        // 验证调查问卷
        this.validateSurvey().then((success) => {
          if (success) {
            // 如果验证成功
            this.loadScore(); // 加载评分
            this.changeStep(step + 1); // 切换至下一步
          }
        });
      } else {
        this.changeStep(step + 1); // 切换至下一步
      }
    }
  };

  onBack = (e) => {
    e.preventDefault();

    if (this.state.isReviewMode) {
      this.cancelReviewMode();
    } else this.changeStep(this.state.step - 1);
  };

  /**
   * 处理保存操作的函数。
   * @returns {Promise<number>} 返回一个 Promise，包含保存的评估 ID。
   */
  onSave = () => {
    // 获取未添加到服务计划识别需求的数量
    const notAddedToServicePlanNeedCount = this.getNotAddedToServicePlanIdentifiedNeedCount();

    // 执行保存操作，并返回一个 Promise
    return this.save().then(({ data: id }) => {
      // 在保存成功后调用 onSaveSuccess 回调函数，并传递相关参数
      this.props.onSaveSuccess(
        {
          id,
          notAddedToServicePlanNeedCount,
          typeName: this.getAssessmentType().name,
        },
        true,
      );
      return id; // 返回保存的评估 ID
    });
  };

  /**
   * 验证调查问卷并保存评估数据。
   * 如果验证成功，则执行保存操作。
   * @returns {void}
   */
  onValidateAndSave = () => {
    this.validateSurvey().then((success) => {
      if (success) this.onSave(); // 如果验证成功，则执行保存操作
    });
  };

  /**
   * 根据需要保存并验证评估数据。
   * @param {boolean} shouldValidate - 是否应该进行验证，默认为 true。
   * @param {boolean} isAutoSave - 是否为自动保存，默认为 false。
   * @returns {void}
   */
  onSaveAndValidateIfNeed = (shouldValidate = true, isAutoSave = false) => {
    if (!shouldValidate) {
      this.setState({ shouldCancelValidation: true });
    }

    const type = this.getAssessmentType();

    // 执行保存操作，并在保存成功后加载详情并更新状态
    this.save({ isAutoSave }).then(
      Response(({ data }) => {
        this.loadDetails(data);
        this.setState({ isChanged: false });
        this.props.onSaveSuccess({ id: data, typeName: type?.name }, false);
      }),
    );
  };

  /**
   * 自动保存评估数据。
   * 根据评估类型决定是否执行保存操作。
   * @returns {void}
   */
  onAutoSave() {
    const type = this.getAssessmentType();

    if (ASSESSMENT_TYPES_FOR_AUTO_SAVE.includes(type?.name)) this.onSaveAndValidateIfNeed(false, true);
  }

  /**
   * 保存评估数据并验证。
   * @returns {void}
   */
  onSaveAndValidate = () => {
    this.onSaveAndValidateIfNeed();
  };

  /**
   * 保存评估数据并关闭编辑器。
   * @returns {void}
   */
  onSaveAndClose = () => {
    const type = this.getAssessmentType();

    // 执行保存操作，并在保存成功后更新状态并调用 onSaveSuccess 回调函数
    this.save().then(
      Response(({ data }) => {
        storeState(data, {
          step: this.state.step,
        });
        this.props.onSaveSuccess({ id: data, typeName: type?.name }, true);
      }),
    );
  };

  changeClientFirstName = (name, value) => {
    this.setState({ clientFirstName: value });
  };
  changeClientMiddleName = (name, value) => {
    this.setState({ clientMiddleName: value });
  };
  changeClientLastName = (name, value) => {
    this.setState({ clientLastName: value });
  };
  changeClientBirthDate = (name, value) => {
    this.setState({ clientBirthDate: value });
  };

  /**
   * 完成评估并保存评估数据。
   * 如果验证成功，则执行保存操作并调用完成成功的回调函数。
   * @returns {void}
   */
  onComplete = () => {
    // 获取未添加到服务计划识别需求的数量
    const notAddedToServicePlanNeedCount = this.getNotAddedToServicePlanIdentifiedNeedCount();

    // 验证调查问卷
    this.validateSurvey().then((success) => {
      if (success) {
        // 如果验证成功
        // 执行加载评分和保存评估数据的 Promise
        Promise.all([this.loadScore(), this.save({ shouldComplete: true })]).then(([score, { data: id }]) => {
          // 在完成成功后调用 onCompleteSuccess 回调函数，并传递相关参数
          this.props.onCompleteSuccess(
            {
              id,
              score,
              notAddedToServicePlanNeedCount,
              typeName: this.getAssessmentType().name,
            },
            true,
          );
          return id;
        });
      } else {
        this.cancelReviewMode(); // 取消审阅模式
      }
    });
  };

  /**
   * 更改评估活动状态（激活或停用）。
   * @param {boolean} isInactive - 是否停用评估活动。
   * @returns {void}
   */
  onChangeActivity = (isInactive) => {
    const type = this.getAssessmentType();

    // 执行保存操作，并在保存成功后调用 onChangeActivitySuccess 回调函数
    this.save({ shouldMarkAsInactive: isInactive }).then((resp) => {
      resp.success && this.props.onChangeActivitySuccess({ id: resp.data, typeName: type?.name }, isInactive);
    });
  };

  /**
   * 更改当前选项卡。
   * @param {number} tab - 要切换到的选项卡索引。
   * @returns {void}
   */
  onChangeTab = (tab) => {
    this.changeStep(tab + 1); // 切换至指定步骤

    // 如果切换到Guide标签页(tab 0)，确保正确定位
    if (tab === 0) {
      setTimeout(() => {
        this.ensureGuideTabVisible();
      }, 50);
    }
  };

  /**
   * 在调查渲染后执行的操作。
   * @param {Survey} survey - Survey 实例。
   * @returns {void}
   */
  onAfterRenderSurvey = (survey) => {
    each(survey.pages, (page) => {
      if (!any(surveyPages, (p) => p.name === page.name)) {
        surveyPages.push({ name: page.name });
      }
    });

    const { surveyData, surveyModel, isSurveyReady } = this.state;

    const isEditMode = this.isEditMode();

    if (!isSurveyReady) {
      // to fix survey date field value validation issue
      each(surveyData, (value, name) => {
        surveyModel.setValue(name, value);

        if (isEditMode) {
          surveyModel.getQuestionByName(name)?.clearErrors() || noop();
        }
      });

      if (isEditMode && this.getFormData().hasErrors) {
        this.validateSurvey();
      }
    }

    /**
     * TODO temp bugfix of survey bag.
     * Need to upgrade a library and investigate an issue again.
     * */

    const { step } = restoreState(this.assessmentId);

    if (
      isEditMode &&
      (!step || step === 1) &&
      surveyRenderCount === 0 &&
      surveyModel.currentPageNo === 0 &&
      survey.pages.length > 1
    ) {
      this.setSurveyCurrentPageNo(1);
      defer().then(() => this.setSurveyCurrentPageNo(0));
    }

    surveyRenderCount++;
    this.setState({ isSurveyReady: true });
  };

  /**
   * 在调查页面渲染后执行的操作。
   * @param {Survey} survey - Survey 实例。
   * @param {object} options - 选项对象。
   * @param {Survey.Page} options.page - 渲染的页面对象。
   * @param {HTMLElement} options.htmlElement - 页面的 HTML 元素。
   * @returns {void}
   */
  onAfterRenderSurveyPage = (survey, options) => {
    const { page, htmlElement } = options;

    if (page.name !== "all") {
      const pg = find(surveyPages, (p) => p.name === page.name);

      if (pg) {
        if (!pg.element) {
          pg.isVisible = page.isVisible;
          pg.element = htmlElement;
        }
      } else {
        surveyPages.push({
          name: page.name,
          isVisible: page.isVisible,
          element: htmlElement,
        });
      }
    }

    const $page = $(htmlElement);
    $page.addClass("SurveyPage");

    if (this.state.isReviewMode) {
      const $nav = $(renderToStaticMarkup(<div className="SurveyPage-Nav" />));

      const visiblePages = where(surveyPages, { isVisible: true });

      if (visiblePages.length > 1) {
        each(visiblePages, ({ name, title }) => {
          $nav.append(
            renderToStaticMarkup(
              <a href={`#${name}`} data-nav-target={name} className="SurveyPage-NavLink">
                {title}
              </a>,
            ),
          );
        });

        $page.prepend($nav);
      }
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

  //since all date fields are required the user will not be able to clear the field
  /**
   * 在调查面板渲染后执行的操作。
   * @param {Survey} survey - Survey 实例。
   * @param {object} options - 选项对象。
   * @param {Survey.Panel} options.panel - 渲染的面板对象。
   * @param {HTMLElement} options.htmlElement - 面板的 HTML 元素。
   * @returns {void}
   */
  onAfterRenderSurveyPanel = (survey, options) => {
    const { panel, htmlElement } = options;

    const { name, title, isNavigable, isExpandable, isNavDestination } = panel;

    const $panel = $(htmlElement);
    $panel.addClass("SurveyPanel");

    if (isExpandable || isNavigable) {
      let mjPanel = findWhere(majorSurveyPanels, { name });

      if (!mjPanel) {
        mjPanel = {
          name,
          title,
          isNavigable,
          isExpandable,
          isNavDestination,
          element: htmlElement,
        };

        majorSurveyPanels.push(mjPanel);
      }

      each(minorSurveyPanels, (mnp) => {
        if (!mnp.navigableParent) {
          mnp.navigableParent = mjPanel;
        }
      });
    }

    if (isExpandable) {
      $panel.addClass("ExpandableSurveyPanel");
    }

    if (isNavDestination) {
      $panel.addClass("NavDestinationSurveyPanel");
    }

    const { isReviewMode } = this.state;

    if (isReviewMode) {
      let page = findWhere(surveyPages, { name });

      if (page) {
        page.isVisible = true;

        $panel.attr("id", name).addClass("SurveyPagePanel");

        let $title = $panel.find(".SurveyPagePanel-Title:first");

        if (!$title.length) {
          $title = $(
            renderToStaticMarkup(<div className="sv_p_title SurveyPanel-Title SurveyPagePanel-Title">{name}</div>),
          );

          $panel.prepend($title);
        }

        each(majorSurveyPanels, (mnp) => {
          if (!mnp.navigableParent) {
            mnp.navigableParent = page;
          }
        });

        const hasNav = $panel.find(".SurveyPanel-Nav").length > 0;

        const navDestinations = filter(majorSurveyPanels, (p) => p.navigableParent.name === name);

        if (!hasNav && navDestinations.length > 1) {
          $panel.addClass("NavigableSurveyPanel");

          const $nav = $(renderToStaticMarkup(<div className="SurveyPanel-Nav" />));

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
        }
      }

      if (isExpandable || isNavigable) {
        $panel.attr("id", name);

        const borderClass = "SurveyMajorPanel-DecoratedBorder";

        if (!$panel.has(`.${borderClass}`).length) {
          $panel.prepend(renderToStaticMarkup(<div className={borderClass} />));
        }
      }

      if (isExpandable) {
        panel.expand();

        $panel.find(".SurveyPanel-Title:first").on("click", (e) => {
          e.stopPropagation();
        });
      }
    } else {
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

          const $title = $panel.find(".SurveyPanel-Title:first");

          $title.after($nav);

          if (isExpandable) {
            const isExpanded = () => {
              return $title.hasClass("sv_p_title_expanded");
            };

            if (isExpanded()) $nav.show();

            $title.on("click", () => {
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

        $panel.attr("id", name);

        const borderClass = "SurveyMinorPanel-DecoratedBorder";

        if (!$panel.has(`.${borderClass}`).length) {
          $panel.prepend(renderToStaticMarkup(<div className={borderClass} />));
        }
      }
    }
  };

  /**
   * 在调查问题渲染后执行的操作。
   * @param {Survey} survey - Survey 实例。
   * @param {object} options - 选项对象。
   * @param {Survey.Question} options.question - 渲染的问题对象。
   * @param {HTMLElement} options.htmlElement - 问题的 HTML 元素。
   * @returns {void}
   */
  onAfterRenderSurveyQuestion = (survey, options) => {
    const { question, htmlElement } = options;

    const name = question.name;
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

    // disable the isPriority feature
    /*if (type === 'boolean') {
            question.visible = false
        }*/

    if (!question.readOnly && question.calendarFlag) {
      const isDateOnly = question.calendarFlag === "withoutTime";

      $elem
        .find("input")
        .datetimepicker({
          useCurrent: false,
          sideBySide: !isDateOnly,
          format: `MM/DD/YYYY${isDateOnly ? "" : " hh:mm A"}`,
          ...(question.disablePast && { minDate: Date.now() }),
          ...(question.disableFuture && { maxDate: DU.endOf(Date.now(), "day") }),
        })
        .on("dp.show", () => {
          $elem.find("input").val(question.value);
        })
        .on("dp.change", (e) => {
          const date = e.date
            ? DU.format(e.date.toDate().getTime(), isDateOnly ? DATE_FORMAT : DATE_TIME_ZONE_FORMAT)
            : null;

          this.changeSurveyValue(name, date);

          question.value = date;
          this.state.surveyModel.setValue(name, date);

          $elem.find("input").val(date);

          question.clearErrors();
        })
        .on("dp.hide", () => {
          $elem.find("input").val(question.value);

          if (question.value) {
            question.clearErrors();
          }
        });
    }
  };

  /**
   * 当调查数值发生变化时触发的函数。
   * @param {Survey} survey - Survey 实例。
   * @param {object} options - 选项对象。
   * @param {string} options.name - 发生变化的字段名称。
   * @param {*} options.value - 发生变化的字段值。
   * @returns {void}
   */
  onSurveyValueChanged = (survey, options) => {
    this.setState((s) => {
      const surveyData = {
        ...s.surveyData,
        [options.name]: options.value,
      };

      /**
       * 此行导致以下错误：
       * 在审查模式之后，当我们尝试更改某些字段时。
       * 更改任何字段都会导致所有isPriority标志重置。
       */
      //this.getSurveyModel().data = surveyData

      return {
        surveyData,
        isChanged: s.isSurveyReady,
      };
    });
  };

  /**
   * 当标记为非活动字段发生变化时触发的函数。
   * @param {string} n - 字段名称。
   * @param {boolean} v - 新的字段值，表示是否标记为非活动。
   * @returns {void}
   */
  onChangeMarkAsInactiveField = (n, v) => {
    this.toggleInactive(v);
    this.onChangeActivity(v);
  };

  /**
   * 异步更改表单字段值，并设置状态以指示表单是否已更改。
   * @param {string} name - 字段名称。
   * @param {*} value - 新的字段值。
   * @returns {Promise<void>}
   */
  onChangeFormField = async (name, value) => {
    await this.changeFormField(name, value);
    this.setState({ isChanged: true });
  };

  /**
   * 更改表单日期字段值的函数，用于解决自定义字段中的验证消息问题。
   * @param {string} name - 字段名称。
   * @param {*} value - 新的字段值。
   * @returns {void}
   */
  onChangeFormDateField = (name, value) => {
    const data = this.props.form.fields.toJS();

    // 将日期转换为时间戳，并更新状态以指示表单是否已更改
    const newValue = value ? value.getTime() : data[name] || null;
    this.setState({ isChanged: value !== newValue });

    // 异步更改表单字段值
    this.changeFormField(name, newValue);
  };

  // FIXME service plan
  /**
   * 创建服务计划的函数。
   * 根据当前表单数据识别服务计划需求，并根据识别结果更新状态。
   * @returns {void}
   */
  onCreateServicePlan = () => {
    // 识别服务计划需求
    const data = this.identifyServicePlanNeeds();

    // 设置服务计划需求识别状态，并更新相关状态以反映识别结果
    this.setServicePlanNeedIdentification({ data });
    this.setState({
      isToServicePlanEditorOpen: isNotEmpty(data),
      isNoIdentifiedNeedsDialogOpen: isEmpty(data),
    });
  };

  /**
   * 关闭服务计划编辑器的函数。
   * 清空服务计划需求识别状态，并将编辑器状态设置为关闭。
   * @returns {void}
   */
  onCloseToServicePlanEditor = () => {
    // 清空服务计划需求识别状态，并更新编辑器状态以关闭编辑器
    this.setServicePlanNeedIdentification({ data: [] });
    this.setState({ isToServicePlanEditorOpen: false });
  };

  /**
   * 从服务计划需求识别中排除项目的函数。
   * 将给定项目分为包含问题和不包含问题的两组，将分组后的部分添加到已排除的部分列表中，然后更新服务计划需求识别状态并重新识别服务计划需求。
   * 如果是编辑模式，则提交排除的部分，并在成功后显示成功对话框。
   * @param {Array<Object>} items - 要从服务计划需求识别中排除的项目数组。
   * @returns {void}
   */
  onExcludeFromServicePlanNeedIdentification = (items) => {
    // 将项目分为包含问题和不包含问题的两组
    const [groups, sections] = partition(items, (o) => isEmpty(o.questions));

    // 获取当前的服务计划需求识别状态
    const identification = this.state.servicePlanNeedIdentification;

    // 将已排除的部分列表中添加新的部分
    const excludedSections = uniq([...identification.excludedSections, ...map(sections, (o) => o.name)]);

    // 将已排除的问题列表中添加新的问题
    const excludedQuestions = uniq([
      ...identification.excludedQuestions,
      ...flatten(map(groups, (g) => map(g, (o) => o.name))),
    ]);

    // 更新服务计划需求识别状态并重新识别服务计划需求
    this.setServicePlanNeedIdentification(
      {
        excludedSections,
        excludedQuestions,
      },
      () => {
        this.setServicePlanNeedIdentification({
          data: this.identifyServicePlanNeeds(),
        });
      },
    );

    // 定义成功时的回调函数
    const onSuccess = () => {
      this.setState({
        isQuestionsExcludeSuccessDialogOpen: true,
      });
    };

    // 如果是编辑模式，则提交排除的部分，并在成功后显示成功对话框
    if (this.isEditMode()) {
      this.actions.servicePlan.needIdentification
        .submit(
          {
            excludedSections,
            excludedQuestions,
          },
          {
            clientId: this.clientId,
            assessmentId: this.assessmentId,
          },
        )
        .then(Response(onSuccess));
    } else onSuccess();
  };

  /**
   * 服务计划保存成功后的处理函数。
   * 在成功保存服务计划后，关闭服务计划编辑器，根据需要保存并验证当前表单，如果创建了新的服务计划，则更新状态以显示相应的消息。
   * 加载保存的服务计划详情并显示保存成功对话框。
   * @param {Object} data - 包含保存的服务计划ID的数据对象。
   * @returns {void}
   */
  onServicePlanSaveSuccess = ({ data: id }) => {
    // 关闭服务计划编辑器
    this.onCloseToServicePlanEditor();

    // 获取当前评估类型
    const type = this.getAssessmentType();

    // 如果评估类型属于以下类型之一，则根据需要保存并验证当前表单
    if ([IN_HOME, IN_HOME_CARE, ARIZONA_SSM, COMPREHENSIVE, NOR_CAL_COMPREHENSIVE].includes(type?.name)) {
      this.onSaveAndValidateIfNeed(false);
    }

    // 获取服务计划的详情数据
    const { details } = this.props.servicePlan;

    // 如果服务计划详情数据不存在，则更新状态以表示新的服务计划已创建
    if (!details.data) {
      this.setState({ isNewServicePlanCreated: true });
    }

    // 加载保存的服务计划详情，并在加载完成后显示保存成功对话框
    this.loadServicePlanDetails(id).then(() => {
      this.setState({ isServicePlanSaveSuccessDialogOpen: true });
    });
  };

  /**
   * 查看服务计划的处理函数。
   * 导航到服务计划页面，并传递相应的参数以指示应该进入编辑模式，并提供要查看的服务计划的ID。
   * @returns {void}
   */
  onViewServicePlan = () => {
    // 获取服务计划的详情数据
    const { details } = this.props.servicePlan;

    // 导航到服务计划页面，并传递相应的参数以指示应该进入编辑模式，并提供要查看的服务计划的ID
    this.props.history.push(path(`/clients/${this.clientId}/service-plans`), {
      shouldEdit: true,
      servicePlanId: details.data.id,
    });
  };

  /**
   * 获取表单数据的辅助函数。
   * @returns {Object} 包含表单数据的 JavaScript 对象。
   */
  getFormData() {
    return this.props.form.fields.toJS();
  }

  /**
   * 更改表单字段的辅助函数。
   * @param {string} name - 要更改的字段的名称。
   * @param {*} value - 要设置的值。
   * @returns {Promise} 表示更改字段操作的 Promise 对象。
   */
  changeFormField(name, value) {
    return this.actions.form.changeField(name, value);
  }

  /**
   * 批量更改表单字段的辅助函数。
   * @param {Object} changes - 要进行更改的字段和对应的值。
   * @returns {Promise} 表示更改字段操作的 Promise 对象。
   */
  changeFormFields(changes) {
    return this.actions.form.changeFields(changes);
  }

  /**
   * 切换活动状态的辅助函数。
   * @param {boolean} isInactive - 表示是否设置为不活动状态的布尔值。
   */
  toggleInactive(isInactive) {
    this.setState({ isInactive });
  }

  /**
   * 更改步骤的辅助函数。
   * @param {number} step - 要设置的步骤数。
   */
  changeStep(step) {
    majorSurveyPanels = [];
    minorSurveyPanels = [];

    this.setState({ step });

    if (this.canRenderSurvey()) {
      this.setSurveyCurrentPageNo(step - 1);
    }

    // 如果切换到第一步(Guide标签页)，确保正确定位
    if (step === 1) {
      setTimeout(() => {
        this.ensureGuideTabVisible();
      }, 50);
    }
  }

  /**
   * 加载调查的辅助函数。
   * @returns {Promise} 表示加载调查操作的 Promise 对象。
   */
  loadSurvey() {
    return this.actions.survey.load({
      clientId: this.clientId,
      typeId: this.getAssessmentType().id,
    });
  }

  /**
   * 更新调查数据的函数。
   * @returns {Promise} 表示更新调查数据操作的 Promise 对象。
   */
  updateSurvey() {
    return this.loadSurvey().then(
      Response(({ data }) => {
        const model = new Model(JSON.parse(data));
        model.applyTheme(PlainLight);
        model.autoFocusFirstQuestion = false;

        const type = this.getAssessmentType();
        model.clearInvisibleValues = [
          IN_HOME,
          HOUSING,
          CARE_MGMT,
          ARIZONA_SSM,
          IN_HOME_CARE,
          COMPREHENSIVE,
          NOR_CAL_COMPREHENSIVE,
          HMIS_ADULT_CHILD_INTAKE,
          HMIS_ADULT_CHILD_REASESSMENT,
          HMIS_ADULT_CHILD_REASESSMENT_EXIT,
        ].includes(type?.name)
          ? "none"
          : "onHidden";

        this.setState(
          {
            surveyModel: model,
          },
          () => {
            // 模型创建完成后更新数据
            this.updateSurveyModelData();
          },
        );
        return data;
      }),
    );
  }

  /**
   * 加载指定评估的详细信息。
   * @param {number} [assessmentId] - 要加载详细信息的评估的 ID。
   * @returns {Promise} 表示加载详细信息操作的 Promise 对象。
   */
  loadDetails(assessmentId) {
    return this.actions.details.load(this.clientId, assessmentId || this.props.assessmentId);
  }

  /**
   * 加载评分数据。
   * @returns {Promise} 表示加载评分数据操作的 Promise 对象。
   */
  loadScore() {
    return this.actions.score.load({
      clientId: this.clientId,
      typeId: this.getAssessmentType().id,
      dataJson: JSON.stringify(this.state.surveyModel.data),
    });
  }

  /**
   * 加载管理数据。
   */
  loadManagement() {
    this.actions.management.load({
      clientId: this.clientId,
      typeId: this.getAssessmentType().id,
    });
  }

  /**
   * 加载服务计划的详细信息。
   * @param {number} id - 要加载详细信息的服务计划的 ID。
   * @returns {Promise} 表示加载服务计划详细信息操作的 Promise 对象。
   */
  loadServicePlanDetails(id) {
    return this.props.actions.servicePlan.details.load(this.clientId, id);
  }

  changeSurveyValue(name, value) {
    this.setState((s) => ({
      surveyData: {
        ...s.surveyData,
        [name]: value,
      },
    }));
  }

  emptySurveyModel() {
    const model = new Model();
    model.applyTheme(PlainLight);
    model.autoFocusFirstQuestion = false;

    const type = this.getAssessmentType();
    model.clearInvisibleValues = [
      IN_HOME,
      HOUSING,
      CARE_MGMT,
      ARIZONA_SSM,
      IN_HOME_CARE,
      COMPREHENSIVE,
      NOR_CAL_COMPREHENSIVE,
      HMIS_ADULT_CHILD_INTAKE,
      HMIS_ADULT_CHILD_REASESSMENT,
      HMIS_ADULT_CHILD_REASESSMENT_EXIT,
    ].includes(type?.name)
      ? "none"
      : "onHidden";

    return model;
  }

  /**
   * 克隆调查模型。
   * @returns {module:survey.SurveyModel} 克隆的调查模型对象。
   */
  cloneSurveyModel() {
    // 使用目录中存储的调查值创建新的调查模型
    const model = new Model(this.props.directory.assessment.survey.value);
    model.applyTheme(PlainLight);
    model.autoFocusFirstQuestion = false;

    const type = this.getAssessmentType();
    model.clearInvisibleValues = [
      IN_HOME,
      HOUSING,
      CARE_MGMT,
      ARIZONA_SSM,
      IN_HOME_CARE,
      COMPREHENSIVE,
      NOR_CAL_COMPREHENSIVE,
      HMIS_ADULT_CHILD_INTAKE,
      HMIS_ADULT_CHILD_REASESSMENT,
      HMIS_ADULT_CHILD_REASESSMENT_EXIT,
    ].includes(type?.name)
      ? "none"
      : "onHidden";

    // 将当前状态中的调查数据设置为新模型的数据
    model.data = this.state.surveyData;
    return model;
  }

  updateSurveyModelData() {
    const { surveyModel, surveyData } = this.state;
    const type = this.getAssessmentType();

    if (surveyModel && surveyData) {
      // 只有在复制或编辑模式且不是 COMPREHENSIVE 类型时才设置数据
      if ((this.props.isCopying || this.isEditMode()) && !type?.name?.includes(COMPREHENSIVE)) {
        surveyModel.data = surveyData;
      }
    }
  }

  /**
   * 保存评估数据。
   * @param {object} options - 保存选项。
   * @param {boolean} [options.shouldComplete=false] - 指示是否应将评估标记为已完成。
   * @param {boolean} [options.shouldMarkAsInactive=false] - 指示是否应将评估标记为不活动。
   * @param {boolean} [options.isAutoSave=false] - 指示是否自动保存。
   * @returns {Promise} 保存评估的承诺。
   */
  save({ shouldComplete = false, shouldMarkAsInactive = false, isAutoSave = false } = {}) {
    // 获取props中的详情和是否复制标志
    const { details, isCopying } = this.props;

    // 从状态中获取服务计划需求识别的排除部分和排除问题
    const { excludedSections, excludedQuestions } = this.state.servicePlanNeedIdentification;

    // 从表单数据中获取类型ID、评论、联系人ID和分配日期
    const { typeId, comment, contactId, dateAssigned } = this.getFormData();

    // 克隆调查模型
    const model = this.cloneSurveyModel();
    model.completeLastPage(); // 完成最后一页以确保数据有效

    // 如果调查数据中没有创建日期，则将当前日期设置为创建日期
    if (!model.data.dateCreated) {
      model.setValue("dateCreated", DU.format(Date.now(), DATE_FORMAT));
    }

    // 确定评估状态
    let statusName = isCopying ? IN_PROCESS : details.data?.statusName;
    if (shouldComplete) statusName = COMPLETED;
    else if (shouldMarkAsInactive) statusName = INACTIVE;
    else if (statusName === INACTIVE && !shouldMarkAsInactive) statusName = IN_PROCESS;
    else if (!statusName) statusName = this.isDraftEnabled() ? IN_PROCESS : COMPLETED;

    // 构造保存数据对象
    const data = {
      typeId,
      comment,
      contactId,
      statusName,
      dateAssigned,
      timeToEdit: time.passedFromSaved(), // 计算自上次保存以来经过的时间
      dataJson: JSON.stringify(model.data), // 将调查数据转换为JSON字符串
      id: !isCopying ? this.assessmentId : null, // 如果不是复制操作，则使用评估ID，否则设置为null
      hasErrors: isNotEmpty(this.getInvalidSurveyPageIndexes()), // 检查是否有无效的调查页
      servicePlanNeedIdentificationExcludedSections: excludedSections, // 服务计划需求识别的排除部分
      servicePlanNeedIdentificationExcludedQuestions: excludedQuestions, // 服务计划需求识别的排除问题
      isAutoSave, // 标记是否为自动保存
    };

    // 如果不允许草稿，则设置完成日期
    if (!this.isDraftEnabled()) data.dateCompleted = dateAssigned;

    // 提交保存数据并返回承诺
    return this.actions.form.submit(this.clientId, data);
  }

  /**
   * 验证调查表单数据。
   * @returns {Promise<boolean>} 返回一个boolean，表示验证结果。
   */
  validateSurvey() {
    const survey = this.state.surveyModel;
    survey.checkErrorsMode = "onValueChanged"; // 设置错误检查模式为在值更改时触发

    // 获取无效调查页的索引数组
    const indexes = this.getInvalidSurveyPageIndexes(true);

    // 如果存在无效的调查页，则将当前页面更改为第一个无效页面
    const index = isNotEmpty(indexes) ? first(indexes) : -1;
    if (index > 0 && index !== survey.currentPageNo) {
      this.setSurveyCurrentPageNo(index); // 设置当前页面为第一个无效页面
      this.setState({ step: index + 1 }); // 更新步骤状态以反映当前页面
    }

    // 更新状态以存储无效调查页的索引数组
    this.setState({ invalidSurveyPageIndexes: indexes });

    // 返回一个延迟，以确保界面更新完成后再执行下一步操作
    return defer().then(() => {
      if (index < 0) return true; // 如果不存在无效页面，则返回true表示验证通过

      // 如果存在无效页面，则滚动到底部显示错误消息
      this.modalRef.current?.scrollBottom(0) || noop();

      // 返回一个延迟，以确保滚动到底部后再将焦点定位到第一个错误的问题上
      return defer().then(() => {
        survey.currentPage.focusFirstErrorQuestion(); // 将焦点定位到第一个错误的问题上
        return false; // 返回false表示验证未通过
      });
    });
  }

  /**
   * 滚动到第一个调查错误问题。
   * 这个函数用于滚动到第一个调查表单中出现错误的问题。
   */
  async scrollToFirstSurveyErrorQuestion() {
    this.scrollBottom(0); // 滚动到底部

    await defer(100); // 等待100毫秒

    this.focusFirstErrorSurveyQuestion(); // 将焦点定位到第一个错误的问题上

    // TODO  临时修复滚动到错误问题的bug，需要延迟再次滚动和聚焦
    await defer(100); // 等待100毫秒
    this.scrollBottom(0); // 再次滚动到底部

    await defer(100); // 再次等待100毫秒
    this.focusFirstErrorSurveyQuestion(); // 再次将焦点定位到第一个错误的问题上
  }

  /**
   * 检查当前是否处于编辑模式。
   * @returns {boolean} 如果当前处于编辑模式，则返回true，否则返回false。
   */
  isEditMode() {
    return isNumber(this.assessmentId);
  }

  /**
   * 获取调查表单模型。
   * @returns {Survey.Model} 调查表单模型。
   */
  getSurveyModel() {
    return this.state.surveyModel;
  }

  /**
   * 设置调查表单模型的当前页码。
   * @param {number} v 要设置的页码值。
   */
  setSurveyCurrentPageNo(v) {
    this.getSurveyModel().currentPageNo = v;
  }

  /**
   * 检查是否可以渲染调查表单。
   * @returns {boolean} 如果可以渲染调查表单，则返回true，否则返回false。
   */
  canRenderSurvey() {
    const { surveyData, surveyModel } = this.state;
    return surveyModel && (this.isEditMode() ? surveyData : true);
  }

  ensureGuideTabVisible() {
    // 确保Guide标签页可见并正确定位
    const { step } = this.state;
    if (step === 1) {
      // 使用与 AssessmentViewer 相同的滚动方式
      const modal = this.modalRef.current;
      if (modal) modal.scrollTop(0);

      // 如果有调查问卷，确保显示第一页
      if (this.canRenderSurvey()) {
        this.setSurveyCurrentPageNo(0);
      }
    }
  }

  scroll(offset) {
    this.modalRef.current.scroll({ top: offset });
  }

  scrollBottom(duration) {
    this.modalRef.current.scrollBottom(duration);
  }

  showAllQuestions() {
    const questions = this.state.surveyModel.getAllQuestions();

    each(questions, (question) => {
      question.visible = true;
    });
  }

  hideAllQuestions() {
    const questions = this.state.surveyModel.getAllQuestions();

    each(questions, (question) => {
      question.visible = false;
    });

    return new Promise((resolve) => {
      setTimeout(() => {
        const questions = this.state.surveyModel.getAllQuestions();

        each(questions, (question) => {
          question.visible = false;
        });

        resolve();
      });
    });
  }

  /**
   * 将界面设置为审查模式。
   */
  setReviewMode() {
    this.setState({
      isReviewModeTransition: true,
    });

    setTimeout(() => {
      majorSurveyPanels = [];
      minorSurveyPanels = [];

      each(surveyPages, (p) => {
        p.isVisible = false;
      });
      // 使用克隆的调查表单模型作为调查审查模型。

      const surveyReviewModel = this.cloneSurveyModel();

      surveyReviewModel.mode = "display";
      surveyReviewModel.isSinglePage = true;
      const questions = surveyReviewModel.getAllQuestions();

      each(questions, (question, i) => {
        question.visible = false;

        const isPriority = questions[i + 1] && questions[i + 1].isPriority && questions[i + 1].value;

        if (isPriority) {
          question.visible = true;
        }
      });

      setTimeout(() => {
        this.setState({
          surveyReviewModel,
          isReviewMode: true,
          isReviewModeTransition: false,
        });
      }, 500);
    }, 100);
  }

  /**
   * 取消审查模式。
   */
  cancelReviewMode() {
    majorSurveyPanels = [];
    minorSurveyPanels = [];

    each(surveyPages, (p) => {
      p.isVisible = true;
    });

    this.setState({
      isReviewMode: false,
      surveyReviewModel: null,
    });
  }

  /**
   * 获取目录数据。
   */
  getDirectoryData() {
    return this.props.getDirectoryData({
      domains: ["servicePlan", "domain"],
      programTypes: ["servicePlan", "program", "type"],
      programSubTypes: ["servicePlan", "program", "subtype"],
    });
  }

  /**
   * 获取调查表单中无效页面的索引。
   * @param {boolean} shouldRefresh - 是否应刷新页面以获取最新的无效页面索引。
   * @returns {number[]} - 无效页面的索引数组。
   */
  getInvalidSurveyPageIndexes(shouldRefresh = false) {
    if (shouldRefresh) {
      const indexes = [];
      const survey = this.state.surveyModel;

      if (survey)
        each(survey.visiblePages, (page) => {
          if (page.hasErrors()) indexes.push(page.visibleIndex);
        });

      return indexes;
    }

    return this.state.invalidSurveyPageIndexes || [];
  }

  /**
   * 将焦点设置在调查表单中的第一个错误问题上。
   */
  focusFirstErrorSurveyQuestion() {
    this.getSurveyModel().currentPage.focusFirstErrorQuestion();
  }

  /**
   * 获取评分数据。
   * @returns {any} - 评分数据。
   */
  getScore() {
    return this.props.directory.assessment.score.value;
  }

  /**
   * 检查草稿功能是否已启用。
   * @returns {boolean} - 如果草稿功能已启用，则为 true；否则为 false。
   */
  isDraftEnabled() {
    const type = this.getAssessmentType();
    return ASSESSMENT_TYPES_WITH_ENABLED_DRAFT.includes(type?.name);
  }

  /**
   * 获取评分警报信息。
   * @returns {object|null} - 评分警报信息对象，如果未找到匹配项，则为 null。
   */
  getScoringAlert() {
    const mg = this.props.directory.assessment.management;
    const score = this.getScore();

    if (isNumber(score) && mg.data) {
      const item = find(mg.data.scale, (o) => score >= o.scoreLow && score <= o.scoreHigh);

      return item
        ? {
            text: item.severity,
            type: LEVELS_ALERT_TYPES[item.highlighting?.toUpperCase()],
          }
        : {
            text: "Severe anxiety disorder.",
            type: LEVELS_ALERT_TYPES.HIGH,
          };
    }

    return null;
  }

  /**
   * 获取筛查结果警报信息。
   * @returns {object|null} - 筛查结果警报信息对象，如果未找到匹配项，则为 null。
   */
  getScreeningResultsAlert() {
    const score = this.getScore();
    const { directory } = this.props;
    const { management } = directory.assessment;

    if (isNumber(score) && management.data) {
      const item = find(management.data.scale, (o) => score >= o.scoreLow && score <= o.scoreHigh);

      if (item?.highlighting === "High") {
        return {
          ...item,
          type: "redmark",
        };
      } else if (item?.highlighting === "Medium") {
        return {
          ...item,
          type: "yellowmark",
        };
      } else if (item?.highlighting === "Low") {
        return {
          ...item,
          type: "greenmark",
        };
      }
    }
  }

  /**
   * 获取乳腺X光检查结果警报信息。
   * @returns {object|null} - 乳腺X光检查结果警报信息对象，如果未找到匹配项，则为 null。
   */
  getMammogramResultsAlert() {
    const score = this.getScore();

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

  /**
   * 根据名称获取调查问题。
   * @param {string} name - 调查问题的名称。
   * @returns {object|null} - 调查问题对象，如果未找到匹配项，则为 null。
   */
  getSurveyQuestionByName(name) {
    return this.state.surveyModel.getQuestionByName(name);
  }

  /**
   * 根据面板名称获取调查问题集合。
   * @param {string} name - 面板名称。
   * @param {object} options - 选项对象。
   * @param {function} options.filter - 过滤器函数，默认为 () => true。
   * @param {function} options.mapper - 映射器函数，默认为 (o) => o。
   * @returns {array} - 调查问题集合。
   */
  getSurveyPanelQuestions(name, options = {}) {
    const defaultMapper = (o) => o;
    const defaultFilter = () => true;

    const panel = this.state.surveyModel.getPanelByName(SURVEY_PANEL_TITLE[name]);

    return map(filter(panel.questions, options.filter || defaultFilter), options.mapper || defaultMapper);
  }

  /**
   * 根据名称获取调查问题及其相关评论。
   * @param {string} name - 调查问题的名称。
   * @returns {object|null} - 包含调查问题和相关评论的对象，如果未找到匹配项，则为 null。
   */
  getSurveyQuestionAndRelatedCommentByName(name) {
    const question = this.state.surveyModel.getQuestionByName(name);

    if (!question) return null;

    const result = { question };

    const questions = reject(question.parent.questions, (o) => o.getType() === "boolean" || !o.isVisible);

    let comment = null;

    if (question.relatedComment !== "none") {
      comment = find(questions, (o) => o?.getType() === "comment" && o.name === question.relatedComment);

      if (!comment) {
        const i = findIndex(questions, (o) => o.name === name);

        if (questions[i + 1]?.getType() === "comment") {
          comment = questions[i + 1];
        }
      }
    }

    result.comment = comment;

    return result;
  }

  /**
   * 获取调查面板问题及相关评论。
   * @param {string} name - 面板名称。
   * @returns {array} - 包含问题和相关评论的数组。
   */
  getSurveyPanelQuestionsAndRelatedComments(name) {
    const questions = this.getSurveyPanelQuestions(name, { filter: (o) => o.getType() !== "boolean" });

    const entries = [];

    each(questions, (o, i) => {
      if (o.getType() !== "comment") {
        entries.push({
          question: o,
          comment: questions[i + 1],
        });
      }
    });

    return entries;
  }

  /**
   * 获取调查面板得分。
   * @param {string} name - 面板名称。
   * @returns {number} - 面板得分。
   */
  getSurveyPanelScore(name) {
    const panel = this.state.surveyModel.getPanelByName(SURVEY_PANEL_TITLE[name]);

    let score = 0;
    const scoring = SCORING[name];

    each(panel.questions, (q) => {
      if (q.value && !["boolean", "comment"].includes(q.getType())) {
        if (scoring[q.name]) {
          score += scoring[q.name][q.value] || 0;
        } else {
          console.error(`Error! Question name is incorrect.`);
        }
      }
    });

    return score;
  }

  /**
   * 设置服务计划需求识别状态。
   * @param {object} state - 状态对象。
   * @param {Function} cb - 回调函数。
   */
  setServicePlanNeedIdentification(state, cb) {
    this.setState(
      (s) => ({
        servicePlanNeedIdentification: {
          ...s.servicePlanNeedIdentification,
          ...state,
        },
      }),
      cb,
    );
  }

  /**
   * 识别服务计划需求。
   * @returns {array} - 服务计划需求数组。
   */
  identifyServicePlanNeeds() {
    const { servicePlan } = this.props;
    const type = this.getAssessmentType();

    const {
      surveyData,
      servicePlanNeedIdentification: { excludedSections, excludedQuestions },
    } = this.state;

    const { domains, programTypes, programSubTypes } = this.getDirectoryData();

    const data = [];

    function isNeedIdentified(condition) {
      return all(condition, (cnQVal, cnQName) => {
        const svQValue = surveyData[cnQName];

        if (svQValue) {
          if (isString(cnQVal)) {
            return isString(svQValue) && uc(svQValue) === uc(cnQVal);
          } else if (isArray(cnQVal)) {
            return any(cnQVal, (o) => {
              if (isString(o)) {
                return isString(svQValue) && uc(o) === uc(svQValue);
              }

              if (isObject(o)) {
                return all(o, (v, k) => {
                  return isString(svQValue[k]) && uc(v) === uc(svQValue[k]);
                });
              }

              return false;
            });
          } else if (isObject(cnQVal)) {
            return all(cnQVal, (v, k) => {
              return isString(svQValue[k]) && uc(v) === uc(svQValue[k]);
            });
          } else return false;
        } else return false;
      });
    }

    function needExists(need) {
      const fields = [
        "domainName",
        "needOpportunity",
        "programTypeName",
        "programSubTypeName",
        "activationOrEducationTask",
      ];

      return any(servicePlan.details.data?.needs, (n) => {
        const { need: protoNeed } =
          find(SURVEY_QUESTIONS_TO_SERVICE_PLAN_NEEDS, (o) => n?.needOpportunity?.includes(o.need?.needOpportunity)) ||
          {};

        return all(pick(need, ...fields), (v, k) => {
          if (need.domainName === "EDUCATION_TASK") {
            return n.domainName === need.domainName;
          }

          if (k === "needOpportunity") {
            return v.includes(protoNeed?.needOpportunity);
          }

          return isString(v) ? v.includes(n[k]) : v === n[k];
        });
      });
    }

    each(SURVEY_QUESTIONS_TO_SERVICE_PLAN_NEEDS, ({ condition, need }) => {
      if (isNeedIdentified(condition)) {
        need = {
          ...need,
          priorityId: 2,
        };

        const domain = findWhere(domains, { name: need.domainName });

        need.domainId = domain?.id;

        if (need.domainName === "EDUCATION_TASK") {
          need.targetCompletionDate = Date.now();
        }

        if (need.programTypeName) {
          const programType = findWhere(programTypes, {
            domainId: domain?.id,
            name: need.programTypeName,
          });

          need.programTypeId = programType?.id;
        }

        if (need.programSubTypeName) {
          const programSubType = findWhere(programSubTypes, { name: need.programSubTypeName });

          need.programSubTypeId = programSubType?.id;
        }

        const questions = [];

        for (let name of keys(condition)) {
          const { question, comment } = this.getSurveyQuestionAndRelatedCommentByName(name) ?? {};

          if (!question) continue;

          const { value, title } = question;

          questions.push({
            name,
            value,
            title,
            isExcluded: excludedQuestions.includes(name),
          });

          //for questions of type matrix we dont have to show comment, only needTitle + values of Yes answered rows
          if (question.getType?.() !== "matrix" && comment && comment.value) {
            need.needOpportunity += ` ${comment.value}`;
          }

          if (isObject(value)) {
            need.needOpportunity +=
              map(
                pick(value, (v) => uc(v) === "YES"),
                (v, k) => snakeToTitle(k),
              ).join(", ") + ".";
          }

          if (type?.name === ARIZONA_SSM && name === "Parenting Skills") {
            for (const qName of ["Active CPS Case?", "Previous CPS Involvement?"]) {
              let qVal = this.getSurveyQuestionByName(qName)?.value;
              if (qVal) need.needOpportunity += `\n${qName}: ${qVal}`;
            }
          }
        }

        data.push({
          need: {
            ...need,
            alreadyExists: needExists(need),
          },
          questions,
        });
      }
    });

    if (type?.name?.includes(COMPREHENSIVE)) {
      const { GAD_7, PHQ_9, CAGE_AID, CAREGIVER, PATIENT_EDUCATION } = SURVEY_PANEL_NAME;

      const MIN_PANEL_SCORE = {
        [GAD_7]: 5,
        [PHQ_9]: 5,
        [CAGE_AID]: 2,
        [CAREGIVER]: 1,
        [PATIENT_EDUCATION]: 1,
      };

      const getPanelCommentsAsText = (name, options = {}) => {
        return compact(
          map(
            filter(this.getSurveyPanelQuestionsAndRelatedComments(name), options.filter || (() => true)),
            ({ question, comment }) => {
              let scoring = SCORING[name][question.name];

              const score = scoring ? scoring[question.value] : 0;

              return score === 1 && comment.value ? comment.value : null;
            },
          ),
        ).join(". ");
      };

      each(SURVEY_PANEL_NAME, (panelName) => {
        const score = this.getSurveyPanelScore(panelName);

        if (score >= MIN_PANEL_SCORE[panelName]) {
          const need = {
            priorityId: 2,
            ...SURVEY_SECTIONS_TO_SERVICE_PLAN_NEEDS[panelName],
          };

          const domain = findWhere(domains, { name: need.domainName });

          need.domainId = domain?.id;

          if (need.programTypeName) {
            const programType = findWhere(programTypes, { name: need.programTypeName });

            need.programTypeId = programType?.id;
          }

          if (need.programSubTypeName) {
            const programSubType = findWhere(programSubTypes, { name: need.programSubTypeName });

            need.programSubTypeId = programSubType?.id;
          }

          if ([GAD_7, PHQ_9, CAGE_AID].includes(panelName)) {
            need.needOpportunity += score;
          }

          if ([PATIENT_EDUCATION, CAREGIVER].includes(panelName)) {
            const text = getPanelCommentsAsText(panelName, { filter: (o) => !!o.question.value });

            if (panelName === PATIENT_EDUCATION) {
              need.activationOrEducationTask += text ? ` ${text}` : "";
              need.targetCompletionDate = Date.now();
            } else {
              need.needOpportunity += text ? ` ${text}` : "";
            }
          }

          const questions = this.getSurveyPanelQuestions(panelName, {
            filter: (o) => !["boolean", "comment"].includes(o.getType()),
            mapper: ({ name, title, value }) => ({ name, title, value }),
          });

          const panelTitle = SURVEY_PANEL_TITLE[panelName];

          data.push({
            need: {
              ...need,
              alreadyExists: needExists(need),
            },
            section: {
              name: panelTitle,
              title: panelTitle,
              score,
              questions,
              isExcluded: excludedSections.includes(panelTitle),
            },
          });
        }
      });
    }

    return data;
  }

  /**
   * 获取未添加到服务计划的已识别需求数量。
   * @returns {number} - 未添加到服务计划的已识别需求数量。
   */
  getNotAddedToServicePlanIdentifiedNeedCount() {
    const data = this.identifyServicePlanNeeds();
    return filter(data, (o) => !o.need.alreadyExists).length;
  }

  isRiskIdentified() {
    const type = this.getAssessmentType();
    // FAST, SHORT, REVISED, SCREEN
    if ([GAD7, PHQ9].includes(type?.name)) {
      const score = this.getScore();

      const mg = this.props.directory.assessment.management;

      if (isNumber(score) && mg.data) {
        const item = find(mg.data.scale, (o) => score >= o.scoreLow && score <= o.scoreHigh);

        return item ? item.isRiskIdentified : true;
      }
    }

    if (type?.name === CARE_MGMT) {
      let score = 0;

      const scoring = SCORING.CARE_MANAGEMENT;

      each(this.state.surveyData, (v, q) => {
        score += scoring[q] ? (scoring[q][v] ?? 0) : 0;
      });

      return score > 0;
    }

    return false;
  }

  getAssessmentTypeById(typeId) {
    if (!isInteger(typeId)) return;

    let type = null;

    const types = this.props.directory.assessment.type.list.dataSource.data;

    each(types, (o) => {
      const t = findWhere(o.types, { id: typeId });
      if (t) type = t;
    });
    /**  type is first page select one
     * {
     *  canAdd: true,
     *id: 3 ,
     *  name:"GAD7"
     * resultTitle:  "Symptom Severity",
     * shortTitle:"GAD-7"
     * title :"GAD-7 (General Anxiety Disorder-7)"
     * }
     *
     */
    return type;
  }

  getAssessmentType() {
    return this.getAssessmentTypeById(
      this.props.form.fields.typeId, // first page select id
    );
  }

  // modal title
  getTitle() {
    const { isCopying } = this.props;
    const { step, isReviewMode } = this.state;

    if (isReviewMode) return "Priority Questions Review";

    if (step > 0) {
      const type = this.getAssessmentType();

      const shortTitle = type?.shortTitle || "";

      return !type?.name?.includes(COMPREHENSIVE) &&
        !type?.name?.includes(BENEFICIARY_COLORECTAL_CANCER_SCREENING) &&
        !type?.name?.includes(BENEFICIARY_MAMMOGRAM_SCREENING) &&
        step === 2
        ? `${shortTitle} Scoring`
        : `${!isCopying && this.isEditMode() ? "Edit" : "Add"} ${shortTitle}`;
    }

    return "Select Assessment Type";
  }

  render() {
    const {
      clientFirstName,
      clientMiddleName,
      clientLastName,
      clientBirthDate,
      step,
      isInactive,
      fastComments,

      shouldCancelValidation,

      surveyData,
      surveyModel,
      surveyReviewModel,

      scrollOffset,

      isChanged,
      isReviewMode,
      isReviewModeTransition,

      isToServicePlanEditorOpen,
      isNoIdentifiedNeedsDialogOpen,
      isServicePlanSaveSuccessDialogOpen,
      isQuestionsExcludeSuccessDialogOpen,

      isNewServicePlanCreated,
      servicePlanNeedIdentification,

      isSurveyReady,
    } = this.state;

    const {
      isOpen,
      form,
      details,
      client,
      clientId,
      isCopying,
      assessmentId,
      directory: {
        assessment: { score, survey, management },
      },
      shouldAddNeedsToServicePlan,
    } = this.props;

    const { dateAssigned, dateCompleted } = form.fields;

    const isFetching = survey.isFetching || details.isFetching || form.isFetching;

    const type = this.getAssessmentType();
    const scoringAlert = this.getScoringAlert();
    const statusName = isCopying ? IN_PROCESS : details.data?.statusName;

    const invalidPageIndexes = this.getInvalidSurveyPageIndexes();

    const hasErrors = isNotEmpty(invalidPageIndexes);

    const hasTabs = type?.name?.includes(COMPREHENSIVE) && !isReviewMode;
    /**
     * step 在选择问题的时候是0 进入问题第一页为1 next按钮 +1
     */
    const screeningAlertResult =
      [BENEFICIARY_COLORECTAL_CANCER_SCREENING].includes(type?.name) && this.getScreeningResultsAlert();

    const mammogramResult = [BENEFICIARY_MAMMOGRAM_SCREENING].includes(type?.name) && this.getMammogramResultsAlert();
    return (
      <>
        <LoadAssessmentDefaultDataAction //加载评估 默认数据操作
          isMultiple
          params={{
            clientId,
            assessmentId: this.props.parentAssessmentId,
            assessmentTypeId: type?.id,
          }}
          shouldPerform={(prevParams) =>
            !this.isEditMode() && Boolean(type) && type?.id !== prevParams.assessmentTypeId
          }
          onPerformed={Response(({ data }) => {
            this.setState({
              surveyData: prepareAssessmentData(data, type.name),
            });
          })}
        />
        {/*//加载服务计划域行动*/}
        <LoadServicePlanDomainsAction />
        {/*//加载服务计划程序类型操作*/}
        <LoadServicePlanProgramTypesAction />
        {/* //加载服务计划程序子类型操作*/}
        <LoadServicePlanProgramSubTypesAction />

        <Modal
          hasCloseBtn
          isOpen={isOpen}
          ref={this.modalRef}
          onClose={this.onClose}
          scrollOffset={scrollOffset}
          className={cn(
            "AssessmentEditor",
            { AssessmentEditor_fetching: isFetching },
            { AssessmentEditor_mode_review: isReviewMode },
          )}
          title={this.getTitle()}
          headerClassName="AssessmentEditor-Header"
          bodyClassName="AssessmentEditor-Body"
          footerClassName="AssessmentEditor-Footer"
          renderHeader={(title) => (
            <div className="d-flex justify-content-between align-items-center">
              <div>{title}</div>
              {!isReviewMode && type?.name?.includes(COMPREHENSIVE) && step > 0 && (
                <div onClick={this.onSaveAndValidate} className="btn AssessmentEditor-ValidateAndSaveBtn">
                  <Diskette className="AssessmentEditor-ValidateAndSaveIcon" />
                </div>
              )}
            </div>
          )}
          renderFooter={() =>
            type &&
            (type?.name?.includes(COMPREHENSIVE) ? (
              <>
                {step === 0 && (
                  <>
                    <Button
                      outline
                      color="success"
                      disabled={form.isFetching || isReviewModeTransition}
                      onClick={this.onClose}
                    >
                      Close
                    </Button>
                    <Button color="success" disabled={form.isFetching || isReviewModeTransition} onClick={this.onNext}>
                      Next
                    </Button>
                  </>
                )}
                {step > 0 && (
                  <div className="AssessmentEditor-FooterBtns">
                    <div className="AssessmentEditor-FooterNavBtns">
                      {step > 1 && (
                        <Button
                          color="link"
                          disabled={form.isFetching || isReviewModeTransition}
                          className="AssessmentEditor-BackBtn"
                          onClick={this.onBack}
                        >
                          Back
                        </Button>
                      )}
                      {isReviewMode && step === 1 && (
                        <Button
                          color="link"
                          disabled={form.isFetching || isReviewModeTransition}
                          className="AssessmentEditor-BackBtn"
                          onClick={this.onCancelReview}
                        >
                          Back
                        </Button>
                      )}
                      {!isReviewMode && step < COMPREHENSIVE_TABS.length && (
                        <Button
                          color="link"
                          disabled={form.isFetching || isReviewModeTransition}
                          className="AssessmentEditor-NextBtn"
                          onClick={this.onNext}
                        >
                          Next
                        </Button>
                      )}
                    </div>
                    {!isReviewMode && (
                      <CheckboxField
                        name="markAsInactive"
                        value={isInactive}
                        className="AssessmentForm-MarkAsInactiveField"
                        label="Mark as Inactive"
                        isDisabled={form.isFetching || isReviewModeTransition}
                        onChange={this.onChangeMarkAsInactiveField}
                      />
                    )}
                    <div className="AssessmentEditor-FooterActionBtns">
                      {isReviewMode ? (
                        <>
                          {!isInactive && (
                            <Button color="success" disabled={form.isFetching} onClick={this.onComplete}>
                              Complete
                            </Button>
                          )}
                        </>
                      ) : (
                        <>
                          {details.data?.statusName !== INACTIVE && (
                            <>
                              <div id="create-service-plan-btn" className="d-inline-block margin-right-10">
                                <Button
                                  outline
                                  color="success"
                                  className="AssessmentForm-CreateServicePlanBtn"
                                  disabled={form.isFetching || isReviewModeTransition || !this.isEditMode()}
                                  onClick={this.onCreateServicePlan}
                                >
                                  <span className="AssessmentForm-CreateServicePlanBtnOptText">Create </span>Service
                                  Plan
                                </Button>
                              </div>
                              {!this.isEditMode() && (
                                <Tooltip
                                  placement="top"
                                  target="create-service-plan-btn"
                                  trigger="click hover"
                                  modifiers={[
                                    {
                                      name: "offset",
                                      options: { offset: [0, 6] },
                                    },
                                    {
                                      name: "preventOverflow",
                                      options: { boundary: document.body },
                                    },
                                  ]}
                                >
                                  The feature will be enabled when the assessment is created and saved
                                </Tooltip>
                              )}
                            </>
                          )}
                          <Button
                            outline
                            color="success"
                            disabled={form.isFetching || isReviewModeTransition}
                            onClick={this.onSaveAndClose}
                          >
                            Save & Close
                          </Button>
                          <Button
                            color="success"
                            disabled={form.isFetching || isReviewModeTransition}
                            onClick={this.onReview}
                          >
                            Review
                          </Button>
                        </>
                      )}
                    </div>
                  </div>
                )}
              </>
            ) : (
              <>
                {step === 0 && (
                  <>
                    <Button outline color="success" disabled={form.isFetching} onClick={this.onClose}>
                      Close
                    </Button>

                    <Button color="success" disabled={form.isFetching} onClick={this.onNext}>
                      Next
                    </Button>
                  </>
                )}
                {step === 1 && (
                  <Button outline color="success" disabled={form.isFetching} onClick={this.onClose}>
                    Cancel
                  </Button>
                )}
                {step > 1 && (
                  <>
                    {[BENEFICIARY_COLORECTAL_CANCER_SCREENING, BENEFICIARY_MAMMOGRAM_SCREENING].includes(
                      type?.name,
                    ) && (
                      <Button outline color="success" disabled={form.isFetching} onClick={this.onClose}>
                        Cancel
                      </Button>
                    )}
                    <Button outline color="success" disabled={form.isFetching} onClick={this.onBack}>
                      Back
                    </Button>
                    {[BENEFICIARY_COLORECTAL_CANCER_SCREENING, BENEFICIARY_MAMMOGRAM_SCREENING].includes(
                      type?.name,
                    ) && (
                      <>
                        {statusName !== COMPLETED && (
                          <Button color="success" disabled={form.isFetching} onClick={this.onSave}>
                            Save Draft
                          </Button>
                        )}
                        {step === 2 && (
                          <Button color="success" disabled={form.isFetching} onClick={this.onNext}>
                            Next
                          </Button>
                        )}
                        {step === 3 && (
                          <Button color="success" disabled={!isChanged || form.isFetching} onClick={this.onSave}>
                            Submit
                          </Button>
                        )}
                      </>
                    )}
                  </>
                )}

                {[GAD7, PHQ9, FAST, SHORT, REVISED, SCREEN].includes(type?.name) ? (
                  <>
                    {step === 1 && (
                      <Button color="success" disabled={form.isFetching} onClick={this.onNext}>
                        Next
                      </Button>
                    )}
                    {step === 2 && (
                      <>
                        <Button color="success" disabled={!isChanged || form.isFetching} onClick={this.onSave}>
                          Submit
                        </Button>
                      </>
                    )}
                  </>
                ) : (
                  <>
                    {step === 1 && (
                      <>
                        {[IN_HOME, HOUSING, ARIZONA_SSM, IN_HOME_CARE].includes(type?.name) && (
                          <>
                            <div id="create-service-plan-btn" className="d-inline-block margin-right-10">
                              <Button
                                outline
                                color="success"
                                className="AssessmentForm-CreateServicePlanBtn"
                                disabled={form.isFetching || isReviewModeTransition || !this.isEditMode() || isCopying}
                                onClick={this.onCreateServicePlan}
                              >
                                <span className="AssessmentForm-CreateServicePlanBtnOptText">Create </span>Service Plan
                              </Button>
                            </div>
                            {(!this.isEditMode() || isCopying) && (
                              <Tooltip
                                placement="top"
                                target="create-service-plan-btn"
                                trigger="click hover"
                                modifiers={[
                                  {
                                    name: "offset",
                                    options: { offset: [0, 6] },
                                  },
                                  {
                                    name: "preventOverflow",
                                    options: { boundary: document.body },
                                  },
                                ]}
                              >
                                The feature will be enabled when the assessment is created and saved
                              </Tooltip>
                            )}
                          </>
                        )}
                        {[
                          HOUSING,
                          ARIZONA_SSM,
                          HMIS_ADULT_CHILD_INTAKE,
                          HMIS_ADULT_CHILD_REASESSMENT,
                          HMIS_ADULT_CHILD_REASESSMENT_EXIT,
                        ].includes(type?.name) ? (
                          <>
                            {statusName !== COMPLETED && (
                              <Button color="success" disabled={form.isFetching} onClick={this.onSave}>
                                Save Draft
                              </Button>
                            )}
                            <Button color="success" disabled={form.isFetching} onClick={this.onComplete}>
                              Complete
                            </Button>
                          </>
                        ) : (
                          <>
                            {[BENEFICIARY_COLORECTAL_CANCER_SCREENING, BENEFICIARY_MAMMOGRAM_SCREENING].includes(
                              type?.name,
                            ) ? (
                              <Button
                                color="success"
                                disabled={form.isFetching || isReviewModeTransition}
                                onClick={this.onNext}
                              >
                                Next
                              </Button>
                            ) : (
                              <Button
                                color="success"
                                disabled={!isChanged || form.isFetching}
                                onClick={this.onValidateAndSave}
                              >
                                Submit
                              </Button>
                            )}
                          </>
                        )}
                      </>
                    )}
                  </>
                )}
              </>
            ))
          }
        >
          <Action
            shouldPerform={() =>
              isSurveyReady &&
              type?.name === IN_HOME &&
              [IN_HOME, IN_HOME_CARE].includes(type?.name) &&
              shouldAddNeedsToServicePlan
            }
            action={() => {
              this.onCreateServicePlan();
            }}
          />
          <Action
            isMultiple
            params={{ isReviewMode }}
            shouldPerform={(prevParams) => !isReviewMode && prevParams.isReviewMode && hasErrors}
            action={() => {
              this.scrollToFirstSurveyErrorQuestion();
              // 滚动到第一个调查错误问题
            }}
          />
          {/* form data list render  */}
          <Action
            isMultiple
            params={{ step }}
            shouldPerform={(prevParams) => step !== prevParams.step && step > 0}
            action={(prevParams) => {
              this.modalRef.current?.scrollTop(0) || noop();

              const indexes = this.getInvalidSurveyPageIndexes();

              if (hasErrors && indexes && indexes.includes(step - 1)) {
                this.scrollToFirstSurveyErrorQuestion();
              }

              if (prevParams.step === 0) {
                this.updateSurvey();
                this.loadManagement();
              }
            }}
          />
          <Action
            isMultiple
            params={{
              assessmentId,
              isFetching: form.isFetching,
            }}
            shouldPerform={(prevParams) =>
              isSurveyReady &&
              this.isEditMode() &&
              (assessmentId !== prevParams.assessmentId || (!form.isFetching && prevParams.isFetching && hasErrors))
            }
            action={() => {
              if (shouldCancelValidation) {
                this.setState({ shouldCancelValidation: false });
              } else
                defer(1000).then(() => {
                  this.validateSurvey();
                });
            }}
          />
          <Action
            isMultiple
            params={{ typeId: type?.id }}
            shouldPerform={(prevParams) => !this.isEditMode() && isNumber(type?.id) && type?.id !== prevParams.typeId}
            action={() => {
              this.changeFormFields({
                contactId: this.authUser.id,
                dateAssigned: Date.now(),
                dateCompleted: !type?.name?.includes(COMPREHENSIVE) ? Date.now() : null,
              });

              const {
                firstName,
                lastName,
                gender,
                birthDate,
                race,
                maritalStatus,
                address,
                email,
                phone,
                cellPhone,
                currentPharmacyName,
                primaryCarePhysicianFirstName,
                primaryCarePhysicianLastName,
              } = client.details.data || {};
              this.setState({
                surveyData: {
                  "Completed by": this.authUser.fullName,
                  "Date started": DU.format(Date.now(), DATE_TIME_ZONE_FORMAT),
                  ...(type?.name?.includes(COMPREHENSIVE)
                    ? {
                        "First name": firstName,
                        "Last name": lastName,
                        Gender: gender === "Undefined" ? "Unknown" : gender,
                        "Date of Birth": birthDate,
                        Race: race,
                        "Marital status": MARITAL_STATUS_MAP[maritalStatus],
                        Street5: address?.street,
                        City: address?.city,
                        State: address?.stateName,
                        "Zip Code": address?.zip,
                        Email: email,
                        "Home phone number": phone,
                        "Cell phone number": cellPhone,
                        "First name2": primaryCarePhysicianFirstName,
                        "Last Name2": primaryCarePhysicianLastName,
                        Name: currentPharmacyName,
                      }
                    : null),
                  ...(type?.name === HOUSING && {
                    Case_manager: this.authUser.fullName,
                  }),
                  ...(type?.name === ARIZONA_SSM && {
                    "First Name": firstName,
                    "Last Name": lastName,
                    Gender: gender === "Undefined" ? "Unknown" : gender,
                    "Date of Birth": birthDate,
                  }),
                },
              });
            }}
          />
          <Action
            isMultiple
            params={{ typeId: type?.id }}
            shouldPerform={(prevParams) => this.isEditMode() && isNumber(type?.id) && type?.id !== prevParams.typeId}
            action={() => {
              if (!survey.isFetching) {
                this.updateSurvey().then(() => {
                  if (type?.name?.includes(COMPREHENSIVE)) {
                    const { step } = restoreState(this.assessmentId);

                    if (step > 1) {
                      this.setState({ step });
                      defer().then(() => this.setSurveyCurrentPageNo(step - 1));
                    }
                  }
                });
              }

              if (!(type?.name?.includes(COMPREHENSIVE) || management.isFetching)) {
                this.loadManagement();
              }
            }}
          />
          {/*/!*大于0*!/ 且*/}
          {/*step 的值不等于 2 或者 type 对象的 name 属性包含变量 COMPREHENSIVE 的值*/}
          {step > 0 &&
            !(step === 2 && !type?.name?.includes(COMPREHENSIVE)) &&
            !(
              [
                BENEFICIARY_COLORECTAL_CANCER_SCREENING,
                BENEFICIARY_MAMMOGRAM_SCREENING,
                BENEFICIARY_SCREENING_FOR_TYPE_II_DIABETES,
                BENEFICIARY_SCREENING_DIABETIC_FOOT_AND_EYE_EXAM,
              ].includes(type?.name) && step === 3
            ) && (
              <div className="AssessmentForm">
                {surveyModel && hasTabs && (
                  <div className="AssessmentForm-TabsWrapper">
                    <Tabs
                      className="AssessmentForm-Tabs"
                      containerClassName="AssessmentForm-TabsContainer"
                      items={map(COMPREHENSIVE_TABS, (title, i) => {
                        const isActive = i === step - 1;
                        const hasIndicator = invalidPageIndexes && invalidPageIndexes.includes(i);

                        return {
                          title,
                          isActive,
                          hasIndicator,
                          indicatorIconClassName: "AssessmentForm-TabIndicatorIcon",
                          className: isActive && hasIndicator ? "ErrorIndicator" : "",
                        };
                      })}
                      onChange={this.onChangeTab}
                    />
                    <Dropdown
                      value={step - 1}
                      items={map(COMPREHENSIVE_TABS, (title, i) => {
                        const isActive = i === step - 1;
                        const hasError = invalidPageIndexes && invalidPageIndexes.includes(i);

                        return {
                          isActive,
                          value: i,
                          text: title,
                          hasIndicator: hasError,
                          onClick: () => this.onChangeTab(i),
                          className: isActive && hasError ? "ErrorIndicator" : "",
                          indicatorIconClassName: "AssessmentForm-ItemIndicatorIcon",
                        };
                      })}
                      toggleText={(COMPREHENSIVE_TABS && COMPREHENSIVE_TABS[step - 1]) || "Guide"}
                      className="AssessmentForm-Dropdown Dropdown_theme_blue"
                    />
                  </div>
                )}

                {isFetching && ![CARE_MGMT].includes(type?.name) ? (
                  <div className="AssessmentForm-Loading">
                    <Loader />
                  </div>
                ) : (
                  <>
                    {type && this.canRenderSurvey() && (
                      <div>
                        {isFetching && <Loader isCentered hasBackdrop />}
                        {!type?.name?.includes(COMPREHENSIVE) && (
                          <div className="margin-top-20 padding-left-40 padding-right-40">
                            {(() => {
                              if (
                                [
                                  HMIS_ADULT_CHILD_INTAKE,
                                  HMIS_ADULT_CHILD_REASESSMENT,
                                  HMIS_ADULT_CHILD_REASESSMENT_EXIT,
                                ].includes(type?.name)
                              )
                                return;

                              if (type?.name === ARIZONA_SSM)
                                return (
                                  <>
                                    <Row form>
                                      <Col md={statusName === COMPLETED ? 6 : 6} xs={statusName === COMPLETED ? 6 : 12}>
                                        <DateField
                                          hasTimeSelect
                                          label="Assessment Date*"
                                          name="dateAssigned"
                                          className="AssessmentForm-Field"
                                          value={dateAssigned}
                                          maxDate={Date.now()}
                                          maxTime={
                                            DU.lt(dateAssigned, Date.now(), "day")
                                              ? DU.endOf(Date.now(), "day").getTime()
                                              : Date.now()
                                          }
                                          timeFormat="hh:mm aa"
                                          dateFormat="MM/dd/yyyy hh:mm a"
                                          isDisabled={this.isEditMode()}
                                          onChange={this.onChangeFormDateField}
                                        />
                                      </Col>
                                      {statusName === COMPLETED && (
                                        <Col xs={6}>
                                          <DateField
                                            isDisabled
                                            hasTimeSelect
                                            label="Completed date*"
                                            name="dateCompleted"
                                            className="AssessmentForm-Field"
                                            value={dateCompleted}
                                            timeFormat="hh:mm aa"
                                            dateFormat="MM/dd/yyyy hh:mm a"
                                            onChange={this.onChangeFormDateField}
                                          />
                                        </Col>
                                      )}
                                    </Row>
                                  </>
                                );

                              if (type?.name === HOUSING)
                                return (
                                  <Row form>
                                    <Col md={statusName === COMPLETED ? 6 : 4} xs={statusName === COMPLETED ? 6 : 12}>
                                      <DateField
                                        hasTimeSelect
                                        label="Assessment date*"
                                        name="dateAssigned"
                                        className="AssessmentForm-Field"
                                        value={dateAssigned}
                                        maxDate={Date.now()}
                                        maxTime={
                                          DU.lt(dateAssigned, Date.now(), "day")
                                            ? DU.endOf(Date.now(), "day").getTime()
                                            : Date.now()
                                        }
                                        timeFormat="hh:mm aa"
                                        dateFormat="MM/dd/yyyy hh:mm a"
                                        isDisabled={this.isEditMode() && !isCopying}
                                        onChange={this.onChangeFormDateField}
                                      />
                                    </Col>
                                    {statusName === COMPLETED && (
                                      <Col md={6}>
                                        <DateField
                                          hasTimeSelect
                                          label="Date Completed"
                                          name="dateCompleted"
                                          className="AssessmentForm-Field"
                                          value={dateCompleted}
                                          timeFormat="hh:mm aa"
                                          dateFormat="MM/dd/yyyy hh:mm a"
                                          isDisabled
                                        />
                                      </Col>
                                    )}
                                  </Row>
                                );
                              //  header
                              return (
                                <Row form>
                                  <Col md={6}>
                                    <DateField
                                      hasTimeSelect
                                      label="Date Completed*"
                                      name="dateAssigned"
                                      className="AssessmentForm-Field"
                                      value={dateAssigned}
                                      maxDate={Date.now()}
                                      maxTime={
                                        DU.lt(dateAssigned, Date.now(), "day")
                                          ? DU.endOf(Date.now(), "day").getTime()
                                          : Date.now()
                                      }
                                      timeFormat="hh:mm aa"
                                      dateFormat="MM/dd/yyyy hh:mm a"
                                      onChange={this.onChangeFormDateField}
                                    />
                                  </Col>
                                  <Col md={6}>
                                    <TextField
                                      isDisabled
                                      type="text"
                                      label="Completed By*"
                                      className="AssessmentForm-Field"
                                      value={
                                        !isCopying && this.isEditMode()
                                          ? details.data.contactName
                                          : this.authUser.fullName
                                      }
                                    />
                                  </Col>
                                </Row>
                              );
                            })()}
                          </div>
                        )}
                        <div style={{ display: isReviewMode ? "none" : "block" }}>
                          {!isReviewMode && (
                            <SurveyUI
                              model={surveyModel}
                              showNavigationButtons="none"
                              onValueChanged={this.onSurveyValueChanged}
                              onAfterRenderSurvey={this.onAfterRenderSurvey}
                              onAfterRenderPage={this.onAfterRenderSurveyPage}
                              onAfterRenderPanel={this.onAfterRenderSurveyPanel}
                              // onAfterRenderQuestion={this.onAfterRenderSurveyQuestion}
                            />
                          )}
                        </div>
                        <div style={{ display: isReviewMode ? "block" : "none" }}>
                          {isReviewMode && (
                            <SurveyUI
                              model={surveyReviewModel}
                              showNavigationButtons="none"
                              onAfterRenderSurvey={this.onAfterRenderSurvey}
                              onAfterRenderPage={this.onAfterRenderSurveyPage}
                              onAfterRenderPanel={this.onAfterRenderSurveyPanel}
                              // onAfterRenderQuestion={this.onAfterRenderSurveyQuestion}
                            />
                          )}
                        </div>
                        {![
                          HOUSING,
                          CARE_MGMT,
                          ARIZONA_SSM,
                          COMPREHENSIVE,
                          NOR_CAL_COMPREHENSIVE,
                          HMIS_ADULT_CHILD_INTAKE,
                          HMIS_ADULT_CHILD_REASESSMENT,
                          HMIS_ADULT_CHILD_REASESSMENT_EXIT,
                          // add screening
                          BENEFICIARY_COLORECTAL_CANCER_SCREENING,
                          BENEFICIARY_MAMMOGRAM_SCREENING,
                          BENEFICIARY_SCREENING_FOR_TYPE_II_DIABETES,
                          BENEFICIARY_SCREENING_DIABETIC_FOOT_AND_EYE_EXAM,
                          FAST,
                          SHORT,
                          SCREEN,
                          REVISED,
                        ].includes(type?.name) && (
                          <div className="padding-left-40 padding-right-40">
                            <TextField
                              type="textarea"
                              label="Comment"
                              name="comment"
                              value={form.fields.comment}
                              className="AssessmentForm-Field AssessmentForm-CommentField"
                              onChange={this.onChangeFormField}
                            />
                          </div>
                        )}
                      </div>
                    )}
                  </>
                )}
              </div>
            )}
          {step === 1 && type?.name === CARE_MGMT && this.isRiskIdentified() && (
            <div className="AssessmentEditor-Warning">
              By clicking “Submit” button, the system will generate “Assessment risk identified” event. The alerts will
              be sent to care team members.
            </div>
          )}
          {step === 2 && type && [GAD7, PHQ9].includes(type?.name) && (
            <div className="AssessmentScoring">
              {form.isFetching && <Loader hasBackdrop />}
              {management.isFetching ? (
                <div className="AssessmentScoring-Loading">
                  <Loader />
                </div>
              ) : (
                <>
                  <div className="AssessmentEditor-Section">
                    <div className="AssessmentEditor-SectionTitle">{type?.shortTitle} Scoring</div>
                    {scoringAlert && (
                      <Alert
                        className="ScoringAlert"
                        type={scoringAlert.type}
                        title={isNumber(score.value) ? `${score.value} Points` : ""}
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
                            dataField: "severityShort",
                            text: `${type?.name === GAD7 ? "Symptom" : "Depression"} Severity`,
                            headerStyle: {
                              width: "200px",
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
                  {[GAD7, PHQ9].includes(type?.name) && this.isRiskIdentified() && (
                    <div className="AssessmentEditor-Warning">
                      By clicking “Submit” button, the system will generate “Assessment risk identified” event. The
                      alerts will be sent to care team members.
                    </div>
                  )}
                </>
              )}
            </div>
          )}
          {/*1228new*/}
          {step === 2 && type && [FAST].includes(type?.name) && (
            <div className="AssessmentScoring">
              {form.isFetching && <Loader hasBackdrop />}
              {management.isFetching ? (
                <div className="AssessmentScoring-Loading">
                  <Loader />
                </div>
              ) : (
                <>
                  {
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
                                    const model = this.cloneSurveyModel();
                                    const modelData = model.data;
                                    const isActive = modelData[item.QId] === "Yes";
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
                      {/*<div></div>*/}
                      <div className="padding-left-10 padding-right-10">
                        <TextField
                          type="textarea"
                          label="Comments/Notes:"
                          name="comment"
                          value={form.fields.comment}
                          className="AssessmentForm-Field AssessmentForm-CommentField"
                          onChange={this.onChangeFormField}
                        />
                      </div>
                    </div>
                  }
                </>
              )}
            </div>
          )}
          {step === 2 && type && [SHORT, REVISED, SCREEN].includes(type?.name) && (
            <div className="AssessmentScoring">
              {form.isFetching && <Loader hasBackdrop />}
              {management.isFetching ? (
                <div className="AssessmentScoring-Loading">
                  <Loader />
                </div>
              ) : (
                <>
                  <div className="AssessmentEditor-Section">
                    <div className="AssessmentEditor-SectionTitle">Suggested Scoring</div>
                    {scoringAlert && (
                      <Alert
                        className="ScoringAlert"
                        type={scoringAlert.type}
                        title={isNumber(score.value) ? `${score.value} Points` : ""}
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
                </>
              )}
            </div>
          )}
          {/*       add  Colonoscopy - decision and education    BENEFICIARY_COLORECTAL_CANCER_SCREENING  */}
          {step === 2 && type && [BENEFICIARY_COLORECTAL_CANCER_SCREENING].includes(type?.name) && (
            <div className="AssessmentScoring">
              {form.isFetching && <Loader hasBackdrop />}
              {management.isFetching ? (
                <div className="AssessmentScoring-Loading">
                  <Loader />
                </div>
              ) : (
                <>
                  <div className="AssessmentEditor-Section">
                    <div className="AssessmentEditor-SectionTitle" style={{ marginTop: 0 }}>
                      Colonoscopy - decision and education
                    </div>
                    <div className="AssessmentEditor-colonoscopy-decision-education">
                      <div className="AssessmentEditor-colonoscopy-decision-education-headerTitle">
                        Advantages/benefits
                      </div>
                      <div className="AssessmentEditor-colonoscopy-decision-education-headerTitle">
                        Disadvantages/risks
                      </div>
                    </div>
                    {COLONOSCOPY_DECISION_AND_EDUCATION.map((item) => {
                      return (
                        <>
                          <div key={item.id} className="AssessmentEditor-colonoscopy-decision-education-subTitle">
                            {item.title}
                          </div>
                          <div className="AssessmentEditor-colonoscopy-decision-education-listContent">
                            <div className="AssessmentEditor-colonoscopy-decision-education-benifitsBox">
                              {item.benefits.map((benefit) => {
                                return (
                                  <div
                                    key={benefit.id}
                                    className="AssessmentEditor-colonoscopy-decision-education-tipsItem"
                                  >
                                    <div style={{ width: 16, height: 16 }}>
                                      <Benefits className="AssessmentEditor-colonoscopy-decision-education-icon" />
                                    </div>
                                    <div className="AssessmentEditor-colonoscopy-decision-education-tipsItemText">
                                      {benefit.title}
                                    </div>
                                  </div>
                                );
                              })}
                            </div>
                            <div className="AssessmentEditor-colonoscopy-decision-education-benifitsBox">
                              {item.risks.map((risk) => {
                                return (
                                  <div
                                    key={risk.id}
                                    className="AssessmentEditor-colonoscopy-decision-education-tipsItem"
                                  >
                                    <div style={{ width: 16, height: 16 }}>
                                      <Risks className="AssessmentEditor-colonoscopy-decision-education-icon" />
                                    </div>
                                    <div className="AssessmentEditor-colonoscopy-decision-education-tipsItemText">
                                      {risk.title}
                                    </div>
                                  </div>
                                );
                              })}
                            </div>
                          </div>
                        </>
                      );
                    })}
                  </div>
                </>
              )}
            </div>
          )}
          {step === 2 && type && [BENEFICIARY_MAMMOGRAM_SCREENING].includes(type?.name) && (
            <div className="AssessmentScoring">
              {form.isFetching && <Loader hasBackdrop />}
              {management.isFetching ? (
                <div className="AssessmentScoring-Loading">
                  <Loader />
                </div>
              ) : (
                <>
                  <div className="AssessmentEditor-Section">
                    <div className="AssessmentEditor-mammogram-Information">
                      Breast cancer is one of the most frequently diagnosed cancer – the U.S. Preventive Services Task
                      Force recently recommended breast cancer screening every other year for women of average risk
                      beginning at 40 (Previously the screenings began at 50 years of age. In the US 1in 8 women has a
                      chance of getting breast cancer . Black women are 40% more likely to die from breast cancer and
                      receive cancer diagnosis earlier in the life.
                    </div>

                    <div className="AssessmentEditor-mammogram-education-headerTitle">Disadvantages/risks</div>
                    {DisadvantagesRisksList.map((risk) => {
                      return (
                        <div
                          key={risk.id}
                          className="AssessmentEditor-colonoscopy-decision-education-tipsItem"
                          style={{ paddingLeft: 15 }}
                        >
                          <div style={{ width: 16, height: 16 }}>
                            <Risks className="AssessmentEditor-colonoscopy-decision-education-icon" />
                          </div>
                          <div className="AssessmentEditor-colonoscopy-decision-education-tipsItemText">
                            {risk.title}
                          </div>
                        </div>
                      );
                    })}
                  </div>
                </>
              )}
            </div>
          )}
          {step === 3 && type && [BENEFICIARY_COLORECTAL_CANCER_SCREENING].includes(type?.name) && (
            <div className="AssessmentScoring">
              {form.isFetching && <Loader hasBackdrop />}
              {management.isFetching ? (
                <div className="AssessmentScoring-Loading">
                  <Loader />
                </div>
              ) : (
                <>
                  <div className="AssessmentEditor-Section">
                    <div className="AssessmentEditor-SectionTitle">Screening results</div>
                    {screeningAlertResult && (
                      <Alert
                        className="ScoringAlert"
                        type={screeningAlertResult.type}
                        title={screeningAlertResult.severity}
                        text={screeningAlertResult.comments}
                      />
                    )}
                  </div>
                  {/*management.data 被 BENEFICIARY_COLORECTAL_CANCER_SCREENING_MANGEMENT 替换了*/}
                  {management.data && (
                    <div className="AssessmentEditor-Section AssessmentEditor-ManagementSection">
                      <div className="AssessmentEditor-SectionTitle">Management</div>
                      <Table
                        className={`${type?.name}Scale`}
                        containerClass={`${type?.name}ScaleContainer`}
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
                              }
                              return 3;
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
                </>
              )}
            </div>
          )}
          {step === 3 && type && [BENEFICIARY_MAMMOGRAM_SCREENING].includes(type?.name) && (
            <div className="AssessmentScoring">
              {form.isFetching && <Loader hasBackdrop />}
              {management.isFetching ? (
                <div className="AssessmentScoring-Loading">
                  <Loader />
                </div>
              ) : (
                <>
                  {scoringAlert && (
                    <Alert className="ScoringAlert" type={mammogramResult.type} title={mammogramResult.degree} />
                  )}
                  <div className="AssessmentEditor-mammogram-education-headerTitle">What is a mammogram?</div>
                  <div className="AssessmentEditor-mammogram-Information">
                    A mammogram is an X-ray of the breast. For many women, mammograms are the best way to find breast
                    cancer early, when it is easier to treat and before it is big enough to feel or cause symptoms.
                    Having regular mammograms can lower the risk of dying from breast cancer. At this time, a mammogram
                    is the best way to find breast cancer for most women of screening age.
                  </div>
                </>
              )}
            </div>
          )}

          {step === 0 && <AssessmentTypeForm />}
        </Modal>
        {isNoIdentifiedNeedsDialogOpen && (
          <WarningDialog
            isOpen
            title="No needs identified"
            buttons={[
              {
                text: "Ok",
                onClick: () => {
                  this.setState({
                    isNoIdentifiedNeedsDialogOpen: false,
                  });
                },
              },
            ]}
          />
        )}
        {isToServicePlanEditorOpen && (
          <AssessmentToServicePlanEditor
            isOpen
            clientId={clientId}
            data={servicePlanNeedIdentification.data}
            onClose={this.onCloseToServicePlanEditor}
            onExcludeQuestions={this.onExcludeFromServicePlanNeedIdentification}
            onSaveSuccess={this.onServicePlanSaveSuccess}
          />
        )}
        {isQuestionsExcludeSuccessDialogOpen && (
          <SuccessDialog
            isOpen
            title="The needs have been hidden"
            buttons={[
              {
                text: "Close",
                onClick: () => {
                  this.setState({
                    isQuestionsExcludeSuccessDialogOpen: false,
                  });
                },
              },
            ]}
          />
        )}
        {isServicePlanSaveSuccessDialogOpen && (
          <SuccessDialog
            isOpen
            title={
              isNewServicePlanCreated ? "The service plan created" : "The needs have been added to the service plan"
            }
            buttons={compact([
              {
                text: "Close",
                outline: true,
                onClick: () => {
                  this.setState({
                    isNewServicePlanCreated: false,
                    isServicePlanSaveSuccessDialogOpen: false,
                  });
                },
              },
              type?.name !== HOUSING && {
                text: "View Service plan",
                onClick: this.onViewServicePlan,
              },
            ])}
          />
        )}
        {this.error && <ErrorViewer isOpen error={this.error} onClose={this.onResetError} />}
      </>
    );
  }
}

export default withRouter(
  connect(mapStateToProps, mapDispatchToProps)(withDirectoryData(withAutoSave()(AssessmentEditor))),
);
