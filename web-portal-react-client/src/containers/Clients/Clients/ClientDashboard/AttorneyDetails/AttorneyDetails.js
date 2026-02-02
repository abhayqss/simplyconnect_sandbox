import React, { useCallback } from 'react'

import { connect } from 'react-redux'

import {
    ListGroup as List,
    ListGroupItem as ListItem
} from 'reactstrap'

import { 
    useAttorneyTypesQuery
} from 'hooks/business/directory/query'

import Avatar from 'containers/Avatar/Avatar'

import { map } from 'underscore'

import { getAddress } from 'lib/utils/Utils'

import './AttorneyDetails.scss'

function mapStateToProps(state) {
    return { data: state.client.details.data.attorneys }
}

function AttorneyDetails({ data }) {
    const { 
        data: types
    } = useAttorneyTypesQuery()

    const getTypeTitles = useCallback(t => (
        types
            ?.filter(o => t?.includes(o.name))
            ?.map(o => o.title)
            ?.join(', ')
    ), [types])

    return (
        <List className="AttorneyList">
            {map(data, o => (
                <ListItem
                    key={o.id}
                    className="AttorneyList-Item Attorney"
                >
                    <div className="margin-right-15">
                        <Avatar
                            name={`${o.firstName} ${o.lastName}`}
                            id={o.avatarId}
                            className="Attorney-Avatar"
                        />
                    </div>

                    <div className="Attorney-Details">
                        <div className="Attorney-FullName">{`${o.firstName} ${o.lastName}`}</div>
                        {!!o.types.length && <div className="Attorney-Detail">{getTypeTitles(o.types)}</div>}
                        {o.email && <div className="Attorney-Detail">{o.email}</div>}
                        {o.phone && <div className="Attorney-Detail">{o.phone}</div>}
                        <div className="Attorney-Detail">{
                            getAddress({ ...o, zip: o.zipCode, state: o.stateTitle }, ', ')}
                        </div>
                    </div>
                </ListItem>
            ))}
        </List>
    )
}

export default connect(mapStateToProps)(AttorneyDetails)
