package com.scnsoft.eldermark.entity;


import javax.persistence.*;
import java.io.Serializable;

/**
 * A table storing information about merged patient records. In terms of OpenXDS merging functionality, when merging,
 * there is always Main and Secondary record.
 */
@Entity
@Table(name = "MPI_merged_residents")
public class MpiMergedResidents implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	/**
	 * Main record
	 */
	@JoinColumn(name = "surviving_resident_id", referencedColumnName = "id", insertable = true, updatable = true, nullable = false)
	@ManyToOne
	private Resident survivingResident;

	/**
	 * Secondary record
	 */
	@JoinColumn(name = "merged_resident_id", referencedColumnName = "id", insertable = true, updatable = true, nullable = false)
	@ManyToOne
	private Resident mergedResident;

	@JoinColumn(name = "surviving_resident_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
	@ManyToOne
	private CareCoordinationResident survivingCCResident;

	@JoinColumn(name = "merged_resident_id", referencedColumnName = "id", insertable = false, updatable = false, nullable = false)
	@ManyToOne
	private CareCoordinationResident mergedCCResident;

	@Column(name = "surviving_resident_id", insertable = false, updatable = false, nullable = false)
	private long survivingResidentId;

	@Column(name = "merged_resident_id", insertable = false, updatable = false, nullable = false)
	private long mergedResidentId;

	/**
	 * true, if records were marked as "matching" (automatically or manually).
	 */
	// it means "surely matched", not merged
	@Column(name = "merged")
	private Boolean merged;

	/**
	 * true, if records were automatically marked as "maybe matching".
	 */
	@Column(name = "probably_matched")
	private Boolean probablyMatched;

	/**
	 * true, if records were <b>automatically</b> merged previously.
	 */
	@Column(name = "merged_automatically")
	private Boolean mergedAutomatically;

	/**
	 * true, if records were <b>manually</b> merged previously.
	 */
	@Column(name = "merged_manually", nullable = false)
	private Boolean mergedManually;

	/**
	 * A confidence score based on the similarity between records
	 */
	@Column(name = "duke_confidence")
	private Double dukeConfidence;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Resident getSurvivingResident() {
		return survivingResident;
	}

	public void setSurvivingResident(Resident survivingResident) {
		this.survivingResident = survivingResident;
	}

	public Resident getMergedResident() {
		return mergedResident;
	}

	public void setMergedResident(Resident mergedResident) {
		this.mergedResident = mergedResident;
	}

	public boolean isMerged() {
		return Boolean.TRUE.equals(merged);
	}

	public void setMerged(Boolean merged) {
		this.merged = merged;
	}

	public boolean isProbablyMatched() {
		return Boolean.TRUE.equals(probablyMatched);
	}

	public void setProbablyMatched(Boolean probablyMatched) {
		this.probablyMatched = probablyMatched;
	}

	public CareCoordinationResident getSurvivingCCResident() {
		return survivingCCResident;
	}

	private void setSurvivingCCResident(CareCoordinationResident survivingCCResident) {
		this.survivingCCResident = survivingCCResident;
	}

	public CareCoordinationResident getMergedCCResident() {
		return mergedCCResident;
	}

    private void setMergedCCResident(CareCoordinationResident mergedCCResident) {
		this.mergedCCResident = mergedCCResident;
	}

	public boolean isMergedAutomatically() {
		return Boolean.TRUE.equals(mergedAutomatically);
	}

	public void setMergedAutomatically(Boolean mergedAutomatically) {
		this.mergedAutomatically = mergedAutomatically;
	}

	public boolean isMergedManually() {
		return Boolean.TRUE.equals(mergedManually);
	}

	public void setMergedManually(Boolean mergedManually) {
		this.mergedManually = mergedManually;
	}

	public Double getDukeConfidence() {
		return dukeConfidence;
	}

	public void setDukeConfidence(Double dukeConfidence) {
		this.dukeConfidence = dukeConfidence;
	}

	public long getSurvivingResidentId() {
		return survivingResidentId;
	}

	public void setSurvivingResidentId(long survivingResidentId) {
		this.survivingResidentId = survivingResidentId;
	}

	public long getMergedResidentId() {
		return mergedResidentId;
	}

	public void setMergedResidentId(long mergedResidentId) {
		this.mergedResidentId = mergedResidentId;
	}

}