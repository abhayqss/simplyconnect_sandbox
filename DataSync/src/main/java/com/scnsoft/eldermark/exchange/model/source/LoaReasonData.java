package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

@Table(LoaReasonData.TABLE_NAME)
public class LoaReasonData extends IdentifiableSourceEntity<Long> {
	
	public static final String TABLE_NAME = "LOA_Reason";
	public static final String ID_COLUMN = "ID";

	@Id
	@Column(ID_COLUMN)
	private long id;

	@Column("Reason_Type_Code")
	private String reasonTypeCode;
	
	@Column("Description")
	private String description;
	
	@Column("Inactive")
	private Boolean inactive;

	@Override
	public Long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getReasonTypeCode() {
		return reasonTypeCode;
	}

	public void setReasonTypeCode(String reasonTypeCode) {
		this.reasonTypeCode = reasonTypeCode;
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

}
