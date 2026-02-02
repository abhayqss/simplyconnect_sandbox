import React, {
    useState,
    useEffect,
    useCallback, useMemo
} from 'react'

import { map } from 'underscore'

import cn from 'classnames'

import Truncate from 'react-truncate'

import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import {
    ListGroup as List,
    ListGroupItem as ListItem,
} from 'reactstrap'

import { useProblemList } from 'hooks/business/client/dashboard'

import { Tabs, Loader, ErrorViewer, Dropdown } from 'components'

import ProblemViewer from '../ProblemViewer/ProblemViewer'

import actions from 'redux/client/problem/list/clientProblemListActions'

import { capitalize, DateUtils as DU, lc } from "lib/utils/Utils"

import { PROBLEM_STATUSES } from 'lib/Constants'

import './ClientProblems.scss'

const {
    OTHER,
    ACTIVE,
    RESOLVED,
} = PROBLEM_STATUSES

const STATUS_NAMES = [ACTIVE, RESOLVED, OTHER]

const { format, formats } = DU

const DATE_FORMAT = formats.longDateMediumTime12

const formatDate = date => format(date, DATE_FORMAT) || '-'

const getProblemDates = ({ resolvedDate, identifiedDate }) => {
    if (!resolvedDate && !identifiedDate) return

    return (
        <>
            <div>{formatDate(identifiedDate)}</div>
            <div>{formatDate(resolvedDate)}</div>
        </>
    )
}

function mapStateToProps(state) {
    return { state: state.client.problem.list }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(actions, dispatch)
    }
}

function ClientProblems(
    {
        state,
        status,
        actions,
        clientId,
        className,
        onChangeStatus
    }
) {
    const [selected, setSelected] = useState(null)
    const [isViewerOpen, setIsViewerOpen] = useState(false)

    const tabs = map(PROBLEM_STATUSES, value => ({
        title: value.toLowerCase(),
        isActive: value === status
    }))

    const {
        state: {
            error,
            isFetching,
            dataSource: ds
        },
        fetch,
    } = useProblemList({
        clientId,
        includeOther: status === OTHER,
        includeActive: status === ACTIVE,
        includeResolved: status === RESOLVED,
    })

    const options = useMemo(() => (
        map(STATUS_NAMES, name => ({
            value: name,
            text: capitalize(name),
            isActive: name === status,
            onClick: () => onChangeStatus(name)
        }))
    ), [onChangeStatus, status])

    const onFetch = useCallback(fetch, [])

    const onOpenViewer = useCallback(o => {
        setSelected(o)
        setIsViewerOpen(true)
    }, [])

    const onCloseViewer = useCallback(() => {
        setSelected(null)
        setIsViewerOpen(false)
    }, [])

    const onChangeTab = useCallback(index => {
        onChangeStatus(STATUS_NAMES[index])
    }, [onChangeStatus])

    useEffect(() => {
        onFetch()

        return actions.clear
    }, [onFetch, status, actions.clear])

    return (
        <div className={cn("ClientProblems", className)}>
            <Tabs
                items={tabs}
                onChange={onChangeTab}
                isDisabled={isFetching}
                className="ClientProblems-Tabs"
                containerClassName="ClientProblems-TabsContainer"
            />
            <Dropdown
                value={status}
                items={options}
                toggleText={capitalize(status)}
                className="ClientProblems-Dropdown Dropdown_theme_blue"
            />
            <List className="ClientProblemList">
                {isFetching ? (
                    <Loader />
                ) : (
                        <>
                            {ds.data.map((o, i) => (
                                <ListItem
                                    key={o.id}
                                    className="ClientProblemList-Item ClientProblem"
                                    style={(i % 2 === 0) ? { backgroundColor: '#f9f9f9' } : null}
                                >
                                    <div className="flex-1">
                                        <div
                                            onClick={() => onOpenViewer(o)}
                                            className="ClientProblemList-Link"
                                        >
                                            <Truncate lines={1}>
                                                {o.name}
                                            </Truncate>
                                        </div>
                                        <span className="ClientProblemList-ClientProblem">
                                            {o.code} {o.codeSet}
                                        </span>
                                    </div>
                                    {(o.resolvedDate || o.identifiedDate) && (
                                        <div className="ClientProblem-Date">
                                            <div>{formatDate(o.identifiedDate)}</div>
                                            <div>{formatDate(o.resolvedDate)}</div>
                                        </div>
                                    )}
                                </ListItem>
                            ))}

                            {ds.data.length === 0 && (
                                <div className="ClientProblemList-Fallback">No problems</div>
                            )}
                        </>
                    )}
            </List>

            {isViewerOpen && (
                <ProblemViewer
                    isOpen
                    clientId={clientId}
                    onClose={onCloseViewer}
                    problemId={selected?.id}
                />
            )}

            {error && (
                <ErrorViewer
                    isOpen
                    error={error}
                    onClose={actions.clearError}
                />
            )}
        </div>
    )
}

export default connect(mapStateToProps, mapDispatchToProps)(ClientProblems)