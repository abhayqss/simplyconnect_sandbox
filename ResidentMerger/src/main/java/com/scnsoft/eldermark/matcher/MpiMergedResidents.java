package com.scnsoft.eldermark.matcher;

/**
 * A table storing information about merged patient records. In terms of OpenXDS merging functionality, when merging,
 * there is always Main and Secondary record.
 */


public class MpiMergedResidents {
	private Long id;
	private long survivingResidentId;
	private long mergedResidentId;
	private Boolean merged;
	private Boolean probablyMatched;
	private Boolean mergedAutomatically;
	private Boolean mergedManually;
	private Double dukeConfidence;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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

	public Boolean isMerged() {
		return merged;
	}

	public void setMerged(Boolean merged) {
		this.merged = merged;
	}

	public Boolean isProbablyMatched() {
		return probablyMatched;
	}

	public void setProbablyMatched(Boolean probablyMatched) {
		this.probablyMatched = probablyMatched;
	}

	public Boolean isMergedAutomatically() {
		return mergedAutomatically;
	}

	public void setMergedAutomatically(Boolean mergedAutomatically) {
		this.mergedAutomatically = mergedAutomatically;
	}

	public Boolean isMergedManually() {
		return mergedManually;
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
}