package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(MedicalProfessionalRoleData.TABLE_NAME)
public class MedicalProfessionalRoleData extends IdentifiableSourceEntity<Long> {
	
	public static final String TABLE_NAME = "Medical_Professional_Role";
	public static final String ID_COLUMN = "Med_Professional_Role";

	@Id
	@Column(ID_COLUMN)
	private long id;
	
	@Column("Description")
	private String description;
	
	@Column("Inactive")
	private Boolean inactive;

	@Override
	public Long getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Boolean getInactive() {
		return inactive;
	}

	public void setInactive(Boolean inactive) {
		this.inactive = inactive;
	}

	public void setId(long id) {
		this.id = id;
	}
	
}
