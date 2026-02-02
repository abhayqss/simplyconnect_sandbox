import React, { memo, useMemo, useState, useCallback, useEffect } from "react";

import cn from "classnames";

import { Badge } from "reactstrap";

import { useMedicationStatisticsQuery } from "hooks/business/client";

import { MEDICATION_STATUSES } from "lib/Constants";

import { ErrorViewer } from "components";

import ClientMedications from "./ClientMedications/ClientMedications";
import ClientSummaryFallback from "../ClientSummaryFallback/ClientSummaryFallback";
import ClientMedicationBarChart from "./ClientMedicationBarChart/ClientMedicationBarChart";

import "./ClientMedicationsSummary.scss";

const { ACTIVE } = MEDICATION_STATUSES;
import service from "services/ClientMedicationService";
function ClientMedicationsSummary({ clientId, className }) {
  const [status, setStatus] = useState(ACTIVE);
  const [isErrorViewerOpen, toggleErrorViewer] = useState(false);
  const [error, setError] = useState([]);

  const [data, setData] = useState([]);
  const [isFetching, setIsFetching] = useState(false);
  useEffect(() => {
    getMedicationStatisticsQuery();
  }, [clientId]);
  const getMedicationStatisticsQuery = () => {
    setIsFetching(true);
    service
      .statistics({ clientId })
      .then((res) => {
        setData(res);
        setIsFetching(false);
      })
      .catch((e) => {
        toggleErrorViewer(true);
        setError(e);
        setIsFetching(false);
      });
  };

  const count = useMemo(() => {
    return data?.reduce((accum, o) => {
      return (accum += o.value);
    }, 0);
  }, [data]);

  const onPickStatus = (status) => setStatus(MEDICATION_STATUSES[status]);

  return (
    <div className={cn("ClientMedicationsSummary", className)}>
      <a name="medications-summary" href="#" />
      <div className="ClientMedicationsSummary-Title">
        <span className="ClientMedicationsSummary-TitleText">Medications</span>
        <Badge color="info" className="ClientMedicationsSummary-MedicationCount">
          {count}
        </Badge>
      </div>

      <ClientSummaryFallback isShown={!count} isLoading={isFetching}>
        <div className="ClientMedicationsSummary-Body">
          <ClientMedicationBarChart
            data={data}
            status={status}
            onPickBar={onPickStatus}
            className="ClientMedicationsSummary-Chart flex-1"
          />
          <ClientMedications
            getMedicationStatisticsQuery={getMedicationStatisticsQuery}
            status={status}
            className="flex-1"
            clientId={clientId}
            onChangeStatus={onPickStatus}
          />
        </div>
      </ClientSummaryFallback>

      {isErrorViewerOpen && <ErrorViewer isOpen error={error} onClose={() => toggleErrorViewer(false)} />}
    </div>
  );
}

export default memo(ClientMedicationsSummary);
