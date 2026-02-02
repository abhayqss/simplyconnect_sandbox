package com.scnsoft.eldermark.entity.client;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.basic.LegacyIdAwareEntity;

@Entity
@Table(name = "ResidentHealthPlan")
@Access(AccessType.FIELD)

public class ClientHealthPlan extends LegacyIdAwareEntity {

	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name = "resident_id")
	public Client client;

	@Column(name = "plan_name")
	private String healthPlanName;

	@Column(name = "plan_policy_number")
	private String policyNumber;

	@Column(name = "plan_group_number")
	private String groupNumber;

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

	public String getHealthPlanName() {
		return healthPlanName;
	}

	public void setHealthPlanName(String healthPlanName) {
		this.healthPlanName = healthPlanName;
	}

	public String getPolicyNumber() {
		return policyNumber;
	}

	public void setPolicyNumber(String policyNumber) {
		this.policyNumber = policyNumber;
	}

	public String getGroupNumber() {
		return groupNumber;
	}

	public void setGroupNumber(String groupNumber) {
		this.groupNumber = groupNumber;
	}
}
