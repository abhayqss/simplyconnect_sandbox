import React, { PureComponent } from 'react'

import PropTypes from 'prop-types'

import { noop }  from 'underscore'

import { connect } from 'react-redux'
import { bindActionCreators, compose } from 'redux'

import widthDirectoryData from 'hocs/withDirectoryData'

import * as careTeamMemberFormActions from 'redux/care/team/member/form/careTeamMemberFormActions'

import { isEmpty } from 'lib/utils/Utils'

import {
    NOTIFICATION_CHANNELS_TYPES,
    NOTIFICATION_RESPONSIBILITY_TYPES
} from 'lib/Constants'

import {
    TChannel,
    TEventTypes,
    TResponsibility
} from './types'

import { NotificationPreference } from '../'

const { List } = require('immutable')

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(careTeamMemberFormActions, dispatch)
    }
}

const { VIEWABLE, NOT_VIEWABLE } = NOTIFICATION_RESPONSIBILITY_TYPES
const { EMAIL, PUSH_NOTIFICATION } = NOTIFICATION_CHANNELS_TYPES

class NotificationPreferencesSection extends PureComponent {
    static propTypes = {
        title: PropTypes.string,
        eventGroup: PropTypes.arrayOf(TEventTypes).isRequired,
        channels: PropTypes.arrayOf(TChannel).isRequired,
        responsibilities: PropTypes.arrayOf(TResponsibility).isRequired,
        preferences: PropTypes.instanceOf(List).isRequired,
        onChangeResponsibility: PropTypes.func,
        onChangeChannels: PropTypes.func
    }

    static defaultProps = {
        title: '',
        onChangeResponsibility: noop,
        onChangeChannels: noop
    }

    onChangeResponsibility = async (eventTypeId, value) => {
        this.modifyChannelIfNeeded(eventTypeId ,value)
        this.onChangePreference('responsibilityName', eventTypeId, value)
        this.props.onChangeResponsibility(eventTypeId, value)
    }

    onChangeChannel = async (id, value) => {
        await this.onChangePreference('channels', id, value)
        this.props.onChangeChannels(id, value)
    }

    onChangePreference = async (property, eventTypeId, value) => {
        await this.actions.changeNotificationPreference(
            property, eventTypeId, value
        )
    }

    get actions() {
        return this.props.actions
    }

    getDirectoryData() {
        return this.props.getDirectoryData({
            notificationPreferences: ['care', 'team', 'notification', 'preference'],
        })
    }

    modifyChannelIfNeeded(id, respName) {
        if ([VIEWABLE, NOT_VIEWABLE].includes(respName)) {
            this.onChangeChannel(id, [])
        }

        else if (isEmpty(this.getData(id).channels)) {
            this.onChangeChannel(id, [EMAIL, PUSH_NOTIFICATION])
        }
    }

    getData(id) {
        const byEventId = np => np.eventTypeId === id

        let data = this.props.preferences.find(byEventId)

        if (data == null) {
            const { notificationPreferences } = this.getDirectoryData()

            data = notificationPreferences.find(byEventId)
        }

        return data || {}
    }

    getErrors(data) {
        const index = this.props.preferences.findIndex(
            np => np === data
        )

        return this.props.errors[index] || {}
    }

    render() {
        const { eventGroup, responsibilities, channels, title } = this.props

        return (
            <div className="CareTeamMemberForm-Section">
                <div className='CareTeamMemberForm-SectionTitle'>
                    {title}
                </div>

                {eventGroup.map(eventType => {
                    const data = this.getData(eventType.id)
                    const errors = this.getErrors(data)

                    return (
                        <NotificationPreference
                            key={eventType.id}
                            data={data}
                            errors={errors}
                            channels={channels}
                            name={eventType.name}
                            title={eventType.title}
                            isDisabledResp={!data.canEdit}
                            responsibilities={responsibilities}
                            onChangeChannel={this.onChangeChannel}
                            onChangeResponsibility={this.onChangeResponsibility}
                        />
                    )
                })}
            </div>
        )
    }
}

export default compose(
    connect(null, mapDispatchToProps),
    widthDirectoryData,
)(NotificationPreferencesSection)
