import cn from "classnames";
import React, { useEffect, useState } from "react";
import { ReactComponent as Filter } from "images/filters.svg";
import { ReactComponent as Empty } from "images/empty.svg";
import "./WorkflowLeft.scss";
import { Button } from "components/buttons";
import WorkflowListItem from "../../WorkflowListItem/WorkflowListItem";
import Pagination from "components/Pagination/Pagination";
import AddWorkflowModal from "components/Events/AddWorkflowModal/AddWorkflowModal";
import { Collapse } from "reactstrap";
import WorkflowFilter from "../../WorkflowFilter/WorkflowFilter";
import { useHistory, withRouter } from "react-router-dom";
import { connect } from "react-redux";
import workflowService from "services/WorkflowService";
import Loader from "components/Loader/Loader";
import { withDirectoryData, withEvent } from "hocs";
import { first } from "underscore";
import service from "../../../../services/DirectoryService";
import { useAuthUser } from "../../../../hooks/common";
import { path } from "../../../../lib/utils/ContextUtils";
import { useQuery } from "@tanstack/react-query";

const WorkflowLeft = (props) => {
  const { setIsRefresh, isRefresh, communityIds, organizationId } = props;

  const user = useAuthUser();
  const [isWorkflowFilterOpen, setIsWorkflowFilterOpen] = useState(false);
  const [communities, setCommunities] = useState();
  const [isFetching, setIsFetching] = useState(false);
  const [workflowList, setWorkflowList] = useState([]);
  const [workflowListTotal, setWorkflowListTotal] = useState(0);
  const [currentPage, setCurrentPage] = useState(1);

  const [isAddWorkflowModalShow, setIsAddWorkflowModalShow] = useState(false);
  const [workflowFilterData, setWorkflowFilterData] = useState({});
  const [isRefreshData, setIsRefreshData] = useState(false);

  useEffect(() => {
    if (communityIds.length === 0 || !organizationId) {
      return;
    }
    const params = {
      filter: workflowFilterData,
      communityIds: communityIds,
      organizationId,
      page: currentPage,
      size: 9,
    };
    getWorkflowList(params);
  }, [communityIds, organizationId, currentPage, workflowFilterData, isRefreshData]);

  useEffect(() => {
    if (organizationId) {
      getCommunities(organizationId);
    }
  }, [organizationId]);

  const getWorkflowList = (params) => {
    setIsFetching(true);
    workflowService.findWorkflowForAdmin(params).then((res) => {
      if (res.success) {
        setWorkflowList(res.data);
        setWorkflowListTotal(res.totalCount);
        setIsFetching(false);
      } else {
        setIsFetching(false);
      }
    });
  };

  const getCommunities = (organizationId) => {
    service.findCommunities({ organizationId }).then((res) => {
      setCommunities(res.data);
    });
  };

  // 权限判断是否可以添加workflow
  const getAddTipText = () => {
    const selectedCommunity = communities?.find((community) => community.id === first(communityIds));

    if (communityIds.length !== 1 || !selectedCommunity) {
      return [false, "Please choose one community in the filter."];
    }

    if (!selectedCommunity.canAddWorkflow) {
      return [false, `You don't have permissions to create a workflow.`];
    }

    return [true];
  };

  const [canAddWorkflow, cannotAddWorkflowReason] = getAddTipText();

  const addWorkflow = () => {
    setIsAddWorkflowModalShow(true);
  };

  const onToggleFilter = () => {
    setIsWorkflowFilterOpen(!isWorkflowFilterOpen);
  };

  const changePage = (newPage) => {
    setCurrentPage(newPage);
  };

  const onAddWorkflowCancel = () => {
    setIsAddWorkflowModalShow(false);
  };

  const onAddWorkflowSuccess = (data) => {
    setIsAddWorkflowModalShow(false);
    setIsRefresh(!isRefresh); // refresh event flag
    if (currentPage === 1) {
      getWorkflowList({
        communityIds,
        organizationId,
        page: 1,
        size: 9,
        filter: workflowFilterData,
      });
    } else {
      setCurrentPage(1);
    }
  };
  const qaIncidentReports = user?.qaIncidentReports;
  const history = useHistory();

  const goQAWorkflow = () => {
    history.push(path(`/admin-events/qa/events/admin`));
  };
  return (
    <>
      <div className="WorkflowLeft">
        <div className="WorkflowLeft-Header">
          <div className="WorkflowLeft-Title">
            <span className="WorkflowLeft-TitleText">Workflow</span>
          </div>
          <div className="WorkflowLeft-ControlPanel">
            <Filter
              className={cn(
                "WorkflowLeft-Icon",
                isWorkflowFilterOpen ? "WorkflowLeft-Icon_rotated_90" : "WorkflowLeft-Icon_rotated_0",
              )}
              onClick={onToggleFilter}
            />
            <Button
              color="success"
              id="add-group-note-btn"
              disabled={!canAddWorkflow}
              className="margin-left-20"
              onClick={addWorkflow}
              hasTip={!canAddWorkflow}
              tipText={cannotAddWorkflowReason}
              tipPlace="top"
            >
              Add Workflow
            </Button>
            {qaIncidentReports && (
              <Button style={{ marginLeft: 15 }} type="outline" color={"success"} onClick={goQAWorkflow}>
                QA Workflow
              </Button>
            )}
          </div>
        </div>
        <Collapse isOpen={isWorkflowFilterOpen}>
          <WorkflowFilter
            organizationId={organizationId}
            communityIds={communityIds}
            setWorkflowFilterData={setWorkflowFilterData}
          />
        </Collapse>
        {isFetching && <Loader />}
        {workflowList && !isFetching && (
          <>
            {workflowList.map((item, index) => {
              return (
                <React.Fragment key={index}>
                  <WorkflowListItem
                    data={item}
                    setIsRefreshData={setIsRefreshData}
                    isRefreshData={isRefreshData}
                    isFetching={isFetching}
                    setIsFetching={setIsFetching}
                  />
                </React.Fragment>
              );
            })}
            <div className={"list-Pagination"}>
              <div className={"list-position"}>
                <Pagination totalCount={workflowListTotal} page={currentPage} size={9} onPageChange={changePage} />
              </div>
            </div>
          </>
        )}
        {workflowList.length === 0 && !isFetching && (
          <div style={{ width: "100%", textAlign: "center" }}>
            <Empty style={{ width: 120, height: 120, marginBottom: 20 }}></Empty>
            <div>No data</div>
          </div>
        )}
      </div>

      <AddWorkflowModal
        isOpen={isAddWorkflowModalShow}
        onCancel={onAddWorkflowCancel}
        onConfirm={onAddWorkflowSuccess}
        onClose={onAddWorkflowCancel}
        communityIds={communityIds}
        organizationId={organizationId}
      />
    </>
  );
};
export default withRouter(connect()(withEvent(".App-Content")(withDirectoryData(WorkflowLeft))));
