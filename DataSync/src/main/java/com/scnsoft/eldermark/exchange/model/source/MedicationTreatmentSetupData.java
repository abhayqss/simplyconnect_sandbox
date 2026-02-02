package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(MedicationTreatmentSetupData.TABLE_NAME)
public class MedicationTreatmentSetupData extends IdentifiableSourceEntity<Long> {
	
	public static final String TABLE_NAME = "Medication_Treatment_Setup";
	public static final String ID_COLUMN = "Unique_ID";
	
	@Id
	@Column(ID_COLUMN)
	private long id;
	
	@Column("Name")
	private String name;
	
	@Column("Side_Effects")
	private String sideEffects;

	@Override
	public Long getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSideEffects() {
		return sideEffects;
	}

	public void setSideEffects(String sideEffects) {
		this.sideEffects = sideEffects;
	}

	public void setId(long id) {
		this.id = id;
	}
	
}
