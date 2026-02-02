import React, { Component } from "react";

import PropTypes from "prop-types";

import { connect } from "react-redux";
import { bindActionCreators } from "redux";

import { withRouter } from "react-router-dom";

import { Button, Col, Row } from "reactstrap";

import withDirectoryData from "hocs/withDirectoryData";

import { Tabs, Table, Modal, Loader } from "components";

import { TextField, DateField, CheckboxField, RadioGroupField } from "components/Form";

import LoadServiceStatusesAction from "actions/directory/LoadServiceStatusesAction";
import LoadServicePlanNeedDomainsAction from "actions/directory/LoadServicePlanDomainsAction";
import LoadServicePlanNeedPrioritiesAction from "actions/directory/LoadServicePlanPrioritiesAction";
import LoadServicePlanProgramTypesAction from "actions/directory/LoadServicePlanProgramTypesAction";
import LoadServicePlanProgramSubTypesAction from "actions/directory/LoadServicePlanProgramSubTypesAction";
import LoadServiceControlRequestStatusesAction from "actions/directory/LoadServiceControlRequestStatusesAction";

import * as servicePlanHistoryActions from "redux/client/servicePlan/history/servicePlanHistoryActions";
import * as servicePlanDetailsActions from "redux/client/servicePlan/details/servicePlanDetailsActions";

import { PAGINATION } from "lib/Constants";

import { isEmpty, DateUtils, isNotEmpty } from "lib/utils/Utils";

import getServicePlanScoring from "../getServicePlanScoring";
import ServicePlanScoring from "../ServicePlanScoring/ServicePlanScoring";

import "./ServicePlanViewer.scss";
import ShowCustomerServicePlan from "./ShowCustomerServicePlan";

const { format, formats } = DateUtils;

const { FIRST_PAGE } = PAGINATION;

const DATE_TIME_FORMAT = formats.longDateMediumTime12;

const YES_NO_OPTIONS = [
  { value: true, label: "Yes" },
  { value: false, label: "No" },
];

function mapStateToProps(state) {
  const { details, history } = state.client.servicePlan;

  return {
    data: details.data,
    isFetching: details.isFetching,

    history,
    directory: state.directory,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: {
      ...bindActionCreators(servicePlanDetailsActions, dispatch),
      history: bindActionCreators(servicePlanHistoryActions, dispatch),
    },
  };
}

class ServicePlanViewer extends Component {
  static propTypes = {
    isOpen: PropTypes.bool,

    planId: PropTypes.number,
    clientId: PropTypes.number,

    isPlanArchived: PropTypes.bool,

    onClose: PropTypes.func,
    onViewDetails: PropTypes.func,
  };

  static defaultProps = {
    onClose: function () {},
    onViewDetails: function () {},
    isPlanArchived: false,
  };

  state = {
    tab: 0,
    value: 0,

    haveCustomServicePlan: false,
    mentalAcuityLevel: "",
    narratives: "",
    careTeam: "",
    consentToReceiveCarePlan: "",
  };

  componentDidMount() {
    this.load();
  }

  componentDidUpdate(prevProps, prevState) {
    const { planId, isPlanArchived } = this.props;

    if (!isPlanArchived) {
      if (prevProps.planId !== planId) {
        this.load();
      }

      const { tab } = this.state;

      if (tab !== prevState.tab) {
        switch (tab) {
          case 0:
            this.load();
            break;

          case 1:
            this.refreshHistory();
            break;
        }
      }
    }
  }

  componentWillUnmount() {
    this.actions.clear();
  }

  changeHaveCustomServicePlan = (status) => {
    this.setState({
      haveCustomServicePlan: status,
    });
  };

  changeNarratives = (narratives) => {
    this.setState({
      narratives,
    });
  };
  changeConsentToReceiveCarePlan = (consentToReceiveCarePlan) => {
    this.setState({
      consentToReceiveCarePlan,
    });
  };
  changeCareTeam = (careTeam) => {
    this.setState({
      careTeam,
    });
  };

  changeMentalAcuityLevel = (mentalAcuityLevel) => {
    this.setState({
      mentalAcuityLevel: Number(mentalAcuityLevel),
    });
  };

  onClose = () => {
    this.props.onClose();

    this.setState({
      mentalAcuityLevel: "",
      narratives: "",
      consentToReceiveCarePlan: "",
      careTeam: "",
    });
  };

  onChangeTab = (tab) => {
    this.setState({ tab });
  };

  onViewDetails = (servicePlan) => {
    this.props.onViewDetails(servicePlan);
  };

  onRefreshHistory = (page) => {
    this.refreshHistory(page);
  };

  get actions() {
    return this.props.actions;
  }

  load() {
    this.actions.load(this.props.clientId, this.props.planId);
  }

  updateHistory(isReload, page) {
    const {
      planId,
      clientId,
      history: { isFetching, shouldReload, dataSource: ds },
    } = this.props;

    if (isReload || shouldReload || (!isFetching && isEmpty(ds.data))) {
      const { page: p, size } = ds.pagination;

      this.actions.history.load({
        size,
        planId,
        clientId,
        page: page || p,
      });
    }
  }

  refreshHistory(page) {
    this.updateHistory(true, page || FIRST_PAGE);
  }

  getDirectoryData() {
    return this.props.getDirectoryData({
      domains: ["servicePlan", "domain"],
      serviceStatuses: ["service", "status"],
      programTypes: ["servicePlan", "program", "type"],
      programSubTypes: ["servicePlan", "program", "subtype"],
    });
  }

  render() {
    const { tab } = this.state;

    const {
      data,
      history,

      isOpen,
      isFetching,
      isPlanArchived,
    } = this.props;

    if (data && data.custome) {
      const jsonResult = JSON.parse(data?.result || "{}");
      if (jsonResult.servicePlan) {
        //   自定义的service plan --可与原系统匹配
        data.needs = jsonResult.servicePlan.needs;
      }
    }

    return (
      <Modal
        isOpen={isOpen}
        onClose={this.onClose}
        className="ServicePlanViewer"
        title="View Service Plan"
        renderFooter={() => (
          <Button outline color="success" onClick={this.onClose}>
            Close
          </Button>
        )}
      >
        <LoadServiceStatusesAction />
        <LoadServicePlanNeedDomainsAction />
        <LoadServicePlanNeedPrioritiesAction />
        <LoadServicePlanProgramTypesAction />
        <LoadServicePlanProgramSubTypesAction />
        <LoadServiceControlRequestStatusesAction />
        {!isPlanArchived && (
          <Tabs
            items={[
              { title: "Details", isActive: tab === 0 },
              { title: "Change History", isActive: tab === 1 },
            ]}
            onChange={this.onChangeTab}
            containerClassName="ServicePlanViewer-TabsContainer"
          />
        )}
        {tab === 0 && (
          <>
            {isFetching ? (
              <Loader />
            ) : (
              <>
                {isEmpty(data) ? (
                  <h4>No Data</h4>
                ) : (
                  <>
                    <div className="ServicePlanViewer-Section">
                      {(!data?.custome || this.state.haveCustomServicePlan) && (
                        <>
                          <div className="ServicePlanViewer-SectionTitle">Summary</div>
                          <Row>
                            <Col md={6}>
                              <DateField
                                value={data.dateCreated}
                                isDisabled={true}
                                label="Date Created*"
                                className="ServicePlanViewer-TextField"
                                hasTimeSelect={true}
                              />
                            </Col>
                            <Col md={6}>
                              <TextField
                                type="text"
                                value={data.createdBy}
                                isDisabled={true}
                                label="Created by*"
                                className="ServicePlanViewer-TextField"
                              />
                            </Col>
                          </Row>

                          {this.state.haveCustomServicePlan && (
                            <Row>
                              <TextField
                                type="text"
                                value={
                                  this.state.mentalAcuityLevel === 1
                                    ? "Low"
                                    : this.state.mentalAcuityLevel === 2
                                      ? "Medium"
                                      : "High"
                                }
                                isDisabled={true}
                                label="Acuity Level"
                                className="ServicePlanViewer-TextField"
                              />
                            </Row>
                          )}

                          {this.state.haveCustomServicePlan && (
                            <Row>
                              <TextField
                                type="text"
                                value={this.state.narratives}
                                isDisabled={true}
                                label="Narratives"
                                className="ServicePlanViewer-TextField"
                              />
                              <TextField
                                type="text"
                                value={this.state.careTeam}
                                isDisabled={true}
                                label="Care Team"
                                className="ServicePlanViewer-TextField"
                              />
                              <RadioGroupField
                                view={"row"}
                                selected={this.state.consentToReceiveCarePlan}
                                isDisabled={true}
                                options={[
                                  {
                                    value: "Yes",
                                    label: "Yes",
                                  },
                                  {
                                    value: "No",
                                    label: "No",
                                  },
                                ]}
                                label="Consent to Receive Care Plan"
                                className="ServicePlanViewer-TextField"
                              />
                            </Row>
                          )}

                          {data.dateCompleted && (
                            <Row>
                              <Col md={6}>
                                <DateField
                                  value={data.dateCompleted}
                                  isDisabled={true}
                                  label="Date Completed*"
                                  className="ServicePlanViewer-TextField"
                                  hasTimeSelect={true}
                                />
                              </Col>
                            </Row>
                          )}
                          <Row>
                            <Col md={6}>
                              <CheckboxField
                                isDisabled
                                value={data.isCompleted}
                                label="Mark service plan as completed"
                                className="ServicePlanViewer-CheckboxField"
                              />
                            </Col>
                          </Row>
                          <Row>
                            <Col md={6}>
                              <CheckboxField
                                isDisabled
                                value={data.clientHasAdvancedDirectiveOnFile}
                                label="Client has an advanced directive on file"
                                className="ServicePlanViewer-CheckboxField"
                              />
                            </Col>
                          </Row>
                        </>
                      )}

                      {isNotEmpty(data.clinicianReview) && (
                        <>
                          <Row>
                            <Col md={6}>
                              <CheckboxField
                                isDisabled
                                name="clinicianReview.wasReviewed"
                                value={data.clinicianReview.wasReviewed}
                                label="Reviewed by Clinician"
                                className="ServicePlanViewer-CheckboxField"
                              />
                            </Col>
                          </Row>
                          <Row>
                            <Col>
                              <TextField
                                isDisabled
                                type="textarea"
                                name="clinicianReview.reviewNotes"
                                value={data.clinicianReview.reviewNotes}
                                label="Notes"
                                maxLength={256}
                                className="ServicePlanViewer-TextField"
                              />
                            </Col>
                          </Row>
                          <Row>
                            <Col md={6}>
                              <RadioGroupField
                                view="row"
                                isDisabled
                                name="clinicianReview.wasReviewedWithMember"
                                selected={data.clinicianReview.wasReviewedWithMember}
                                title="Was care plan reviewed with member?*"
                                options={YES_NO_OPTIONS}
                                containerClass="ServicePlanViewer-RadioGroupField"
                              />
                            </Col>
                            {data.clinicianReview.wasReviewedWithMember && (
                              <Col md={6}>
                                <DateField
                                  isDisabled
                                  name="clinicianReview.dateOfReviewWithMember"
                                  value={data.clinicianReview.dateOfReviewWithMember}
                                  label="Date when this occurred"
                                  maxDate={Date.now()}
                                  className="ServicePlanViewer-TextField"
                                />
                              </Col>
                            )}
                          </Row>
                          <Row>
                            <Col md={6}>
                              <RadioGroupField
                                view="row"
                                isDisabled
                                name="clinicianReview.wasCopyReceivedByMember"
                                selected={data.clinicianReview.wasCopyReceivedByMember}
                                title="Did member receive a copy of the care plan?*"
                                options={YES_NO_OPTIONS}
                                containerClass="ServicePlanViewer-RadioGroupField"
                              />
                            </Col>
                            {data.clinicianReview.wasCopyReceivedByMember && (
                              <Col md={6}>
                                <DateField
                                  isDisabled
                                  name="clinicianReview.dateOfCopyWasReceivedByMember"
                                  value={data.clinicianReview.dateOfCopyWasReceivedByMember}
                                  label="Date copy received"
                                  maxDate={Date.now()}
                                  className="ServicePlanViewer-TextField"
                                />
                              </Col>
                            )}
                          </Row>
                          {data.clinicianReview.wasCopyReceivedByMember === false && (
                            <Row>
                              <Col>
                                <TextField
                                  isDisabled
                                  type="textarea"
                                  name="clinicianReview.copyWasNotReceivedNotes"
                                  value={data.clinicianReview.copyWasNotReceivedNotes}
                                  label="Notes*"
                                  maxLength={256}
                                  className="ServicePlanViewer-TextField"
                                />
                              </Col>
                            </Row>
                          )}
                          <Row>
                            <Col>
                              <RadioGroupField
                                view="row"
                                isDisabled
                                name="clinicianReview.isClientLSSProgramParticipant"
                                selected={data.clinicianReview.isClientLSSProgramParticipant}
                                title="Is client currently enrolled in any other LSS program?*"
                                options={YES_NO_OPTIONS}
                                containerClass="ServicePlanViewer-RadioGroupField"
                              />
                            </Col>
                          </Row>
                          {data.clinicianReview.isClientLSSProgramParticipant && (
                            <Row>
                              <Col>
                                <TextField
                                  isDisabled
                                  name="clinicianReview.lssPrograms"
                                  value={data.clinicianReview.lssPrograms}
                                  label="Please specify programs*"
                                  maxLength={256}
                                  className="ServicePlanViewer-TextField"
                                />
                              </Col>
                            </Row>
                          )}
                        </>
                      )}
                    </div>
                    <ServicePlanScoring
                      isDisabled
                      data={getServicePlanScoring(this.props.data, this.getDirectoryData())}
                    />

                    {data.custome && (
                      <ShowCustomerServicePlan
                        template={data.template}
                        result={data.result}
                        changeHaveCustomServicePlan={this.changeHaveCustomServicePlan}
                        changeNarratives={this.changeNarratives}
                        changeCareTeam={this.changeCareTeam}
                        changeConsentToReceiveCarePlan={this.changeConsentToReceiveCarePlan}
                        changeMentalAcuityLevel={this.changeMentalAcuityLevel}
                      />
                    )}
                  </>
                )}
              </>
            )}
          </>
        )}

        {tab === 1 && (
          <Table
            hasPagination
            keyField="id"
            title="Change History"
            noDataText="No data"
            isLoading={history.isFetching}
            className="ServicePlanChangeHistory"
            containerClass="ServicePlanChangeHistoryContainer"
            data={history.dataSource.data}
            pagination={history.dataSource.pagination}
            columns={[
              {
                dataField: "dateModified",
                text: "Date",
                align: "right",
                headerAlign: "right",
                formatter: (v) => format(v, DATE_TIME_FORMAT),
              },
              {
                dataField: "status",
                text: "Status",
                align: "left",
              },
              {
                dataField: "author",
                text: "Author",
                formatter: (v, row) => `${row.author}, ${row.authorRole}`,
              },
              {
                dataField: "isArchived",
                text: "Updates",
                headerStyle: {
                  textAlign: "left",
                },
                formatter: (v, row) =>
                  v ? (
                    <Button
                      color="link"
                      className="ServicePlanChangeHistory-ViewDetailsBtn"
                      onClick={() => this.onViewDetails(row)}
                    >
                      View Details
                    </Button>
                  ) : null,
              },
            ]}
            columnsMobile={["dateModified"]}
            renderCaption={(title) => {
              return (
                <div className="ServicePlanChangeHistory-Caption">
                  <div className="ServicePlanChangeHistory-Title">{title}</div>
                </div>
              );
            }}
            onRefresh={this.onRefreshHistory}
          />
        )}
      </Modal>
    );
  }
}

export default withRouter(connect(mapStateToProps, mapDispatchToProps)(withDirectoryData(ServicePlanViewer)));
