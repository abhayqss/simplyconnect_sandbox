package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(CommunicationTypeData.TABLE_NAME)
public class CommunicationTypeData extends IdentifiableSourceEntity<String> {
    public static final String TABLE_NAME = "communication_types";
    public static final String CODE = "Code";

    @Id
    @Column(CODE)
    private String code;

    @Column("Name")
    private String name;

    @Column("Type_Name")
    private String typeName;

    @Column("Inactive")
    private Boolean inactive;

    @Column("Type_Code")
    private String typeCode;

    @Override
    public String getId() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public Boolean getInactive() {
        return inactive;
    }

    public void setInactive(Boolean inactive) {
        this.inactive = inactive;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }
}
