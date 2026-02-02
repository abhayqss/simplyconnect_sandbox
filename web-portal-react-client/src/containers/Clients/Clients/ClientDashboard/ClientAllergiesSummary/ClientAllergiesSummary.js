import React, {
    memo,
    useState,
    useEffect,
    useCallback
} from 'react'

import cn from 'classnames'

import { Badge } from 'reactstrap'

import Table from 'components/Table/Table'
import ErrorViewer from 'components/ErrorViewer/ErrorViewer'
import ClientSummaryFallback from '../ClientSummaryFallback/ClientSummaryFallback'

import { AllergyViewer } from 'containers/Clients/Clients/Allergies'

import { useAllergyList } from 'hooks/business/client/dashboard'

import { DateUtils as DU } from 'lib/utils/Utils'

import './ClientAllergiesSummary.scss'

const { format, formats } = DU

const DATE_FORMAT = formats.longDateMediumTime12

function ClientAllergiesSummary({ clientId, className }) {
    const [selected, setSelected] = useState(null)
    const [isViewerOpen, setIsViewerOpen] = useState(false)

    const {
        state: {
            error,
            isFetching,
            dataSource: ds
        },
        sort,
        fetch,
        clearError,
    } = useAllergyList({ clientId })

    const onSelect = o => {
        setSelected(o)
        setIsViewerOpen(true)
    }

    const onCloseViewer = useCallback(() => {
        setSelected(null)
        setIsViewerOpen(false)
    }, [])

    useEffect(() => { fetch() }, [fetch])

    return (
        <div className={cn('ClientAllergiesSummary', className)}>
            <div className='ClientAllergiesSummary-Title'>
                <span className='ClientAllergiesSummary-TitleText'>Allergies</span>

                <Badge color='info' className='ClientAllergiesSummary-AllergiesCount'>
                    {ds.pagination.totalCount}
                </Badge>
            </div>

            <ClientSummaryFallback isShown={!ds.pagination.totalCount} isLoading={isFetching}>
                <div className="ClientAllergiesSummary-Body">
                    <Table
                        hasPagination
                        keyField='id'
                        className='AllergyList'
                        containerClass='AllergyListContainer'
                        data={ds.data}
                        noDataText="No allergies"
                        pagination={ds.pagination}
                        onRefresh={fetch}
                        columns={[
                            {
                                dataField: 'substance',
                                text: 'Substance',
                                sort: true,
                                onSort: sort,
                                formatter: (v, row) => {
                                    return (
                                        <div
                                            onClick={() => onSelect(row)}
                                            className='ClientAllergiesSummary-Substance'>
                                            {v}
                                        </div>
                                    )
                                },
                            },
                            {
                                dataField: 'reaction',
                                text: 'Reaction',
                            },
                            {
                                dataField: 'identifiedDate',
                                text: 'Identified',
                                headerAlign: 'right',
                                align: 'right',
                                sort: true,
                                onSort: sort,
                                formatter: v => {
                                    return format(v, DATE_FORMAT)
                                }
                            }
                        ]}
                        columnsMobile={['substance', 'identifiedDate']}
                    />
                </div>
            </ClientSummaryFallback>

            {isViewerOpen && (
                <AllergyViewer
                    isOpen
                    clientId={clientId}
                    allergyId={selected?.id}
                    onClose={onCloseViewer}
                />
            )}

            {error && (
                <ErrorViewer
                    isOpen
                    error={error}
                    onClose={clearError}
                />
            )}
        </div>
    )
}

export default memo(ClientAllergiesSummary)