package com.scnsoft.eldermark.entity;

import com.scnsoft.eldermark.entity.event.Event;
import com.scnsoft.eldermark.entity.note.Note;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Immutable;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Entity
@Table(name = "EventOrNote")
@Immutable
public class EventNote implements Serializable {

    @Id
    @Column(name = "item_id", insertable = false, updatable = false)
    private String itemId;

    @Column(name = "numeric_id", insertable = false, updatable = false)
    private Long numericId;

    @Column(name = "item_type_id", insertable = false, updatable = false, columnDefinition = "int")
    private Long itemTypeId;

    @JoinColumn(name = "resident_id", referencedColumnName = "id", insertable = false, updatable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Client client;

    @Column(name = "first_name", insertable = false, updatable = false)
    private String firstName;

    @Column(name = "middle_name", insertable = false, updatable = false)
    private String middleName;

    @Column(name = "last_name", insertable = false, updatable = false)
    private String lastName;

    @Column(name = "datetime", insertable = false, updatable = false)
    private Instant date;

    @Column(name = "type_id", insertable = false, updatable = false)
    private Long typeId;

    @Column(name = "type_name", insertable = false, updatable = false)
    private String typeName;

    @Column(name = "type_title", insertable = false, updatable = false)
    private String typeTitle;

    @Column(name = "sub_type_id", insertable = false, updatable = false)
    private Long subTypeId;

    @Column(name = "sub_type_title", insertable = false, updatable = false)
    private String subTypeTitle;

    @Column(name = "sub_type_name", insertable = false, updatable = false)
    private String subTypeName;

    @Column(name = "is_er_visit", insertable = false, updatable = false)
    private Boolean isErVisit;

    @JoinColumn(name = "event_id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private Event event;

    @Column(name = "event_id", insertable = false, updatable = false)
    private Long eventId;

    @JoinColumn(name = "note_id", referencedColumnName = "id", insertable = false, updatable = false)
    @OneToOne(fetch = FetchType.LAZY)
    private Note note;

    @Column(name = "note_id", insertable = false, updatable = false)
    private Long noteId;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(name = "EventOrNote_Resident",
            joinColumns = {@JoinColumn(name = "item_type_id", referencedColumnName = "item_type_id"), @JoinColumn(name = "numeric_id", referencedColumnName = "numeric_id")},
            inverseJoinColumns = @JoinColumn(name = "resident_id"))
    private List<Client> clients;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public Long getNumericId() {
        return numericId;
    }

    public void setNumericId(Long numericId) {
        this.numericId = numericId;
    }

    public Long getItemTypeId() {
        return itemTypeId;
    }

    public void setItemTypeId(Long itemTypeId) {
        this.itemTypeId = itemTypeId;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getTypeId() {
        return typeId;
    }

    public void setTypeId(Long typeId) {
        this.typeId = typeId;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeTitle() {
        return typeTitle;
    }

    public void setTypeTitle(String typeTitle) {
        this.typeTitle = typeTitle;
    }

    public String getSubTypeName() {
        return subTypeName;
    }

    public void setSubTypeName(String subTypeName) {
        this.subTypeName = subTypeName;
    }

    public Long getSubTypeId() {
        return subTypeId;
    }

    public void setSubTypeId(Long subTypeId) {
        this.subTypeId = subTypeId;
    }

    public String getSubTypeTitle() {
        return subTypeTitle;
    }

    public void setSubTypeTitle(String subTypeTitle) {
        this.subTypeTitle = subTypeTitle;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public Boolean isErVisit() {
        return isErVisit;
    }

    public void setErVisit(Boolean isErVisit) {
        this.isErVisit = isErVisit;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Note getNote() {
        return note;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public Long getEventId() {
        return eventId;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }

    public String getFullName() {
        return Stream.of(getFirstName(), getMiddleName(), getLastName()).filter(StringUtils::isNotEmpty)
                .collect(Collectors.joining(" "));
    }

    public List<Client> getClients() {
        return clients;
    }

    public void setClients(List<Client> clients) {
        this.clients = clients;
    }
}
