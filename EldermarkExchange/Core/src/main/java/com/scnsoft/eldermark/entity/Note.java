package com.scnsoft.eldermark.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Entity
@Inheritance(strategy= InheritanceType.JOINED)
@Table(name = "Note")
public class Note implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "chain_id")
    private Long chainId;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NoteType type;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private NoteStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "last_modified_date", nullable = false)
    private Date lastModifiedDate;

    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    @OneToOne(optional = false)
    private Employee employee;

    @JoinColumn(name = "resident_id", referencedColumnName = "id", nullable = false)
    @OneToOne(optional = false)
    private Resident resident;

    @JoinColumn(name = "event_id", referencedColumnName = "id")
    @OneToOne
    private Event event;

    @Column(name = "archived", nullable = false)
    private Boolean archived;

    @Column(name="subjective")
    private String subjective;

    @Column(name = "objective")
    private String objective;

    @Column(name = "assessment")
    private String assessment;

    @Column(name = "note_plan")
    private String plan;

    @JoinColumn(name = "note_sub_type_id", referencedColumnName = "id")
    @ManyToOne
    private NoteSubType subType;

    @JoinColumn(name = "resident_admittance_history_id")
    @ManyToOne
    private AdmittanceHistory admittanceHistory;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "intake_date")
    private Date intakeDate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Note_Resident", joinColumns = @JoinColumn(name = "note_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "resident_id", nullable = false))
    private List<Resident> noteResidents;

    @Column(name = "note_name")
    private String noteName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getChainId() {
        return chainId;
    }

    public void setChainId(Long chainId) {
        this.chainId = chainId;
    }

    public NoteType getType() {
        return type;
    }

    public void setType(NoteType type) {
        this.type = type;
    }

    public NoteStatus getStatus() {
        return status;
    }

    public void setStatus(NoteStatus status) {
        this.status = status;
    }

    public Date getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(Date lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Resident getResident() {
        return resident;
    }

    public void setResident(Resident resident) {
        this.resident = resident;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Boolean getArchived() {
        return archived;
    }

    public void setArchived(Boolean archived) {
        this.archived = archived;
    }

    public String getSubjective() {
        return subjective;
    }

    public void setSubjective(String subjective) {
        this.subjective = subjective;
    }

    public String getObjective() {
        return objective;
    }

    public void setObjective(String objective) {
        this.objective = objective;
    }

    public String getAssessment() {
        return assessment;
    }

    public void setAssessment(String assessment) {
        this.assessment = assessment;
    }

    public String getPlan() {
        return plan;
    }

    public void setPlan(String plan) {
        this.plan = plan;
    }

    public NoteSubType getSubType() {
        return subType;
    }

    public void setSubType(NoteSubType noteSubType) {
        this.subType = noteSubType;
    }

    public AdmittanceHistory getAdmittanceHistory() {
        return admittanceHistory;
    }

    public void setAdmittanceHistory(AdmittanceHistory admittanceHistory) {
        this.admittanceHistory = admittanceHistory;
    }

    public Date getIntakeDate() {
        return intakeDate;
    }

    public void setIntakeDate(Date intakeDate) {
        this.intakeDate = intakeDate;
    }

    public List<Resident> getNoteResidents() {
        return noteResidents;
    }

    public void setNoteResidents(List<Resident> noteResidents) {
        this.noteResidents = noteResidents;
    }

    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }
}
