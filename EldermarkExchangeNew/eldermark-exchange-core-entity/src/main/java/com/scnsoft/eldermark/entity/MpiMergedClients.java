package com.scnsoft.eldermark.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

/**
 * A table storing information about merged patient records. In terms of OpenXDS
 * merging functionality, when merging, there is always Main and Secondary
 * record.
 */
@Entity
@Table(name = "MPI_merged_residents")
public class MpiMergedClients implements Serializable {
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Main record
     */
    @JoinColumn(name = "surviving_resident_id", referencedColumnName = "id", insertable = true, updatable = true, nullable = false)
    @ManyToOne
    private Client survivingClient;

    /**
     * Secondary record
     */
    @JoinColumn(name = "merged_resident_id", referencedColumnName = "id", insertable = true, updatable = true, nullable = false)
    @ManyToOne
    private Client mergedClient;

    @Column(name = "surviving_resident_id", insertable = false, updatable = false, nullable = false)
    private long survivingClientId;

    @Column(name = "merged_resident_id", insertable = false, updatable = false, nullable = false)
    private long mergedClientId;

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

	public boolean isProbablyMatched() {
		return Boolean.TRUE.equals(probablyMatched);
	}

	public void setProbablyMatched(Boolean probablyMatched) {
		this.probablyMatched = probablyMatched;
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

    public Client getSurvivingClient() {
        return survivingClient;
    }

    public void setSurvivingClient(Client survivingClient) {
        this.survivingClient = survivingClient;
    }

    public Client getMergedClient() {
        return mergedClient;
    }

    public void setMergedClient(Client mergedClient) {
        this.mergedClient = mergedClient;
    }

    public long getSurvivingClientId() {
        return survivingClientId;
    }

    public void setSurvivingClientId(long survivingClientId) {
        this.survivingClientId = survivingClientId;
    }

    public long getMergedClientId() {
        return mergedClientId;
    }

    public void setMergedClientId(long mergedClientId) {
        this.mergedClientId = mergedClientId;
    }

    public boolean isMerged() {
        return Boolean.TRUE.equals(merged);
    }

    public void setMerged(Boolean merged) {
        this.merged = merged;
    }

}