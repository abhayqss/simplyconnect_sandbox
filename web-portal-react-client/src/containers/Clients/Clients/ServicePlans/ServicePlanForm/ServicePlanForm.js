import React, { PureComponent } from "react";

import { all, each, filter, findWhere, first, get, isEqual, isNumber, map, noop, omit, pick, where } from "underscore";

import $ from "jquery";
import PropTypes from "prop-types";

import { connect } from "react-redux";
import { bindActionCreators } from "redux";

import { withRouter } from "react-router-dom";

import { Button, Col, Form, Row } from "reactstrap";

import { Action, Loader, Scrollable } from "components";

import { CheckboxField, DateField, RadioGroupField, TextField } from "components/Form";

import { ConfirmDialog } from "components/dialogs";

import { withAutoSave, withDirectoryData } from "hocs";

import ServicePlan from "entities/ServicePlan";

import LoadServiceStatusesAction from "actions/directory/LoadServiceStatusesAction";
import LoadServicePlanDomainsAction from "actions/directory/LoadServicePlanDomainsAction";
import LoadServicePlanPrioritiesAction from "actions/directory/LoadServicePlanPrioritiesAction";
import LoadServicePlanProgramTypesAction from "actions/directory/LoadServicePlanProgramTypesAction";
import LoadServicePlanProgramSubTypesAction from "actions/directory/LoadServicePlanProgramSubTypesAction";
import LoadServiceControlRequestStatusesAction from "actions/directory/LoadServiceControlRequestStatusesAction";

import * as servicePlanFormActions from "redux/client/servicePlan/form/servicePlanFormActions";

import { hyphenate, interpolate } from "lib/utils/Utils";

import { setProperty } from "lib/utils/ObjectUtils";

import { Response } from "lib/utils/AjaxUtils";

import { SERVICE_PLAN_NEED_DOMAINS } from "lib/Constants";

import { ReactComponent as Warning } from "images/alert-yellow.svg";

import "./ServicePlanForm.scss";

import NeedSection from "./NeedSection/NeedSection";
import getServicePlanScoring from "../getServicePlanScoring";
import ServicePlanScoring from "../ServicePlanScoring/ServicePlanScoring";

const { EDUCATION_TASK } = SERVICE_PLAN_NEED_DOMAINS;

const BUTTONS_SECTION_HEIGHT = 70;

const CSS_SELECTORS = {
  FORM: ".ServicePlanForm",
  NEED: ".ServicePlanForm .NeedSection:eq($0)",
  GOAL: ".ServicePlanForm .NeedSection:eq($0) .GoalSection:eq($1)",
  ERRORS: ".ServicePlanForm .form-control.is-invalid",
};

const YES_NO_OPTIONS = [
  { value: true, label: "Yes" },
  { value: false, label: "No" },
];

function getNeedElement(index) {
  return $(interpolate(CSS_SELECTORS.NEED, index));
}

function getGoalElement(index, needIndex) {
  return $(interpolate(CSS_SELECTORS.GOAL, needIndex, index));
}

function getErrorFieldElements() {
  return $(CSS_SELECTORS.ERRORS);
}

function mapStateToProps(state) {
  const { can, form } = state.client.servicePlan;

  const canReviewByClinician = can.reviewByClinician.value;

  return {
    error: form.error,
    fields: form.fields,
    isValid: form.isValid,
    isFetching: form.isFetching,

    canReviewByClinician,

    auth: state.auth,
    client: state.client,
    directory: state.directory,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: bindActionCreators(servicePlanFormActions, dispatch),
  };
}

class ServicePlanForm extends PureComponent {
  static propTypes = {
    isLoading: PropTypes.bool,

    planId: PropTypes.number,
    clientId: PropTypes.number,

    autoSaveAdapter: PropTypes.shape({
      init: PropTypes.func,
    }),

    onCancel: PropTypes.func,
    onCurrentUnit: PropTypes.func,
    onSubmitSuccess: PropTypes.func,
  };

  static defaultProps = {
    isLoading: false,
    onCancel: noop(),
    onCurrentUnit: noop(),
    onSubmitSuccess: noop(),
  };

  scrollableRef = React.createRef();

  state = {
    unit: 0,
    minHeight: null,

    candidate: null,

    //autosave feature: we cant manage difference in data if we delete existing need
    hasDeletedExistingNeeds: false,

    isDeleteNeedConfirmDialogOpen: false,
    isDeleteGoalConfirmDialogOpen: false,
    isChangeDomainConfirmDialogOpen: false,
  };
  __initialData;

  constructor(props) {
    super(props);

    if (this.props.autoSaveAdapter && this.isEditMode) {
      this.props.autoSaveAdapter.init({
        onSave: () => this.onAutoSave(),
      });
    }
  }

  get actions() {
    return this.props.actions;
  }

  get authUser() {
    return this.props.auth.login.user.data;
  }

  get client() {
    return this.props.client.details.data;
  }

  get isEditMode() {
    return isNumber(this.props.planId);
  }

  componentDidMount() {
    if (!this.isEditMode) {
      this.actions.changeFields(
        {
          dateCreated: Date.now(),
          createdBy: this.authUser.fullName,
          clientHasAdvancedDirectiveOnFile: this.client?.hasAdvancedDirectiveOnFile,
        },
        true,
      );
    }
  }

  componentDidUpdate() {
    if (this.isEditMode && !this.__initialData && !!this.props.fields.id) this.__initialData = this.props.fields.toJS();
  }

  getAutoSaveData() {
    const data = this.getData(true);

    Object.keys(this.props.error).map((key) => {
      const path = key.split(".");
      const keyToItem = first(path, path.length - 2)
        .join(".")
        .replaceAll(".fields", "");
      const item = get(data, keyToItem);

      if (!item || !item.id) return setProperty(data, keyToItem, undefined);

      setProperty(data, key.replaceAll(".fields", ""), get(this.__initialData, key));
    });

    return {
      ...data,
      isAutoSave: true,
      needs: map(filter(data.needs, Boolean), (n) => ({
        ...n,
        goals: filter(n.goals, Boolean),
      })),
    };
  }

  onAutoSave = () => {
    if (this.isEditMode && !this.state.hasDeletedExistingNeeds) {
      this.validate().then((success) => {
        if (success) {
          this.actions
            .submit(this.getData(true), { clientId: this.props.clientId })
            .then(Response(this.props.onSubmitSuccess));
        } else {
          this.actions
            .submit(this.getAutoSaveData(), { clientId: this.props.clientId })
            .then(Response(this.props.onSubmitSuccess));
        }
      });
    }
  };

  onPrevUnit = () => {
    this.setState(
      (s) => ({ unit: s.unit - 1 }),
      () => {
        this.scrollTop(0);

        this.props.onCurrentUnit(this.state.unit);
      },
    );
  };

  onNextUnit = () => {
    this.validate().then((success) => {
      if (success) {
        this.setState(
          (s) => ({ unit: s.unit + 1 }),
          () => {
            this.scrollTop(0);

            this.props.onCurrentUnit(this.state.unit);
          },
        );
      } else this.scroll(first(getErrorFieldElements()));
    });
  };

  onSubmit = (e) => {
    e.preventDefault();

    this.validate().then((success) => {
      if (success) {
        this.submit().then(Response(this.props.onSubmitSuccess));
      } else this.scroll(first(getErrorFieldElements()));
    });
  };

  onChangeField = (name, value) => {
    this.changeField(name, value).then(() => {
      if (!this.props.isValid) this.validate();
    });
  };

  onChangeDateField = (name, value) => {
    //todo refactor
    this.changeField(name, value ? new Date(value).getTime() : null).then(() => {
      if (!this.props.isValid) this.validate();
    });
  };

  onOpenDatePicker = () => {
    this.setState({
      minHeight: 500 + BUTTONS_SECTION_HEIGHT,
    });
  };

  onCloseDatePicker = () => {
    this.setState({ minHeight: null });
  };

  onChangeScore = (...args) => {
    this.actions.changeScore(...args);
  };

  onAddNeed = () => {
    const index = this.props.fields.needs.size;

    this.addNeed(index).then(() => {
      this.scroll(getNeedElement(index));
    });
  };

  onChangeNeedField = (index, name, value, onCancel) => {
    if (name === "domainId") {
      const { domains } = this.getDirectoryData();

      const next = findWhere(domains, { id: value });

      const need = this.props.fields.needs.get(index);

      const current = findWhere(domains, { id: need.fields.domainId });

      if (
        current &&
        next &&
        !this.isEmptyNeed(index) &&
        (current.name === EDUCATION_TASK || next.name === EDUCATION_TASK)
      ) {
        onCancel();

        this.setState({
          candidate: { index, domain: next },
          isChangeDomainConfirmDialogOpen: true,
        });
      } else
        this.changeNeedFields(index, {
          domainId: value,
          domainName: next && next.name,
        }).then(() => {
          this.onNeedFieldChanged(index, name, value);
        });
    } else
      this.changeNeedField(index, name, value).then(() => {
        this.onNeedFieldChanged(index, name, value);
      });
  };

  onNeedFieldChanged = (index, name, value) => {
    if (name === "domainId") {
      const programTypes = where(this.getDirectoryData().programTypes, { domainId: value });

      if (programTypes.length === 1) {
        const programTypeId = first(programTypes).id;

        this.changeNeedField(index, "programTypeId", programTypeId).then(() => {
          this.onNeedFieldChanged(index, "programTypeId", programTypeId);
        });
      } else
        this.changeNeedFields(index, {
          programTypeId: null,
          programSubTypeId: null,
        });
    }

    if (name === "programTypeId") {
      const programSubTypes = where(this.getDirectoryData().programSubTypes, { programTypeId: value });

      if (programSubTypes.length === 1) {
        this.changeNeedField(index, "programSubTypeId", first(programSubTypes).id);
      } else this.changeNeedField(index, "programSubTypeId", null);
    }

    if (!this.props.isValid) this.validate();
  };

  onDeleteNeed = (index) => {
    if (this.isEmptyNeed(index)) {
      this.deleteNeed(index);
    } else {
      const need = this.props.fields.needs.get(index);

      const domain = findWhere(this.getDirectoryData().domains, { id: need.fields.domainId }) || {};

      this.setState({
        candidate: { domain, index },
        isDeleteNeedConfirmDialogOpen: true,
      });
    }
  };

  onAddGoal = (index, needIndex) => {
    this.addGoal(index, needIndex).then(() => {
      this.scroll(getGoalElement(index, needIndex));
    });
  };

  onChangeGoalField = (...args) => {
    this.changeGoalField(...args).then(() => {
      if (!this.props.isValid) this.validate();
    });
  };

  onChangeGoalFields = (...args) => {
    this.changeGoalFields(...args).then(() => {
      if (!this.props.isValid) this.validate();
    });
  };

  onDeleteGoal = (index, needIndex) => {
    if (this.isEmptyGoal(index, needIndex)) {
      this.deleteGoal(index, needIndex);
    } else
      this.setState({
        candidate: { index, needIndex },
        isDeleteGoalConfirmDialogOpen: true,
      });
  };

  onCloseAllConfirmDialogs = () => {
    this.setState({
      candidate: null,
      isDeleteNeedConfirmDialogOpen: false,
      isDeleteGoalConfirmDialogOpen: false,
      isChangeDomainConfirmDialogOpen: false,
    });
  };

  onChangeWasReviewedWithMemberField = (name, value) => {
    this.changeField(name, value);

    if (value) this.changeField("clinicianReview.dateOfReviewWithMember", Date.now());
    else this.changeField("clinicianReview.dateOfReviewWithMember", null);
  };

  onChangeWasCopyReceivedByMemberField = (name, value) => {
    this.changeField(name, value);

    if (value) this.changeField("clinicianReview.dateOfCopyWasReceivedByMember", Date.now());
    else this.changeField("clinicianReview.dateOfCopyWasReceivedByMember", null);
  };

  scroll(...args) {
    this.scrollableRef.current?.scroll(...args) || noop();
  }

  scrollTop(duration) {
    this.scrollableRef.current?.scrollTop(duration) || noop();
  }

  changeField(name, value) {
    return this.actions.changeField(name, value);
  }

  addNeed(index) {
    return this.actions.addNeed(index);
  }

  clearNeed(index) {
    this.actions.clearNeed(index);
  }

  changeNeedField(index, name, value) {
    return this.actions.changeNeedField(index, name, value);
  }

  changeNeedFields(...args) {
    return this.actions.changeNeedFields(...args);
  }

  deleteNeed(index) {
    return this.actions.removeNeed(index);
  }

  addGoal(...args) {
    return this.actions.addGoal(...args);
  }

  changeGoalField(index, needIndex, name, value) {
    return this.actions.changeGoalField(index, needIndex, name, value);
  }

  changeGoalFields(index, needIndex, changes) {
    return this.actions.changeGoalFields(index, needIndex, changes);
  }

  deleteGoal(...args) {
    return this.actions.removeGoal(...args);
  }

  validate() {
    const { fields, canReviewByClinician } = this.props;

    return this.actions.validate(fields.toJS(), { canReviewByClinician });
  }

  submit() {
    return this.actions.submit(this.getData(), { clientId: this.props.clientId });
  }

  getData(isAutoSave = false) {
    const { fields, canReviewByClinician } = this.props;

    const filter = (v, k) => !(k.includes("HasError") || k.includes("ErrorText"));

    let data = fields.toJS();

    const domainIds = new Set();

    each(data.needs, (n) => {
      domainIds.add(n.fields.domainId);
    });

    data = {
      isAutoSave,
      ...pick(data, filter),
      clinicianReview: pick(data.clinicianReview, filter),
      needs: map(data.needs, (need) => ({
        ...pick(need.fields, filter),
        goals: map(need.fields.goals, (goal) => pick(goal.fields, filter)),
      })),
      scoring: map([...domainIds], (domainId) => ({
        domainId,
        score: (findWhere(data.scoring, { domainId }) || { score: 0 }).score,
      })),
    };

    if (canReviewByClinician) {
      data.clinicianReview = pick(data.clinicianReview, filter);
    } else delete data.clinicianReview;

    return data;
  }

  getDirectoryData() {
    return this.props.getDirectoryData({
      domains: ["servicePlan", "domain"],
      serviceStatuses: ["service", "status"],
      programTypes: ["servicePlan", "program", "type"],
      programSubTypes: ["servicePlan", "program", "subtype"],
    });
  }

  isEmptyNeed(index) {
    const excluded = ["goals", "domainId", "domainName"];

    const filter = (v, k) => k.includes("HasError") || k.includes("ErrorText") || excluded.includes(k);

    const need = this.props.fields.getIn(["needs", index]);

    return (
      isEqual(omit(need.fields.toJS(), filter), omit(need.fields.clear().toJS(), filter)) &&
      all(need.fields.goals.toJS(), (g, i) => this.isEmptyGoal(i, index))
    );
  }

  isEmptyGoal(index, needIndex) {
    const filter = (v, k) => k.includes("HasError") || k.includes("ErrorText");

    const goal = this.props.fields.getIn(["needs", needIndex, "fields", "goals", index]);

    return isEqual(omit(goal.fields.toJS(), filter), omit(goal.fields.clear().toJS(), filter));
  }

  getNeedAnchorLinks() {
    const links = [];

    const needs = map(this.props.fields.needs.toJS(), (n) => ({ ...n.fields }));

    const { domains } = this.getDirectoryData();

    each(needs, ({ domainId }, i) => {
      const domain = findWhere(domains, { id: domainId }) || { title: "Select" };

      links.push({
        group: domainId,
        href: `#service-plan-need-${i + 1}`,
        text:
          domain.title +
          (where(needs, { domainId }).length > 1 ? ` ${where(links, { group: domainId }).length + 1}` : ""),
      });
    });

    return links;
  }

  render() {
    const { fields, clientId, onCancel, isLoading, isFetching, canReviewByClinician } = this.props;

    const {
      unit,
      minHeight,

      candidate,

      isDeleteNeedConfirmDialogOpen,
      isDeleteGoalConfirmDialogOpen,
      isChangeDomainConfirmDialogOpen,
    } = this.state;

    const hasNeeds = fields.needs.size > 0;

    return (
      <>
        <Form className="ServicePlanForm" style={{ minHeight }} onSubmit={this.onSubmit}>
          <Action action={this.actions.clear} />
          <Action performingPhase="unmounting" action={this.actions.clear} />
          <Action
            isMultiple
            shouldPerform={() => canReviewByClinician && !fields.clinicianReview}
            action={() => this.changeField("clinicianReview", ServicePlan().clinicianReview)}
          />
          <LoadServiceStatusesAction />
          <LoadServicePlanDomainsAction />
          <LoadServicePlanPrioritiesAction />
          <LoadServicePlanProgramTypesAction />
          <LoadServicePlanProgramSubTypesAction />
          <LoadServiceControlRequestStatusesAction />
          {(isLoading || isFetching) && (
            <Loader
              hasBackdrop
              style={{
                position: hasNeeds ? "fixed" : "absolute",
              }}
            />
          )}
          <Scrollable ref={this.scrollableRef} style={{ flex: 1 }}>
            {unit === 0 && (
              <div className="ServicePlanForm-Section">
                <a href="#summary" className="ServicePlanForm-Anchors">
                  Summary
                </a>
                {map(this.getNeedAnchorLinks(), ({ href, text }) => (
                  <a href={href} key={hyphenate(text)} className="ServicePlanForm-Anchors">
                    {text}
                  </a>
                ))}
                <div id="summary" className="ServicePlanForm-SectionTitle">
                  Summary
                </div>
                <Row>
                  <Col md={4}>
                    <DateField
                      name="dateCreated"
                      isDisabled={this.isEditMode}
                      value={fields.dateCreated}
                      timeFormat="hh:mm a"
                      dateFormat="MM/dd/yyyy hh:mm a"
                      hasTimeSelect={true}
                      label="Date Created*"
                      className="ServicePlanForm-TextField"
                      hasError={fields.dateCreatedHasError}
                      errorText={fields.dateCreatedErrorText}
                      onFocus={this.onOpenDatePicker}
                      onChange={this.onChangeDateField}
                      onOpenPicker={this.onOpenDatePicker}
                      onClickOutside={this.onCloseDatePicker}
                    />
                  </Col>
                  <Col md={4}>
                    <TextField
                      isDisabled
                      type="text"
                      name="createdBy"
                      value={fields.createdBy}
                      label="Created by*"
                      className="ServicePlanForm-TextField"
                      hasError={fields.createdByHasError}
                      errorText={fields.createdByErrorText}
                      onChange={this.onChangeField}
                    />
                  </Col>
                  <Col md={4}>
                    <CheckboxField
                      name="isCompleted"
                      value={fields.isCompleted}
                      label="Mark service plan as completed"
                      className="ServicePlanForm-CheckboxField padding-top-35"
                      hasError={fields.isCompletedHasError}
                      errorText={fields.isCompletedErrorText}
                      onChange={this.onChangeField}
                    />
                  </Col>
                </Row>
                <Row>
                  <Col md={6}>
                    <CheckboxField
                      isDisabled
                      name="clientHasAdvancedDirectiveOnFile"
                      value={fields.clientHasAdvancedDirectiveOnFile}
                      label="Client has an advanced directive on file"
                      className="ServicePlanForm-CheckboxField"
                      onChange={this.onChangeField}
                    />
                  </Col>
                </Row>

                {canReviewByClinician && fields.clinicianReview && (
                  <>
                    <Row>
                      <Col md={6}>
                        <CheckboxField
                          name="clinicianReview.wasReviewed"
                          value={fields.clinicianReview.wasReviewed}
                          label="Reviewed by Clinician"
                          className="ServicePlanForm-CheckboxField"
                          onChange={this.onChangeField}
                        />
                      </Col>
                    </Row>
                    <Row>
                      <Col>
                        <TextField
                          type="textarea"
                          name="clinicianReview.reviewNotes"
                          value={fields.clinicianReview.reviewNotes}
                          label="Notes"
                          maxLength={256}
                          className="ServicePlanForm-TextField"
                          onChange={this.onChangeField}
                        />
                      </Col>
                    </Row>
                    <Row>
                      <Col md={6}>
                        <RadioGroupField
                          view="row"
                          name="clinicianReview.wasReviewedWithMember"
                          selected={fields.clinicianReview.wasReviewedWithMember}
                          title="Was care plan reviewed with member?*"
                          options={YES_NO_OPTIONS}
                          onChange={this.onChangeWasReviewedWithMemberField}
                          hasError={fields.clinicianReview.wasReviewedWithMemberHasError}
                          errorText={fields.clinicianReview.wasReviewedWithMemberErrorText}
                          containerClass="ServicePlanForm-RadioGroupField"
                        />
                      </Col>
                      {fields.clinicianReview.wasReviewedWithMember && (
                        <Col md={6}>
                          <DateField
                            name="clinicianReview.dateOfReviewWithMember"
                            value={fields.clinicianReview.dateOfReviewWithMember}
                            label="Date when this occurred*"
                            maxDate={Date.now()}
                            className="ServicePlanForm-TextField"
                            hasError={fields.clinicianReview.dateOfReviewWithMemberHasError}
                            errorText={fields.clinicianReview.dateOfReviewWithMemberErrorText}
                            onFocus={this.onOpenDatePicker}
                            onChange={this.onChangeDateField}
                            onOpenPicker={this.onOpenDatePicker}
                            onClickOutside={this.onCloseDatePicker}
                          />
                        </Col>
                      )}
                    </Row>
                    <Row>
                      <Col md={6}>
                        <RadioGroupField
                          view="row"
                          name="clinicianReview.wasCopyReceivedByMember"
                          selected={fields.clinicianReview.wasCopyReceivedByMember}
                          title="Did member receive a copy of the care plan?*"
                          options={YES_NO_OPTIONS}
                          onChange={this.onChangeWasCopyReceivedByMemberField}
                          hasError={fields.clinicianReview.wasCopyReceivedByMemberHasError}
                          errorText={fields.clinicianReview.wasCopyReceivedByMemberErrorText}
                          containerClass="ServicePlanForm-RadioGroupField"
                        />
                      </Col>
                      {fields.clinicianReview.wasCopyReceivedByMember && (
                        <Col md={6}>
                          <DateField
                            name="clinicianReview.dateOfCopyWasReceivedByMember"
                            value={fields.clinicianReview.dateOfCopyWasReceivedByMember}
                            label="Date copy received*"
                            maxDate={Date.now()}
                            className="ServicePlanForm-TextField"
                            hasError={fields.clinicianReview.dateOfCopyWasReceivedByMemberHasError}
                            errorText={fields.clinicianReview.dateOfCopyWasReceivedByMemberErrorText}
                            onFocus={this.onOpenDatePicker}
                            onChange={this.onChangeDateField}
                            onOpenPicker={this.onOpenDatePicker}
                            onClickOutside={this.onCloseDatePicker}
                          />
                        </Col>
                      )}
                    </Row>
                    {fields.clinicianReview.wasCopyReceivedByMember === false && (
                      <Row>
                        <Col>
                          <TextField
                            type="textarea"
                            name="clinicianReview.copyWasNotReceivedNotes"
                            value={fields.clinicianReview.copyWasNotReceivedNotes}
                            label="Notes*"
                            maxLength={256}
                            className="ServicePlanForm-TextField"
                            hasError={fields.clinicianReview.copyWasNotReceivedNotesHasError}
                            errorText={fields.clinicianReview.copyWasNotReceivedNotesErrorText}
                            onChange={this.onChangeField}
                          />
                        </Col>
                      </Row>
                    )}
                    <Row>
                      <Col>
                        <RadioGroupField
                          view="row"
                          name="clinicianReview.isClientLSSProgramParticipant"
                          selected={fields.clinicianReview.isClientLSSProgramParticipant}
                          title="Is client currently enrolled in any other LSS program?*"
                          options={YES_NO_OPTIONS}
                          onChange={this.onChangeField}
                          hasError={fields.clinicianReview.isClientLSSProgramParticipantHasError}
                          errorText={fields.clinicianReview.isClientLSSProgramParticipantErrorText}
                          containerClass="ServicePlanForm-RadioGroupField"
                        />
                      </Col>
                    </Row>
                    {fields.clinicianReview.isClientLSSProgramParticipant && (
                      <Row>
                        <Col>
                          <TextField
                            name="clinicianReview.lssPrograms"
                            value={fields.clinicianReview.lssPrograms}
                            label="Please specify programs*"
                            maxLength={256}
                            className="ServicePlanForm-TextField"
                            hasError={fields.clinicianReview.lssProgramsHasError}
                            errorText={fields.clinicianReview.lssProgramsErrorText}
                            onChange={this.onChangeField}
                          />
                        </Col>
                      </Row>
                    )}
                  </>
                )}

                <div className="ServicePlanForm-SectionHeader">
                  <div className="ServicePlanForm-SectionTitle flex-1">Needs / Opportunities</div>
                  <div className="flex-1 text-right">
                    <Button color="success" className="AddNeedBtn" onClick={this.onAddNeed}>
                      Add a Need / Opportunity
                    </Button>
                  </div>
                </div>
                {fields.needs.map((o, index) => (
                  <NeedSection
                    key={"need" + index}
                    index={index}
                    clientId={clientId}
                    error={o.error}
                    fields={o.fields}
                    isValid={o.isValid}
                    onDelete={this.onDeleteNeed} //We have to use onDeleteNeed
                    onChangeField={this.onChangeNeedField}
                    onAddGoal={this.onAddGoal}
                    onDeleteGoal={this.onDeleteGoal}
                    onChangeGoalField={this.onChangeGoalField}
                    onChangeGoalFields={this.onChangeGoalFields}
                  />
                ))}
              </div>
            )}
            {unit === 1 && (
              <ServicePlanScoring
                data={getServicePlanScoring(this.getData(), this.getDirectoryData())}
                onChangeScore={this.onChangeScore}
              />
            )}
          </Scrollable>
          <div className="ServicePlanForm-Buttons">
            {unit === 0 && (
              <Button outline color="success" disabled={isLoading || isFetching} onClick={onCancel}>
                Cancel
              </Button>
            )}
            {unit === 0 && hasNeeds && (
              <Button color="success" disabled={isLoading || isFetching} onClick={this.onNextUnit}>
                Next
              </Button>
            )}
            {unit === 1 && (
              <Button outline color="success" disabled={isLoading || isFetching} onClick={this.onPrevUnit}>
                Back
              </Button>
            )}
            {(unit === 1 || !hasNeeds) && (
              <Button color="success" disabled={isLoading || isFetching}>
                Save
              </Button>
            )}
          </div>
        </Form>
        {isDeleteNeedConfirmDialogOpen && (
          <ConfirmDialog
            isOpen
            icon={Warning}
            confirmBtnText="Delete"
            title={
              candidate?.domain.name === EDUCATION_TASK
                ? "Relevant Activation or Education Task will be deleted"
                : "Need/Opportunity with the related goals will be deleted"
            }
            onConfirm={() => {
              this.setState({ hasDeletedExistingNeeds: true });
              this.deleteNeed(candidate?.index);
              this.onCloseAllConfirmDialogs();
            }}
            onCancel={this.onCloseAllConfirmDialogs}
          />
        )}
        {isChangeDomainConfirmDialogOpen && (
          <ConfirmDialog
            isOpen
            icon={Warning}
            confirmBtnText="OK"
            title={
              candidate?.domain.name !== EDUCATION_TASK
                ? "Relevant Activation or Education Task will be deleted"
                : "Need/Opportunity with the related goals will be deleted"
            }
            onConfirm={() => {
              const { index, domain } = candidate;

              this.clearNeed(index);

              this.changeNeedFields(index, {
                domainId: domain.value,
                domainName: domain.name,
              }).then(() => {
                this.onNeedFieldChanged(index, "domainId", domain.value);
                this.onCloseAllConfirmDialogs();
              });
            }}
            onCancel={this.onCloseAllConfirmDialogs}
          />
        )}
        {isDeleteGoalConfirmDialogOpen && (
          <ConfirmDialog
            isOpen
            icon={Warning}
            confirmBtnText="Delete"
            title="Goal with the corresponding fields will be deleted"
            onConfirm={() => {
              this.deleteGoal(candidate?.index, candidate?.needIndex);
              this.onCloseAllConfirmDialogs();
            }}
            onCancel={this.onCloseAllConfirmDialogs}
          />
        )}
      </>
    );
  }
}

export default withRouter(
  connect(mapStateToProps, mapDispatchToProps)(withDirectoryData(withAutoSave()(ServicePlanForm))),
);
