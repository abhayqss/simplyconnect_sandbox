import DocumentTitle from "react-document-title";
import { UpdateSideBarAction } from "../../../../actions/admin";
import React from "react";
import { Breadcrumbs } from "../../../../components";
import { useAuthUser } from "../../../../hooks/common";
import "./WorkflowManager.scss";
import activeImg from "images/workflow/analytics/active.svg";
import oaImg from "images/workflow/analytics/oa.svg";
import timeImg from "images/workflow/analytics/time.svg";
import numberImg from "images/workflow/analytics/number.svg";
import { styled } from "@mui/material/styles";
import LinearProgress, { linearProgressClasses } from "@mui/material/LinearProgress";
import { Bar, BarChart, ResponsiveContainer } from "recharts";

const BorderLinearProgress = styled(LinearProgress)(({ theme }) => ({
  height: 7,
  borderRadius: 5,
  [`&.${linearProgressClasses.colorPrimary}`]: {
    backgroundColor: "#EDEDED",
  },
  [`& .${linearProgressClasses.bar}`]: {
    borderRadius: 5,
    backgroundColor: "#1CC1A3",
  },
}));

const data = [
  {
    name: "Page A",
    uv: 4000,
    pv: 2400,
    amt: 2400,
  },
  {
    name: "Page B",
    uv: 3000,
    pv: 1398,
    amt: 2210,
  },
  {
    name: "Page C",
    uv: 2000,
    pv: 9800,
    amt: 2290,
  },
  {
    name: "Page D",
    uv: 2780,
    pv: 3908,
    amt: 2000,
  },
  {
    name: "Page E",
    uv: 1890,
    pv: 4800,
    amt: 2181,
  },
  {
    name: "Page F",
    uv: 2390,
    pv: 3800,
    amt: 2500,
  },
  {
    name: "Page G",
    uv: 3490,
    pv: 4300,
    amt: 2100,
  },
];

const Analytics = () => {
  const user = useAuthUser();
  const isSuperAdmin = user.roleName === "ROLE_SUPER_ADMINISTRATOR";

  return (
    <DocumentTitle title={"Simply Connect | Admin | workflow"}>
      <>
        <div>
          <UpdateSideBarAction />
        </div>

        <div className="AnalyticsWrap">
          <Breadcrumbs
            items={[
              { title: "Admin", href: "/admin/workflow", isEnabled: true },
              {
                title: isSuperAdmin ? "Workflow Library" : "Workflow Management",
                href: "/admin/workflowManager",
              },
              {
                title: "Workflow Analytics",
                href: "/admin/workflowManager/analytics/:id",
                isActive: true,
              },
            ]}
          />

          <div className="analyticsHeader">Workflow Analytics</div>

          <div className="topAnalytics">
            <div className="topAnalyticsBox">
              <div className="topAnalyticsTitle">Active Workflow</div>
              <div className="fistBox topText">827</div>
              <img src={activeImg || ""} alt="" />
            </div>
            <div className="topAnalyticsBox">
              <div className="topAnalyticsTitle">OA progress</div>
              <div className="secBox topText">98%</div>
              <img src={oaImg || ""} alt="" />
            </div>
            <div className="topAnalyticsBox">
              <div className="topAnalyticsTitle">Average workflow time</div>
              <div className="thrBox topText">
                32
                <span>Hrs</span>
                56
                <span>mins</span>
              </div>
              <img src={timeImg || ""} alt="" />
            </div>
            <div className="topAnalyticsBox">
              <div className="topAnalyticsTitle">Average number (QA)</div>
              <div className="fourBox topText">45</div>
              <img src={numberImg || ""} alt="" />
            </div>
          </div>

          <div className="echartsWrap">
            <div className="leftEcharts echartsBox">
              <div className="blackBg" />
              <div className="echartsContent">
                <div className="echartsContentTitle">Annual completed workflow</div>
                <div className="workflowCenter">
                  <div className="workflowCenterLeft">
                    <span>323</span>
                    <span>Annual workflow</span>
                  </div>
                  <div className="workflowCenterRight">
                    <span>27</span>
                    <span>Average monthly</span>
                  </div>
                </div>

                {/* echarts */}
                <div className="workflowEndEcharts">
                  <ResponsiveContainer width="100%" height="100%">
                    <BarChart width={150} height={40} data={data} className={"abcd"}>
                      <Bar dataKey="uv" fill="#A7DEFC" />
                    </BarChart>
                  </ResponsiveContainer>
                </div>
              </div>
            </div>
            <div className="rightEcharts echartsBox">
              <div className="blackBg" />
              <div className="echartsContent">
                <div className="echartsContentTitle">Monthy completed workflows</div>
                {/*  进度  */}

                <div className="progressWrapBox">
                  <div className="progressWrap">
                    <div className="progressTitle">Workflow type one</div>
                    <div className="progressBox">
                      <BorderLinearProgress variant="determinate" value={50} />
                      <span className="progressInfo">50%</span>
                    </div>
                  </div>
                  <div className="progressWrap">
                    <div className="progressTitle">Workflow type one</div>
                    <div className="progressBox">
                      <BorderLinearProgress variant="determinate" value={50} />
                      <span className="progressInfo">50%</span>
                    </div>
                  </div>
                  <div className="progressWrap">
                    <div className="progressTitle">Workflow type one</div>
                    <div className="progressBox">
                      <BorderLinearProgress variant="determinate" value={50} />
                      <span className="progressInfo">50%</span>
                    </div>
                  </div>
                  <div className="progressWrap">
                    <div className="progressTitle">Workflow type one</div>
                    <div className="progressBox">
                      <BorderLinearProgress variant="determinate" value={50} />
                      <span className="progressInfo">50%</span>
                    </div>
                  </div>
                  <div className="progressWrap">
                    <div className="progressTitle">Workflow type one</div>
                    <div className="progressBox">
                      <BorderLinearProgress variant="determinate" value={50} />
                      <span className="progressInfo">50%</span>
                    </div>
                  </div>
                  <div className="progressWrap">
                    <div className="progressTitle">Workflow type one</div>
                    <div className="progressBox">
                      <ResponsiveContainer width={700} height="80%">
                        <BorderLinearProgress variant="determinate" value={50} />
                      </ResponsiveContainer>
                      <span className="progressInfo">50%</span>
                    </div>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>
      </>
    </DocumentTitle>
  );
};

export default Analytics;
