package com.scnsoft.eldermark.entity;


import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "MPI_log")
public class MpiLog implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "last_index_updated")
	private Date lastIndexUpdated;

	@Column(name = "last_matched")
	private Date lastMatched;

	public Date getLastIndexUpdated() {
		return lastIndexUpdated;
	}

	public void setLastIndexUpdated(Date lastIndexUpdated) {
		this.lastIndexUpdated = lastIndexUpdated;
	}

	public Date getLastMatched() {
		return lastMatched;
	}

	public void setLastMatched(Date lastMatched) {
		this.lastMatched = lastMatched;
	}
}