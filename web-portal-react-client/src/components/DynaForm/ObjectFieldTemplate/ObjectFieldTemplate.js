import React from 'react'

import { Row, Col } from 'reactstrap'

function ObjectFieldTemplate(props) {
    const {
        title,
        required,
        idSchema,
        uiSchema,
        properties,
        description,
        formContext,
        TitleField,
        DescriptionField
    } = props

    let body = properties.map(
        prop => prop.content
    )

    if (uiSchema["ui:grid"]) {
        body = uiSchema["ui:grid"].map((row, i) => (
            <Row key={i}>
                {Object.entries(row).map(([name, opts]) => {
                    const prop = properties.find(
                        o => o.name === name
                    )

                    return (
                        <Col {...opts} key={name}>
                            {prop?.content ?? ''}
                        </Col>
                    )
                })}
            </Row>
        ))
    }

    return (
        <fieldset id={idSchema.$id}>
            {(uiSchema["ui:title"] || title) && (
                <TitleField
                    id={`${idSchema.$id}__title`}
                    title={title || uiSchema["ui:title"]}
                    required={required}
                    formContext={formContext}
                />
            )}
            {description && (
                <DescriptionField
                    id={`${idSchema.$id}__description`}
                    description={description}
                    formContext={formContext}
                />
            )}
            {body}
        </fieldset>
    );
}

export default ObjectFieldTemplate