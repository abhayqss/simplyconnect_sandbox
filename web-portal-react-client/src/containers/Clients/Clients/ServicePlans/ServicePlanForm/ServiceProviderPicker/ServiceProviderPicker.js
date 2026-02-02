import React, {
    useState,
    useEffect,
    useCallback
} from 'react'

import PTypes from 'prop-types'

import { noop } from 'underscore'

import { useHistory } from 'react-router-dom'

import Modal from 'components/Modal/Modal'

import Communities from 'containers/Marketplace/Private/Communities/Communities'
import CommunityDetails from 'containers/Marketplace/Private/Communities/CommunityDetails/CommunityDetails'

import Store from 'lib/stores/Store'

import './ServiceProviderPicker.scss'

const store = new Store()

export default function ServiceProviderPicker({
    isOpen,
    clientId,
    domainId,
    programSubTypeId,

    onPick,
    onClose
}) {
    const [selected, setSelected] = useState(null)
    const [isDetailsOpen, setIsDetailsOpen] = useState(false)
    const [shouldCancelClient, setShouldCancelClient] = useState(false)

    const history = useHistory()

    const onSelect = useCallback((community, shouldCancelClient = false) => {
        setShouldCancelClient(shouldCancelClient)
        setSelected(community)
        setIsDetailsOpen(true)
    }, [])

    const onBack = useCallback(() => {
        setIsDetailsOpen(false)
    }, [])

    const {
        communityId,
        communityName
    } = selected || {}

    useEffect(() => () => {
        store.clear('CLIENT_COMMUNITY_FILTER')
    }, [])

    return (
        <Modal
            isOpen={isOpen}
            onClose={onClose}
            hasFooter={false}
            className="ServiceProviderPicker"
            title={isDetailsOpen ? communityName : 'Marketplace'}
        >
            {isDetailsOpen ? (
                <CommunityDetails
                    clientId={clientId}
                    history={history}
                    communityId={communityId}
                    communityName={communityName}
                    programSubTypeId={programSubTypeId}
                    shouldCancelClient={shouldCancelClient}
                    onBack={onBack}
                    onChoose={onPick}
                />
            ) : (
                <Communities
                    clientId={clientId}
                    domainId={domainId}
                    history={history}
                    programSubTypeId={programSubTypeId}
                    shouldCancelClient={shouldCancelClient}
                    onSelect={onSelect}
                />
            )}
        </Modal>
    )
}

ServiceProviderPicker.propTypes = {
    isOpen: PTypes.bool,
    clientId: PTypes.number,
    domainId: PTypes.number,
    programSubTypeId: PTypes.number,

    onPick: PTypes.func,
    onClose: PTypes.func,
}

ServiceProviderPicker.defaultProps = {
    isOpen: false,

    onPick: noop,
    onClose: noop
}

