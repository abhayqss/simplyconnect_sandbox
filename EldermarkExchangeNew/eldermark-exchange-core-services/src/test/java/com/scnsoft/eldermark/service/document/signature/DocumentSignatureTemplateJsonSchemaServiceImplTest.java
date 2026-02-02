package com.scnsoft.eldermark.service.document.signature;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplate;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateField;
import com.scnsoft.eldermark.entity.signature.DocumentSignatureTemplateFieldLocation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class DocumentSignatureTemplateJsonSchemaServiceImplTest {

    private final DocumentSignatureTemplateJsonSchemaService service = new DocumentSignatureTemplateJsonSchemaServiceImpl(
            new ObjectMapper(),
            List.of()
    );

    @Test
    void testJsonGeneration() {

        // Given
        var template = new DocumentSignatureTemplate();
        var field1 = new DocumentSignatureTemplateField();
        var location1 = new DocumentSignatureTemplateFieldLocation();
        location1.setTopLeftX((short) 0);
        location1.setTopLeftY((short) 1);
        location1.setPageNo((short) 1);
        field1.setName("field1");
        field1.setJsonSchema("{\"someJsonSchemaField\":\"value1\"}");
        field1.setJsonUiSchema("{\"someUiJsonSchemaField\":\"value1\"}");
        field1.setLocations(List.of(location1));

        var field2 = new DocumentSignatureTemplateField();
        var location2 = new DocumentSignatureTemplateFieldLocation();
        location2.setTopLeftX((short) 2);
        location2.setTopLeftY((short) 0);
        location2.setPageNo((short) 1);
        field2.setName("field2");
        field2.setJsonSchema("{\"someJsonSchemaField\":\"value2\"}");
        field2.setJsonUiSchema("{\"someUiJsonSchemaField\":\"value2\"}");
        field2.setLocations(List.of(location2));

        var field3 = new DocumentSignatureTemplateField();
        var location3 = new DocumentSignatureTemplateFieldLocation();
        location3.setTopLeftX((short) 1);
        location3.setTopLeftY((short) 0);
        location3.setPageNo((short) 1);
        field3.setName("field3");
        field3.setJsonSchema("{\"someJsonSchemaField\":\"value3\"}");
        field3.setJsonUiSchema("{\"someUiJsonSchemaField\":\"value3\"}");
        field3.setLocations(List.of(location3));

        template.setFields(List.of(field1, field2, field3));

        // When
        service.fillJsonSchemasGeneratedByTemplateFields(template);

        // Then
        assertNotNull(template.getFormSchema());
        var jsonSchemaContext = JsonPath.parse(template.getFormSchema());
        assertEquals("object", jsonSchemaContext.read("$.type"));
        assertEquals("value1", jsonSchemaContext.read("$.properties.field1.someJsonSchemaField"));
        assertEquals("value2", jsonSchemaContext.read("$.properties.field2.someJsonSchemaField"));
        assertEquals("value3", jsonSchemaContext.read("$.properties.field3.someJsonSchemaField"));

        assertNotNull(template.getFormUiSchema());
        var jsonUiSchemaContext = JsonPath.parse(template.getFormUiSchema());
        assertEquals("value1", jsonUiSchemaContext.read("$.field1.someUiJsonSchemaField"));
        assertEquals("value2", jsonUiSchemaContext.read("$.field2.someUiJsonSchemaField"));
        assertEquals("value3", jsonUiSchemaContext.read("$.field3.someUiJsonSchemaField"));
        assertEquals(2, jsonUiSchemaContext.read("$.ui:grid.length()", Integer.class));
        assertEquals(6, jsonUiSchemaContext.read("$.ui:grid[0].field3.md", Integer.class));
        assertEquals(6, jsonUiSchemaContext.read("$.ui:grid[0].field2.md", Integer.class));
        assertEquals(6, jsonUiSchemaContext.read("$.ui:grid[1].field1.md", Integer.class));
    }
}
