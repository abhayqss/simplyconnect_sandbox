import React, {
    useRef,
    useEffect
} from 'react'

import $ from 'jquery'
import DocumentTitle from 'react-document-title'

import { connect } from 'react-redux'
import { bindActionCreators } from 'redux'

import { useParams } from 'react-router-dom'

import useSideBarUpdate from 'hooks/common/redux/useSideBarUpdate'

import Loader from 'components/Loader/Loader'

import detailsActions from 'redux/client/document/details/clientDocumentDetailsActions'

import { toNumberExcept } from 'lib/utils/Utils'

import './CcdDocumentDetails.scss'

function mapStateToProps(state) {
    return { state: state.client.document.details }
}

function mapDispatchToProps(dispatch) {
    return {
        actions: bindActionCreators(detailsActions, dispatch)
    }
}

function CcdDocumentDetails({ state, actions }) {
    const {
        data,
        isFetching
    } = state

    const params = useParams()

    const clientId = toNumberExcept(
        params.clientId, [null, undefined]
    )

    const {
        documentId,
        documentTitle
    } = params

    const containerRef = useRef()

    const update = useSideBarUpdate()

    useEffect(() => {
        update({ isNo: true })
        actions.load({ clientId, documentId })
    }, [])

    useEffect(() => {
        if (data) {
            const iframe = document.createElement('iframe')

            iframe.name = 'document'
            iframe.width = '100%'
            iframe.height = '100%'
            iframe.allowFullscreen = true
            iframe.className = 'CcdDocumentDetails-Frame'

            $(containerRef.current).append(iframe)

            const doc = $(iframe).contents()[0]

            doc.write(data)
            doc.close()
        }
    }, [data])

    return (
        <DocumentTitle title={documentTitle ?? 'Document'}>
            <div className="CcdDocumentDetails">
                {isFetching ? (
                    <Loader isCentered/>
                ) : (
                    <div
                        ref={containerRef}
                        className="CcdDocumentDetails-FrameContainer"
                    />
                )}
            </div>
        </DocumentTitle>
    )
}

export default connect(mapStateToProps, mapDispatchToProps)(CcdDocumentDetails)