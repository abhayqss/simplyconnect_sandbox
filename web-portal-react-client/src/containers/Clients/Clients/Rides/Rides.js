import React, { memo, useRef, useState, useEffect, useCallback } from "react";

import $ from "jquery";

import { compact } from "underscore";

import { useParams } from "react-router-dom";

import { useSelector } from "react-redux";

import { Button } from "reactstrap";

import { useAuthUser, useLocationState } from "hooks/common";

import { useBoundActions } from "hooks/common/redux";

import { useClientQuery, useSideBarUpdate } from "hooks/business/client";

import { Breadcrumbs } from "components";

import { Dialog, WarningDialog } from "components/dialogs";

import ClientEditor from "../ClientEditor/ClientEditor";

import { isNotEmpty, allAreNotEmpty } from "lib/utils/Utils";
import { Response } from "lib/utils/AjaxUtils";

import requestActions from "redux/transportation/ride/request/transpRideRequestActions";
import historyActions from "redux/transportation/ride/history/transpRideHistoryActions";

import { isDataValid } from "../ClientDashboard/ClientDashboard";

import { TRANSPORTATION_ACTION, TRANSPORTATION_ACTION_DESC } from "lib/Constants";

import "./Rides.scss";

function Rides() {
  const [{ isInstructionNeed } = {}, clearLocationState] = useLocationState();

  const [isEditorOpen, setIsEditorOpen] = useState(false);
  const [transportationAction, setTransportationAction] = useState(null);
  const [isInvalidDataWarningDialogOpen, setIsInvalidDataWarningDialogOpen] = useState(false);
  const [isInstructionDialogOpen, toggleInstructionDialog] = useState(isInstructionNeed);

  const transportationFormRef = useRef();

  const client = useSelector((state) => state.client.details?.data ?? {});
  const { canRequestRide, canViewRideHistory } = client;

  const actions = {
    request: useBoundActions(requestActions),
    history: useBoundActions(historyActions),
  };

  const { clientId } = useParams();

  const user = useAuthUser();

  function submitTransportationForm(url, token, action = "") {
    if (allAreNotEmpty(url, token)) {
      const form = transportationFormRef.current;

      $(form).attr("action", url);

      $(form).find('[name="payload"]').val(token);
      $(form).find('[name="action"]').val(action);

      form.submit();
    }
  }

  const onCreateTransportationRideRequest = useCallback(() => {
    actions.request.load({ clientId }).then(
      Response(({ data: { url, token } = {} }) => {
        submitTransportationForm(url, token, "create");
      }),
    );
  }, [actions, clientId]);

  const onOpenTransportationRideHistory = useCallback(() => {
    actions.history.load({ clientId }).then(
      Response(({ data: { url, token } = {} }) => {
        submitTransportationForm(url, token);
      }),
    );
  }, [actions, clientId]);

  function requestRide() {
    setTransportationAction(TRANSPORTATION_ACTION.RIDE);

    if (isDataValid(client) && (client.email || user?.email)) {
      onCreateTransportationRideRequest();
    } else {
      setIsInvalidDataWarningDialogOpen(true);
    }
  }
  // view history
  function viewHistory() {
    setTransportationAction(TRANSPORTATION_ACTION.HISTORY);

    if (isDataValid(client) && (client.email || user?.email)) {
      onOpenTransportationRideHistory();
    } else {
      setIsInvalidDataWarningDialogOpen(true);
    }
  }

  const updateSideBar = useSideBarUpdate({ clientId });

  const onCloseEditor = useCallback(() => {
    setIsEditorOpen(false);
    setTransportationAction(null);
  }, []);

  useClientQuery({ clientId: +clientId });

  useEffect(updateSideBar, []);

  const isClientActive = client?.isActive;

  return (
    <div className="Rides">
      <div className="Rides-Header">
        <Breadcrumbs
          className="Rides-Breadcrumbs"
          items={compact([
            {
              title: "Clients",
              href: "/clients",
              isEnabled: true,
            },
            {
              title: client.fullName ?? "",
              href: `/clients/${clientId}`,
              isEnabled: true,
            },
            {
              title: "Rides",
              href: "#",
              isActive: true,
            },
          ])}
        />

        <div className="Rides-Title">Rides</div>
      </div>

      <div className="Rides-Body">
        <div className="Rides-MessageText">
          <p>
            With Simply Connect Transportation you can efficiently schedule safe and reliable rides directly from your
            laptop, tablet or phone.
          </p>

          <p>
            Simply Connect is integrated real-time directly into the providers’ dispatch system. Requesting, updating or
            canceling transportation appointments is electronic, seamless and does not require additional faxes or phone
            calls. You are provided with ride confirmation instantly and can easily track the ride history.
          </p>

          <p>
            Simply Connect connected to fleets nationwide and continues to grow. All fleets and drivers are trained and
            certified, this is your instant source of certified medical transportation providers.
          </p>
        </div>

        <div className="Rides-ActionButtons">
          <Button
            outline
            color="success"
            className="Rides-Button Rides-ViewHistoryBtn"
            disabled={!canViewRideHistory}
            onClick={viewHistory}
          >
            Ride History
          </Button>

          {isClientActive && (
            <Button
              color="success"
              className="Rides-Button Rides-RequestRideBtn"
              disabled={!canRequestRide}
              onClick={requestRide}
            >
              Request a Ride
            </Button>
          )}
        </div>
      </div>

      <ClientEditor
        isOnDashboard
        isOpen={isEditorOpen}
        clientId={clientId}
        isClientEmailRequired={isNotEmpty(transportationAction) && !(client.email || client?.email)}
        isValidationNeed={isNotEmpty(transportationAction)}
        onClose={onCloseEditor}
        onSaveSuccess={onCloseEditor}
      />

      {isInvalidDataWarningDialogOpen && (
        <WarningDialog
          isOpen
          title={`Please fill in the required fields to ${TRANSPORTATION_ACTION_DESC[transportationAction]}`}
          buttons={[
            {
              text: "Cancel",
              outline: true,
              onClick: () => {
                setTransportationAction(null);
                setIsInvalidDataWarningDialogOpen(false);
              },
            },
            {
              text: "Edit Record",
              onClick: () => {
                setIsEditorOpen(true);
                setIsInvalidDataWarningDialogOpen(false);
              },
            },
          ]}
        />
      )}

      {isInstructionDialogOpen && (
        <Dialog
          isOpen
          buttons={[
            {
              text: "Close",
              color: "success",
              onClick: () => {
                clearLocationState();
                toggleInstructionDialog(false);
              },
            },
          ]}
        >
          <p>
            Easily schedule non-emergency transportation services and share details with family members and care teams
            by clicking Request Ride button.
          </p>
          <p>
            If you have any operational questions reach out to our Support team: <b>support@simplyconnect.me</b>
          </p>
        </Dialog>
      )}

      <form method="POST" target="_blank" className="d-none" ref={transportationFormRef}>
        <input name="action" />
        <input name="payload" />
      </form>
    </div>
  );
}

export default memo(Rides);
