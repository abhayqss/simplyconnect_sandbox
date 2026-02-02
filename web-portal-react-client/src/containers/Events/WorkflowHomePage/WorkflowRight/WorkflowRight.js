import React, { PureComponent } from "react";

import cn from "classnames";
import memo from "memoize-one";

import { any, compact, find, findIndex, first } from "underscore";

import $ from "jquery";
import "jquery.scrollto";

import { connect } from "react-redux";
import { bindActionCreators } from "redux";

import { withRouter } from "react-router-dom";

import DocumentTitle from "react-document-title";

import { Badge, Collapse } from "reactstrap";

import { withDirectoryData, withEvent } from "hocs";

import { Action, ErrorViewer, Loader, Table, Tabs } from "components";

import { Button } from "components/buttons";

import { SuccessDialog, WarningDialog } from "components/dialogs";

import { LoadCanViewNotesAction } from "actions/events";

import UpdateSideBarAction from "actions/sidebar/UpdateSideBarAction";

import notePageNumberActions from "redux/note/page/number/notePageNumberActions";
import eventPageNumberActions from "redux/event/page/number/eventPageNumberActions";
import eventNoteComposedListActions from "redux/event/note/composed/list/eventNoteComposedListActions";

import EventNoteFilter from "../../EventNoteFilter/EventNoteFilter";

import NoteEditor from "../../NoteEditor/NoteEditor";
import NoteDetails from "../../NoteDetails/NoteDetails";
import EventEditor from "../../EventEditor/EventEditor";

import { EVENT_GROUP_COLORS, NOTE_TYPES, PAGINATION, RESPONSIVE_BREAKPOINTS, SERVER_ERROR_CODES } from "lib/Constants";

import { DateUtils as DU, defer, isEmpty, isInteger, isNotEmpty, promise, toNumberExcept } from "lib/utils/Utils";

import { Response } from "lib/utils/AjaxUtils";
import { getQueryParams } from "lib/utils/UrlUtils";

import { ReactComponent as Filter } from "images/filters.svg";

import "./WorkflowRight.scss";
import EventDetails from "../../EventDetails/EventDetails";

const EVENT = "EVENT";
const NOTE = "NOTE";

const { EVENT_NOTE, GROUP_NOTE, CLIENT_NOTE, PATIENT_NOTE } = NOTE_TYPES;

const { TABLET_PORTRAIT } = RESPONSIVE_BREAKPOINTS;

const NOTE_TYPE_COLORS = {
  [EVENT_NOTE]: "#d5f3b8",
  [CLIENT_NOTE]: "#e7ccfe",
  [PATIENT_NOTE]: "#e7ccfe",
  [GROUP_NOTE]: "#c9e5ff",
};

const NOTE_SUB_TYPE_COLORS = {
  FACE_TO_FACE_ENCOUNTER: "#fff1ca",
  NON_FACE_TO_FACE_ENCOUNTER: "#fff1ca",
};

const ENTITY_TAB_INDEXES = {
  [EVENT]: 1,
  [NOTE]: 2,
};

const { format, formats } = DU;

const TIME_FORMAT = formats.time;
const DATE_FORMAT = formats.americanMediumDate;

const { FIRST_PAGE } = PAGINATION;

const { ACCESS_DENIED, ACCOUNT_INACTIVE } = SERVER_ERROR_CODES;

function getIdKey(entity) {
  return entity ? entity.toLowerCase() + "Id" : null;
}

function isIgnoredError(e = {}) {
  return [ACCESS_DENIED, ACCOUNT_INACTIVE].includes(e.code);
}

function mapStateToProps(state) {
  const { event, note } = state;
  const { list } = event.note.composed;

  return {
    error: list.error,
    isFetching: list.isFetching,
    fetchCount: list.fetchCount,
    dataSource: list.dataSource,
    shouldReload: list.shouldReload,

    canAdd: event.can.add.value,
    canAddNote: note.can.add.value,
    canViewNotes: note.can.view.value,

    notePageNumber: note.page.number,
    eventPageNumber: event.page.number,

    event,
    auth: state.auth,

    client: {
      details: state.client.details,
    },

    directory: state.directory,
  };
}

function mapDispatchToProps(dispatch) {
  return {
    actions: {
      ...bindActionCreators(eventNoteComposedListActions, dispatch),
      pageNumber: {
        note: bindActionCreators(notePageNumberActions, dispatch),
        event: bindActionCreators(eventPageNumberActions, dispatch),
      },
    },
  };
}

class WorkFlowRightEvents extends PureComponent {
  state = {
    tab: 0,

    saved: null,
    selected: null,
    shouldScroll: false,
    shouldRefresh: false,
    isSelectDisabled: false,

    isFilterOpen: false,

    isEventEditorOpen: false,
    isSaveEventSuccessDialogOpen: false,
    isSaveEventNoteSuccessDialogOpen: false,

    isNoteEditorOpen: false,
    isSaveNoteSuccessDialogOpen: false,
    isUpdateNoteSuccessDialogOpen: false,

    isGroupNoteEditorOpen: false,
    isGroupNoteWarningDialogOpen: false,

    isAccessDeniedDialogOpen: false,
    isShowModal: false,
  };

  constructor(props) {
    super(props);

    this.getEventIndicatorColor = memo(this.getEventIndicatorColor);

    this.getNoteIndicatorColor = memo(this.getNoteIndicatorColor);
  }

  get actions() {
    return this.props.actions;
  }

  get clientId() {
    return toNumberExcept(this.props.match.params.clientId, [undefined]);
  }

  get authUser() {
    return this.props.auth.login.user.data;
  }

  get isMobileView() {
    return document.body.getBoundingClientRect().width < TABLET_PORTRAIT;
  }

  componentDidMount() {
    const { state } = this.props.location;

    if (state) {
      const { tab, noteId, selected, shouldCreateNote, shouldCreateEvent } = state;

      this.setState({
        isEventEditorOpen: !!shouldCreateEvent,
      });

      this.setState({
        isNoteEditorOpen: !!shouldCreateNote,
      });

      if (noteId) {
        this.onChangeTab(2);
        this.onSelectNote(noteId);
      } else {
        this.onChangeTab(tab ?? 0);
        if (tab === 1) this.onSelectEvent(selected?.id);

        if (tab === 2) this.onSelectNote(selected?.id);
      }

      this.props.history.replace("events", {});
    } else if (isInteger(this.clientId)) {
      defer(200).then(() => this.refresh());
    }

    if (this.isMobileView) this.scrollToList();
  }

  componentDidUpdate(prevProps) {
    if (this.props.shouldReload) {
      this.refresh();
    }
    if (this.props.isWorkflowAdd !== prevProps.isWorkflowAdd) {
      this.refresh();
    }
  }

  componentWillUnmount() {
    this.actions.clear();
    this.actions.clearFilter();
  }

  onResetError = () => {
    this.actions.clearError();
  };

  onRefresh = (page) => {
    this.refresh(page);
  };

  onSelect = (selected = null) => {
    this.setState({ selected });
  };

  onToggleFilter = () => {
    this.setState((s) => ({
      isFilterOpen: !s.isFilterOpen,
    }));
  };

  onChangeTab = (tab) => {
    this.setState({
      selected: null,
      isShowModal: false,
    });
    if (!this.props.isFetching) {
      this.changeTab(tab).then(() => {
        this.refresh();
      });
    }
  };

  onSelectEvent = (id) => {
    if (!id) return;

    this.selectEvent(id);
    this.setState({ shouldScroll: true });
  };

  onSelectNote = (id) => {
    if (!id) return;

    this.selectNote(id);
    this.setState({ shouldScroll: true });
  };

  onSaveEventNoteSuccessDialogOpen = () => {
    this.setState({ isSaveEventNoteSuccessDialogOpen: true });
  };

  onUpdateNoteSuccessDialogOpen = () => {
    this.setState({ isUpdateNoteSuccessDialogOpen: true });
  };

  onLoadDetailsSuccess = () => {
    const { selected, shouldScroll } = this.state;

    if (shouldScroll && selected) {
      const index = findIndex(this.props.dataSource.data, { id: selected.id });

      this.scrollToListItem(index);

      this.setState({ shouldScroll: false });
    }
  };

  onSaveEventSuccess = (id) => {
    this.setState({
      tab: 1,
      saved: { id },
      selected: null,
      shouldRefresh: true,
      isEventEditorOpen: false,
      isSaveEventSuccessDialogOpen: true,
    });
  };

  onViewSavedEventDetails = () => {
    this.setState({
      isSaveEventSuccessDialogOpen: false,
    });

    this.selectEvent(this.state.saved?.id);
  };

  onCloseSaveEventSuccessDialog = () => {
    this.setState({ isSaveEventSuccessDialogOpen: false });
  };

  onCloseEventEditor = () => {
    this.setState({
      isEventEditorOpen: false,
    });
  };

  onSaveNoteSuccess = () => {
    const { tab } = this.state;

    this.setState({
      selected: null,
      shouldRefresh: [0, 2].includes(tab),
      isNoteEditorOpen: false,
      isSaveNoteSuccessDialogOpen: true,
    });
  };

  onSaveGroupNoteSuccess = () => {
    const { tab } = this.state;

    this.setState({
      selected: null,
      shouldRefresh: [0, 2].includes(tab),
      isGroupNoteEditorOpen: false,
      isSaveNoteSuccessDialogOpen: true,
    });
  };

  onCloseSaveNoteSuccessDialog = () => {
    this.setState({ isSaveNoteSuccessDialogOpen: false });
  };

  onAddGroupNote = () => {
    this.setState({ isGroupNoteEditorOpen: true });
  };

  onCloseGroupNoteEditor = () => {
    this.setState({ isGroupNoteEditorOpen: false });
  };

  onCloseNoteEditor = () => {
    this.setState({ isNoteEditorOpen: false });
  };

  onCloseDetailsMobile = () => {
    this.setState({
      selected: null,
      isShowModal: false,
    });
  };

  onSaveEventNoteSuccess = async (shouldRedirectToNote) => {
    this.setState({ isSaveEventNoteSuccessDialogOpen: false });

    await this.changeTab(shouldRedirectToNote ? 2 : this.state.tab);

    this.refresh();
  };

  changeTab(tab) {
    return new Promise((resolve) => {
      this.setState({ tab }, resolve);
    });
  }

  scrollToList() {
    let navBarElement = $(".Events-Navigation-Right").get(0);
    let scrollable = $(".SideBar-Content, .App-Content");

    scrollable.stop().animate({
      scrollTop: navBarElement.offsetTop,
    });
  }

  scrollToListItem(index) {
    let listItemElement = $(".react-bootstrap-table tbody tr").get(index);
    let scrollable = $(".react-bootstrap-table");

    this.scrollToList();

    scrollable.animate({
      scrollTop: listItemElement.offsetTop,
    });
  }

  refresh(page) {
    return this.update(true, page || FIRST_PAGE);
  }

  update(isReload, page) {
    const { isFetching, shouldReload, dataSource: ds } = this.props;

    if (isReload || shouldReload || (!isFetching && isEmpty(ds.data))) {
      const clientId = this.clientId;

      const filter = ds.filter.toJS();
      const { organizationId } = filter;

      if (clientId || organizationId) {
        const { tab } = this.state;
        const { page: p, size } = ds.pagination;

        return this.actions.load({
          size,
          filter,
          page: page || p,
          excludeNotes: tab === 1,
          excludeEvents: tab === 2,
          ...(clientId ? { clientId } : { organizationId }),
        });
      }
    }
    return promise();
  }

  onFailure = (error) => {
    if (error?.code === ACCESS_DENIED)
      this.setState({
        isAccessDeniedDialogOpen: true,
      });
  };

  selectEvent(id) {
    this.selectListItem(id, EVENT);
  }

  selectNote(id) {
    this.selectListItem(id, NOTE);
  }

  selectListItem(id, entity) {
    this.setState(
      {
        isSelectDisabled: true,
        tab: ENTITY_TAB_INDEXES[entity],
      },
      () => {
        this.getListPageNumber(id, entity).then(
          Response(({ data: page }) => {
            const { isFetching, dataSource: ds } = this.props;

            const setSelected = () => {
              this.setState({
                isSelectDisabled: false,
                selected: { id, entity },
              });
            };

            if (page === 0 && (isFetching || isNotEmpty(ds.data)) && ds.pagination.page === FIRST_PAGE) {
              setSelected();
            } else this.refresh(page + 1).then(Response(setSelected, this.onFailure));
          }, this.onFailure),
        );
      },
    );
  }

  getListPageNumber(id, entity) {
    const key = entity.toLowerCase();

    const ds = this.props.dataSource;
    const { organizationId } = ds.filter;

    const clientId = ds.filter.clientId || this.clientId;

    const idParams = clientId ? { clientId } : { organizationId };

    return this.actions.pageNumber[key].load({
      ...idParams,
      pageSize: 15,
      [getIdKey(entity)]: id,
    });
  }

  getDirectoryData() {
    return this.props.getDirectoryData({
      noteTypes: ["note", "type"],
      eventTypes: ["event", "type"],
      communities: ["community"],
    });
  }

  getEventIndicatorColor(typeName) {
    const group =
      find(this.getDirectoryData().eventTypes, (group) => any(group.eventTypes, (o) => o.name === typeName)) || {};

    return EVENT_GROUP_COLORS[group.name] ?? "#fff1ca";
  }

  getNoteIndicatorColor(subTypeName) {
    return NOTE_SUB_TYPE_COLORS[subTypeName] ?? "#d3dfe8";
  }

  canAddGroupNote() {
    const { communityIds } = this.props.dataSource.filter;

    const { communities } = this.getDirectoryData();

    const selectedCommunity = communities.length
      ? communities?.find((community) => community.id === first(communityIds))
      : undefined;

    if (communityIds.length !== 1 || !selectedCommunity) {
      return [false, "Please choose one community in the filter."];
    }

    if (!selectedCommunity?.canAddGroupNote) {
      return [false, `You don't have permissions to create a group note.`];
    }

    return [true];
  }

  render() {
    const {
      tab,
      selected,
      shouldRefresh,
      isSelectDisabled,

      isFilterOpen,

      isEventEditorOpen,
      isSaveEventSuccessDialogOpen,

      isNoteEditorOpen,
      isSaveNoteSuccessDialogOpen,

      isGroupNoteEditorOpen,
      isGroupNoteWarningDialogOpen,

      isAccessDeniedDialogOpen,
      isShowModal,
    } = this.state;

    const {
      error,
      isFetching,
      fetchCount,
      dataSource: ds,
      isWorkflowAdd,

      canAdd,
      canAddNote,
      canViewNotes,
      notePageNumber,
      eventPageNumber,

      client,
      location,
    } = this.props;

    const clientId = this.clientId;
    const isClient = isInteger(this.clientId);

    const organizationId = ds.filter.organizationId;

    const [canAddGNote, cannotAddGNoteReason] = this.canAddGroupNote();
    const { fullName: clientName, isActive: isClientActive } = isClient ? client?.details?.data || {} : {};

    const communityId = first(this.props.dataSource.filter.communityIds);

    return (
      // title
      <DocumentTitle title={`Simply Connect | Events and Notes`}>
        <>
          <div className={cn("Events-Right")}>
            <LoadCanViewNotesAction
              isMultiple
              params={{ clientId, organizationId }}
              shouldPerform={(prevParams) =>
                isInteger(organizationId) &&
                (prevParams.clientId !== clientId || prevParams.organizationId !== organizationId)
              }
            />

            <>
              <Action
                isMultiple
                params={{ organizationId }}
                shouldPerform={(prevParams) =>
                  !isFetching &&
                  fetchCount === 0 &&
                  isInteger(organizationId) &&
                  organizationId !== prevParams.organizationId
                }
                action={() => {
                  defer(200).then(() => this.refresh());
                }}
              />
              <UpdateSideBarAction params={{ changes: { isHidden: true } }} />
            </>
            <Action
              isMultiple
              params={{ shouldRefresh }}
              shouldPerform={() => !isFetching && shouldRefresh}
              action={() => {
                defer(300).then(() => this.refresh());
                this.setState({ shouldRefresh: false });
              }}
            />
            <Action
              isMultiple
              params={{ fetchCount }}
              shouldPerform={(prevParams) => !isSelectDisabled && fetchCount > prevParams.fetchCount}
              action={() => {
                !this.isMobileView && this.onSelect(first(ds.data));
              }}
            />
            <Action
              isMultiple
              params={{ isFetching }}
              shouldPerform={(prevParams) => isFetching && !prevParams.isFetching && !isSelectDisabled}
              action={this.onSelect}
            />
            <Action
              shouldPerform={() => !!location.search && isInteger(organizationId)}
              action={() => {
                const { noteId, eventId } = getQueryParams(location.search);

                if (noteId && eventId) return;

                this.onSelectEvent(+eventId);
                this.onSelectNote(+noteId);
              }}
            />
            <Action
              isMultiple
              params={{
                isFetching: eventPageNumber.isFetching || notePageNumber.isFetching,
              }}
              shouldPerform={(prevParams) =>
                (eventPageNumber.isFetching || notePageNumber.isFetching) && !prevParams.isFetching
              }
              action={this.onSelect}
            />

            <div className="Events-Header-Right">
              <div className="Events-Title-Right">
                <span className="Events-TitleText-Right">
                  Events & Notes
                  {ds.pagination.totalCount ? (
                    <Badge color="info" className="Badge Badge_place_top-right">
                      {ds.pagination.totalCount}
                    </Badge>
                  ) : null}
                </span>
              </div>
              <div className="Events-ControlPanel-Right">
                <Filter
                  className={cn(
                    "EventFilter-Icon",
                    isFilterOpen ? "EventFilter-Icon_rotated_90" : "EventFilter-Icon_rotated_0",
                  )}
                  onClick={this.onToggleFilter}
                />
                {/*Client NameClient Name*/}
              </div>
              <div style={{ width: "100%" }}>
                {canViewNotes && (
                  <Button
                    color="success"
                    id="add-group-note-btn"
                    disabled={!canAddGNote}
                    style={{ width: "100%" }}
                    onClick={this.onAddGroupNote}
                    hasTip={!canAddGNote}
                    tipText={cannotAddGNoteReason}
                    tipPlace="top"
                  >
                    Create a Group Note
                  </Button>
                )}
              </div>
            </div>
            {/*  搜索条件是否展示 */}
            <Collapse isOpen={isFilterOpen}>
              <EventNoteFilter clientId={clientId} areNotesExcluded={!canViewNotes} />
            </Collapse>

            <div className="Events-NavigationContainer-Right row">
              <div
                className={cn(
                  // "col-sm-6",
                  // "col-xl-4",
                  "Events-Navigation-Right",
                )}
              >
                {/* 左侧table列表 */}
                <div className="Events-NavHeader-Right">
                  <Tabs
                    items={compact([
                      canViewNotes && { title: "All", isActive: tab === 0 },
                      { title: "Events", isActive: tab === 1 },
                      canViewNotes && { title: "Notes", isActive: tab === 2 },
                    ])}
                    onChange={this.onChangeTab}
                    className="Events-NavTabs-Right"
                    containerClassName="Events-NavTabsContainer-Right"
                  />
                </div>

                <div className="Events-NavBody-Right">
                  {isFetching || eventPageNumber.isFetching || notePageNumber.isFetching ? (
                    <Loader />
                  ) : (
                    <Table
                      keyField="id"
                      hasHover={true}
                      hasPagination={true}
                      rowEvents={{
                        onClick: (e, row) => {
                          this.onSelect(row);
                          this.setState({ isShowModal: true });
                        },
                      }}
                      getRowStyle={(row) => ({
                        ...(selected?.id === row.id
                          ? {
                              backgroundColor: "#f2fbff",
                            }
                          : null),
                      })}
                      noDataText={`No ${["events/notes", "events", "notes"][canViewNotes ? tab : 1]} found.`}
                      className="EventNoteComposedList"
                      containerClass="EventNoteComposedListContainer-Right"
                      data={ds.data}
                      pagination={ds.pagination}
                      columns={[
                        {
                          text: "",
                          dataField: "clientName",
                          style: (cell, row, i) => ({
                            borderLeft: "8px solid",
                            borderLeftColor:
                              row.entity === EVENT
                                ? this.getEventIndicatorColor(row.typeName)
                                : this.getNoteIndicatorColor(row.subTypeName),
                          }),
                          formatter: (v, row) =>
                            row.entity === EVENT ? (
                              <>
                                <div className="Event-ClientName-Right">{row.clientName}</div>
                                <div className="Event-Type">{row.typeTitle}</div>
                                {tab === 0 && (
                                  <span className="Event-Entity" style={{ backgroundColor: "#fff1ca" }}>
                                    EVENT
                                  </span>
                                )}
                              </>
                            ) : (
                              <>
                                <div className="Note-ClientName">{row.clientName}</div>
                                <div className="Note-Subtype">{row.subTypeTitle}</div>
                                <span
                                  style={{
                                    backgroundColor: NOTE_TYPE_COLORS[row.typeName],
                                  }}
                                  className="Note-Entity"
                                >
                                  {row.typeTitle}
                                </span>
                              </>
                            ),
                        },
                        {
                          text: "",
                          dataField: "date",
                          formatter: (v, row) =>
                            row.entity === EVENT ? (
                              <>
                                <div className="Event-Date">{format(v, DATE_FORMAT)}</div>
                                <div className="Event-Time">{format(v, TIME_FORMAT)}</div>
                              </>
                            ) : (
                              <>
                                <div className="Note-Date">{format(v, DATE_FORMAT)}</div>
                                <div className="Note-Time">{format(v, TIME_FORMAT)}</div>
                              </>
                            ),
                        },
                      ]}
                      onRefresh={this.onRefresh}
                    />
                  )}
                </div>
              </div>
              <div className={cn("col-sm-6", "col-xl-8", "Events-DetailsContainer")}>
                <div>
                  {selected?.entity === EVENT && isShowModal && (
                    <EventDetails
                      isWorkflowEvent={true}
                      clientId={clientId}
                      eventId={selected.id}
                      organizationId={organizationId}
                      onLoadSuccess={this.onLoadDetailsSuccess}
                      onLoadFailure={this.onFailure}
                      onSaveNoteSuccessDialogOpen={this.onSaveEventNoteSuccessDialogOpen}
                      onSaveNoteSuccess={this.onSaveEventNoteSuccess}
                      onClose={this.onCloseDetailsMobile}
                    />
                  )}
                  {selected?.entity === NOTE && isShowModal && (
                    <NoteDetails
                      isWorkflowEvent={true}
                      clientId={clientId}
                      noteId={selected.id}
                      isClientActive={isClientActive}
                      organizationId={organizationId}
                      onPickEvent={this.onSelectEvent}
                      onLoadSuccess={this.onLoadDetailsSuccess}
                      onLoadFailure={this.onFailure}
                      onUpdateSuccessDialogOpen={this.onUpdateNoteSuccessDialogOpen}
                      onSaveSuccess={() => {
                        this.refresh();
                        this.setState({ isUpdateNoteSuccessDialogOpen: false });
                      }}
                      onClose={this.onCloseDetailsMobile}
                    />
                  )}
                </div>
              </div>
            </div>

            {/*all open modal*/}
            {isEventEditorOpen && (
              <EventEditor
                isOpen
                clientId={clientId}
                onSaveSuccess={this.onSaveEventSuccess}
                onClose={this.onCloseEventEditor}
              />
            )}
            {isSaveEventSuccessDialogOpen && (
              <SuccessDialog
                isOpen
                title="The event has been submitted"
                buttons={[
                  {
                    text: "Close",
                    outline: true,
                    onClick: this.onCloseSaveEventSuccessDialog,
                  },
                  {
                    text: "View details",
                    onClick: this.onViewSavedEventDetails,
                  },
                ]}
              />
            )}
            {isNoteEditorOpen && (
              <NoteEditor
                isOpen
                clientId={clientId}
                clientName={clientName}
                organizationId={organizationId}
                onClose={this.onCloseNoteEditor}
                onSaveSuccess={this.onSaveNoteSuccess}
              />
            )}
            {isGroupNoteEditorOpen && (
              <NoteEditor
                isOpen
                isGroup
                clientId={clientId}
                communityId={communityId}
                organizationId={organizationId}
                onClose={this.onCloseGroupNoteEditor}
                onSaveSuccess={this.onSaveGroupNoteSuccess}
              />
            )}
            {isSaveNoteSuccessDialogOpen && (
              <SuccessDialog
                isOpen
                title="The note has been created."
                buttons={[
                  {
                    text: "Close",
                    outline: true,
                    onClick: this.onCloseSaveNoteSuccessDialog,
                  },
                  {
                    text: "View Note",
                    onClick: () => {
                      this.changeTab(2).then(() => this.refresh());
                      this.onCloseSaveNoteSuccessDialog();
                    },
                  },
                ].filter((v, index) => tab === 1 || !index)}
              />
            )}
            {isGroupNoteWarningDialogOpen && (
              <WarningDialog
                isOpen
                title="Please choose one community in the filter"
                buttons={[
                  {
                    text: "Ok",
                    onClick: () => {
                      this.setState({
                        isGroupNoteWarningDialogOpen: false,
                      });
                    },
                  },
                ]}
              />
            )}
            {isAccessDeniedDialogOpen && (
              <WarningDialog
                isOpen
                title={`You don't have permissions to see this ${selected?.entity === EVENT ? "event" : "note"}`}
                buttons={[
                  {
                    text: "Close",
                    onClick: () =>
                      this.setState({
                        isAccessDeniedDialogOpen: false,
                      }),
                  },
                ]}
              />
            )}
            {error && !isIgnoredError(error) && <ErrorViewer isOpen error={error} onClose={this.onResetError} />}
          </div>
        </>
      </DocumentTitle>
    );
  }
}

export default withRouter(
  connect(mapStateToProps, mapDispatchToProps)(withEvent(".App-Content")(withDirectoryData(WorkFlowRightEvents))),
);
