package com.scnsoft.eldermark.entity.note;

import com.scnsoft.eldermark.entity.Client;
import com.scnsoft.eldermark.entity.Employee;
import com.scnsoft.eldermark.entity.basic.AuditableEntity;
import com.scnsoft.eldermark.entity.document.facesheet.AdmittanceHistory;
import com.scnsoft.eldermark.entity.event.Event;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "Note")
@Inheritance(strategy = InheritanceType.JOINED)
public class Note extends AuditableEntity implements Serializable {

    private static final long serialVersionUID = 6586472382916866200L;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private NoteType type;

    @JoinColumn(name = "employee_id", referencedColumnName = "id", nullable = false)
    @OneToOne(optional = false, fetch = FetchType.LAZY)
    private Employee employee;

    @Column(name = "employee_id", nullable = false, insertable = false, updatable = false)
    private Long employeeId;

    @JoinColumn(name = "resident_id", referencedColumnName = "id")
    @OneToOne(fetch = FetchType.LAZY)
    private Client client;

    @Column(name = "resident_id", insertable = false, updatable = false)
    private Long clientId;

    @JoinColumn(name = "event_id", referencedColumnName = "id")
    @ManyToOne
    private Event event;

    @Column(name = "subjective")
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

    @Column(name = "intake_date")
    private Instant intakeDate;

    @JoinColumn(name = "resident_admittance_history_id")
    @ManyToOne
    private AdmittanceHistory admittanceHistory;

    @Column(name = "note_date")
    private Instant noteDate;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "Note_Resident", joinColumns = @JoinColumn(name = "note_id", nullable = false), inverseJoinColumns = @JoinColumn(name = "resident_id", nullable = false))
    private List<Client> noteClients;

    @ElementCollection
    @CollectionTable(name="Note_Resident",
            joinColumns=@JoinColumn(name="note_id"))
    @Column(name="resident_id", insertable = false, updatable = false)
    private Set<Long> noteClientIds;

    @Column(name = "note_name")
    private String noteName;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clinician_completing_encounter_id")
    private Employee clinicianCompletingEncounter;

    @Column(name = "other_clinician_completing_encounter")
    private String otherClinicianCompletingEncounter;

    @Column(name = "encounter_date", nullable = false)
    private Instant encounterDate;

    @Column(name = "encounter_time_from", nullable = false)
    private Instant encounterFromTime;

    @Column(name = "encounter_time_to", nullable = false)
    private Instant encounterToTime;

    public NoteType getType() {
        return type;
    }

    public void setType(NoteType type) {
        this.type = type;
    }

    public Employee getEmployee() {
        return employee;
    }

    public void setEmployee(Employee employee) {
        this.employee = employee;
    }

    public Long getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Long employeeId) {
        this.employeeId = employeeId;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
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

    public Instant getIntakeDate() {
        return intakeDate;
    }

    public void setIntakeDate(Instant intakeDate) {
        this.intakeDate = intakeDate;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public AdmittanceHistory getAdmittanceHistory() {
        return admittanceHistory;
    }

    public void setAdmittanceHistory(AdmittanceHistory admittanceHistory) {
        this.admittanceHistory = admittanceHistory;
    }

    public Instant getNoteDate() {
        return noteDate;
    }

    public void setNoteDate(Instant noteDate) {
        this.noteDate = noteDate;
    }

    public List<Client> getNoteClients() {
        if (noteClients == null) {
            return Collections.singletonList(getClient());
        }
        return noteClients;
    }

    public void setNoteClients(List<Client> noteClients) {
        this.noteClients = noteClients;
    }

    public String getNoteName() {
        return noteName;
    }

    public void setNoteName(String noteName) {
        this.noteName = noteName;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public void setNoteClientIds(Set<Long> noteClientIds) {
        this.noteClientIds = noteClientIds;
    }

    public Set<Long> getNoteClientIds() {
        if (noteClientIds == null) {
            return Collections.singleton(getClientId());
        }
        return noteClientIds;
    }

    public Employee getClinicianCompletingEncounter() {
        return clinicianCompletingEncounter;
    }

    public void setClinicianCompletingEncounter(Employee clinicianCompletingEncounter) {
        this.clinicianCompletingEncounter = clinicianCompletingEncounter;
    }

    public String getOtherClinicianCompletingEncounter() {
        return otherClinicianCompletingEncounter;
    }

    public void setOtherClinicianCompletingEncounter(String otherClinicianCompletingEncounter) {
        this.otherClinicianCompletingEncounter = otherClinicianCompletingEncounter;
    }

    public Instant getEncounterDate() {
        return encounterDate;
    }

    public void setEncounterDate(Instant encounterDate) {
        this.encounterDate = encounterDate;
    }

    public Instant getEncounterFromTime() {
        return encounterFromTime;
    }

    public void setEncounterFromTime(Instant encounterFromTime) {
        this.encounterFromTime = encounterFromTime;
    }

    public Instant getEncounterToTime() {
        return encounterToTime;
    }

    public void setEncounterToTime(Instant encounterToTime) {
        this.encounterToTime = encounterToTime;
    }
}
