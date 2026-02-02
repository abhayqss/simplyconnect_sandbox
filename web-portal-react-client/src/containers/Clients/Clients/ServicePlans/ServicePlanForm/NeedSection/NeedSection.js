import React, { PureComponent } from "react";

import { last, map, sortBy, where } from "underscore";

import PropTypes from "prop-types";

import { connect } from "react-redux";
import { compose } from "redux";

import { Button, Col, Row } from "reactstrap";

import "./NeedSection.scss";

import withSelectOptions from "hocs/withSelectOptions";

import TextField from "components/Form/TextField/TextField";
import DateField from "components/Form/DateField/DateField";
import SelectField from "components/Form/SelectField/SelectField";

import { SERVICE_PLAN_NEED_DOMAINS } from "lib/Constants";

import { trimStartIfString } from "lib/utils/Utils";

import { ReactComponent as Delete } from "images/delete.svg";

import GoalSection from "../GoalSection/GoalSection";

const { EDUCATION_TASK } = SERVICE_PLAN_NEED_DOMAINS;

function mapStateToProps(state) {
  return {
    directory: state.directory,
  };
}

class NeedSection extends PureComponent {
  static propTypes = {
    clientId: PropTypes.number,

    index: PropTypes.number,
    error: PropTypes.string,
    fields: PropTypes.object,

    isValid: PropTypes.bool,

    onDelete: PropTypes.func,
    onChangeField: PropTypes.func,

    onAddGoal: PropTypes.func,
    onDeleteGoal: PropTypes.func,
    onChangeGoalField: PropTypes.func,
    onChangeGoalFields: PropTypes.func,
  };

  static defaultProps = {
    fields: {},

    onDelete: () => {},
    onChangeField: () => {},

    onAddGoal: () => {},
    onDeleteGoal: () => {},
    onChangeGoalField: () => {},
    onChangeGoalFields: () => {},
  };

  constructor(props) {
    super(props);

    // 自定义 priority 选项显示文本
    const optionMapper = (o) => {
      let text = o.title;
      if (o.title === "High") text = "High-3 months";
      else if (o.title === "Medium") text = "Medium-6 months";
      else if (o.title === "Low") text = "Low-12 months";
      return {
        value: o.id,
        text,
      };
    };

    this.getDomainSelectOptions = this.props.MemoizedSelectOptions(["servicePlan", "domain"], {
      value: "id",
      text: "title",
    });
    this.getPrioritySelectOptions = this.props.MemoizedSelectOptions(["servicePlan", "priority"], optionMapper);
  }

  onChangeField = (name, value, onCancel) => {
    this.changeField(name, trimStartIfString(value), onCancel);
  };

  onChangeDateField = (name, value) => {
    this.changeField(name, value ? new Date(value).getTime() : null);
  };

  onDelete = () => {
    this.props.onDelete(this.props.index);
  };

  onAddGoal = () => {
    const { index, fields, onAddGoal: cb } = this.props;

    cb(fields.goals.size, index);
  };

  onChangeGoalField = (index, name, value) => {
    this.props.onChangeGoalField(index, this.props.index, name, value);
  };

  onChangeGoalFields = (index, changes) => {
    this.props.onChangeGoalFields(index, this.props.index, changes);
  };

  onDeleteGoal = (index) => {
    this.props.onDeleteGoal(index, this.props.index);
  };

  getDirectory() {
    const {
      servicePlan: { domain, priority, program },
    } = this.props.directory;

    const path = ["list", "dataSource", "data"];

    return {
      domains: domain.getIn(path) || [],
      priorities: priority.getIn(path) || [],
      programTypes: program.type.getIn(path) || [],
      programSubTypes: program.subtype.getIn(path) || [],
    };
  }

  changeField(name, value, onCancel) {
    const { index, onChangeField: cb } = this.props;

    cb(index, last(name.split(":")), value, onCancel);
  }

  render() {
    const { index, fields, clientId } = this.props;

    const { programTypes, programSubTypes } = this.getDirectory();

    const { domainId, programTypeId } = fields;

    const programTypeFieldOpts = map(sortBy(where(programTypes, { domainId }), "title"), (o) => ({
      name: o.name,
      text: o.title,
      value: o.id,
    }));

    const programSubTypeFieldOpts = map(sortBy(where(programSubTypes, { programTypeId }), "title"), (o) => ({
      name: o.name,
      text: `${o.title}, ${o.zcode}`,
      value: o.id,
    }));

    return (
      <div id={`service-plan-need-${index + 1}`} className="NeedSection">
        <div className="NeedSection-DecoratedLeftBorder">
          <span className="NeedSection-BigCircle" />
          <span className="NeedSection-SmallCircle" />
        </div>
        <div className="NeedSection-Header">
          <div className="NeedSection-Title">Need / Opportunity #{index + 1}</div>
          <Delete className="NeedSection-DeleteBtn" onClick={this.onDelete} />
        </div>
        {fields.domainName === EDUCATION_TASK ? (
          <>
            <Row>
              <Col md={6}>
                <SelectField
                  isMultiple={false}
                  hasError={fields.domainIdHasError}
                  label="Domain*"
                  name={index + ":domainId"}
                  value={domainId}
                  options={this.getDomainSelectOptions()}
                  errorText={fields.domainIdErrorText}
                  className="NeedSection-SelectField"
                  onChange={this.onChangeField}
                />
              </Col>
              <Col md={6}>
                <SelectField
                  isMultiple={false}
                  hasError={fields.programTypeIdHasError}
                  label="Program Type"
                  name={index + ":programTypeId"}
                  value={fields.programTypeId}
                  options={programTypeFieldOpts}
                  isDisabled={!programTypeFieldOpts.length}
                  errorText={fields.programTypeIdErrorText}
                  placeholder={programTypeFieldOpts.length ? "Select" : "Not applicable"}
                  className="NeedSection-SelectField"
                  onChange={this.onChangeField}
                />
              </Col>
            </Row>
            <Row>
              <Col md={6}>
                <SelectField
                  isMultiple={false}
                  hasError={fields.programSubTypeIdHasError}
                  label="Program Sub Type"
                  name={index + ":programSubTypeId"}
                  value={fields.programSubTypeId}
                  options={programSubTypeFieldOpts}
                  isDisabled={!programTypeFieldOpts.length}
                  errorText={fields.programSubTypeIdErrorText}
                  placeholder={programTypeFieldOpts.length ? "Select" : "Not applicable"}
                  className="NeedSection-SelectField"
                  onChange={this.onChangeField}
                />
              </Col>
              <Col md={6}>
                <SelectField
                  isMultiple={false}
                  hasError={fields.priorityIdHasError}
                  label="Priority*"
                  name={index + ":priorityId"}
                  value={fields.priorityId}
                  options={this.getPrioritySelectOptions()}
                  errorText={fields.priorityIdErrorText}
                  className="NeedSection-SelectField"
                  onChange={this.onChangeField}
                />
              </Col>
            </Row>
            <Row>
              <Col md={12}>
                <TextField
                  type="textarea"
                  name={index + ":activationOrEducationTask"}
                  value={fields.activationOrEducationTask}
                  label="Activation or Education Task*"
                  className="NeedSection-TextField"
                  maxLength={20000}
                  hasError={fields.activationOrEducationTaskHasError}
                  errorText={fields.activationOrEducationTaskErrorText}
                  onChange={this.onChangeField}
                />
              </Col>
            </Row>
            <Row>
              <Col md={3}>
                <DateField
                  name={index + ":targetCompletionDate"}
                  value={fields.targetCompletionDate}
                  label="Target Completion Date*"
                  className="NeedSection-TextField"
                  timeFormat="hh:mm a"
                  dateFormat="MM/dd/yyyy hh:mm a"
                  hasTimeSelect={true}
                  hasError={fields.targetCompletionDateHasError}
                  errorText={fields.targetCompletionDateErrorText}
                  onChange={this.onChangeDateField}
                />
              </Col>
              <Col md={3}>
                <DateField
                  type="date"
                  name={index + ":completionDate"}
                  timeFormat="hh:mm a"
                  dateFormat="MM/dd/yyyy hh:mm a"
                  value={fields.completionDate}
                  label="Completion Date"
                  className="NeedSection-TextField"
                  hasTimeSelect={true}
                  hasError={fields.completionDateHasError}
                  errorText={fields.completionDateErrorText}
                  onChange={this.onChangeDateField}
                />
              </Col>
            </Row>
          </>
        ) : (
          <>
            <Row>
              <Col md={6}>
                <SelectField
                  isMultiple={false}
                  hasError={fields.domainIdHasError}
                  name={index + ":domainId"}
                  label="Domain*"
                  value={fields.domainId}
                  options={this.getDomainSelectOptions()}
                  errorText={fields.domainIdErrorText}
                  className="NeedSection-SelectField"
                  onChange={this.onChangeField}
                />
              </Col>
              <Col md={6}>
                <SelectField
                  isMultiple={false}
                  hasError={fields.programTypeIdHasError}
                  label="Program Type"
                  name={index + ":programTypeId"}
                  value={fields.programTypeId}
                  options={programTypeFieldOpts}
                  isDisabled={!programTypeFieldOpts.length}
                  errorText={fields.programTypeIdErrorText}
                  placeholder={programTypeFieldOpts.length > 0 ? "Select" : "Not applicable"}
                  className="NeedSection-SelectField"
                  onChange={this.onChangeField}
                />
              </Col>
            </Row>
            <Row>
              <Col md={6}>
                <SelectField
                  isMultiple={false}
                  hasError={fields.programSubTypeIdHasError}
                  label="Program Sub Type"
                  name={index + ":programSubTypeId"}
                  value={fields.programSubTypeId}
                  options={programSubTypeFieldOpts}
                  isDisabled={!programTypeFieldOpts.length}
                  errorText={fields.programSubTypeIdErrorText}
                  placeholder={programTypeFieldOpts.length > 0 ? "Select" : "Not applicable"}
                  className="NeedSection-SelectField"
                  onChange={this.onChangeField}
                />
              </Col>
              <Col md={6}>
                <SelectField
                  name={index + ":priorityId"}
                  value={fields.priorityId}
                  options={this.getPrioritySelectOptions()}
                  label="Priority*"
                  className="NeedSection-SelectField"
                  isMultiple={false}
                  hasError={fields.priorityIdHasError}
                  errorText={fields.priorityIdErrorText}
                  onChange={this.onChangeField}
                />
              </Col>
            </Row>
            <Row>
              <Col md={12}>
                <TextField
                  type="textarea"
                  name={index + ":needOpportunity"}
                  value={fields.needOpportunity}
                  label="Need / Opportunity*"
                  className="NeedSection-TextAreaField"
                  maxLength={20000}
                  hasError={fields.needOpportunityHasError}
                  errorText={fields.needOpportunityErrorText}
                  onChange={this.onChangeField}
                />
              </Col>
            </Row>
            <Row>
              <Col md={12}>
                <TextField
                  type="textarea"
                  name={index + ":proficiencyGraduationCriteria"}
                  value={fields.proficiencyGraduationCriteria}
                  label="Proficiency / Graduation Criteria"
                  className="NeedSection-TextAreaField"
                  hasError={fields.proficiencyGraduationCriteriaHasError}
                  errorText={fields.proficiencyGraduationCriteriaErrorText}
                  onChange={this.onChangeField}
                />
              </Col>
            </Row>
            <div className="NeedSection-GoalHeader">
              <span className="NeedSection-BigCircle" />
              <div className="NeedSection-GoalTitle">Goals</div>
              <Button color="success" className="NeedSection-AddGoalBtn" onClick={this.onAddGoal}>
                Add a Goal
              </Button>
            </div>
            {fields.goals.map((o, i) => (
              <GoalSection
                key={`goal-${index}-${i}`}
                index={i}
                needIndex={index}
                clientId={clientId}
                domainId={domainId}
                programSubTypeId={fields.programSubTypeId}
                error={o.error}
                fields={o.fields}
                isValid={o.isValid}
                onDelete={this.onDeleteGoal}
                onChangeField={this.onChangeGoalField}
                onChangeFields={this.onChangeGoalFields}
              />
            ))}
          </>
        )}
      </div>
    );
  }
}

export default compose(connect(mapStateToProps), withSelectOptions)(NeedSection);
