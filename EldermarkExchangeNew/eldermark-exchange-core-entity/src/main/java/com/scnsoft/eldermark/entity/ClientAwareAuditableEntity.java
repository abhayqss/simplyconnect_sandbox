package com.scnsoft.eldermark.entity;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.scnsoft.eldermark.entity.basic.AuditableEntity;

@MappedSuperclass
public class ClientAwareAuditableEntity extends AuditableEntity {

	@JoinColumn(name = "resident_id", referencedColumnName = "id", nullable = false)
	@ManyToOne
	private Client client;

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}

}
