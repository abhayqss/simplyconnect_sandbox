import React, { useState } from 'react'

import service from 'services/DirectoryService'

import Table from 'components/Table/Table'
import { SearchField } from 'components/Form'

import './ClientSearchField.scss'

const params = {}

const options = {
    doLoad: () => service.findOrganizations(),
    textProp: 'label'
}

function ClientSearchField() {
    let [value, setValue] = useState(null)

    let onChange = (_, value) => setValue(value)

    return (
        <SearchField
            name="search"
            label="Clients*"
            value={value}
            onChange={onChange}
            options={options}
            params={params}
        >
            {({ dataSource, value, refresh, onPickItem }) => (
                <Table
                    hasPagination
                    keyField="id"
                    isLoading={dataSource.isFetching}
                    className="ClientSearchList"
                    containerClass="ClientSearchListContainer"
                    data={dataSource.data}
                    pagination={dataSource.pagination}
                    columns={[
                        {
                            dataField: 'id',
                            text: 'ID',
                        },
                        {
                            dataField: 'label',
                            text: 'Organization name',
                        },
                    ]}
                    onRefresh={refresh}
                />
            )}
        </SearchField>
    )
}

export default ClientSearchField
