package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(ResPharmacyData.TABLE_NAME)
public class ResPharmacyData extends IdentifiableSourceEntity<Long> {

	public static final String TABLE_NAME = "Res_Pharmacy";
	public static final String ID_COLUMN = "Unique_ID";
	
	@Id
	@Column(ID_COLUMN)
	private long id;
	
	@Column("Res_Number")
	private Long resNumber;
	
	@Column("Pharmacy_ID")
	private Long pharmacyId;
	
	@Column("Rank")
	private Integer rank;

	@Override
	public Long getId() {
		return id;
	}

	public Long getResNumber() {
		return resNumber;
	}

	public void setResNumber(Long resNumber) {
		this.resNumber = resNumber;
	}

	public Long getPharmacyId() {
		return pharmacyId;
	}

	public void setPharmacyId(Long pharmacyId) {
		this.pharmacyId = pharmacyId;
	}

	public Integer getRank() {
		return rank;
	}

	public void setRank(Integer rank) {
		this.rank = rank;
	}

	public void setId(long id) {
		this.id = id;
	}
	
}
