package com.scnsoft.eldermark.entity.xds.segment;

import com.scnsoft.eldermark.entity.xds.datatype.IDCodedValueForHL7Tables;
import com.scnsoft.eldermark.entity.xds.hl7table.HL7CodeTable0105SourceOfComment;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "NTE_NotesAndComments")
public class NTENotesAndComments {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "set_id")
    private String setId;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "source_of_comment_id")
    private IDCodedValueForHL7Tables<HL7CodeTable0105SourceOfComment> sourceOfComment;

    @ElementCollection
    @CollectionTable(name = "NTE_NotesAndComments_comment", joinColumns = @JoinColumn(name = "nte_id"))
    @Column(name = "comment")
    private List<String> comments;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSetId() {
        return setId;
    }

    public void setSetId(String setId) {
        this.setId = setId;
    }

    public IDCodedValueForHL7Tables<HL7CodeTable0105SourceOfComment> getSourceOfComment() {
        return sourceOfComment;
    }

    public void setSourceOfComment(IDCodedValueForHL7Tables<HL7CodeTable0105SourceOfComment> sourceOfComment) {
        this.sourceOfComment = sourceOfComment;
    }

    public List<String> getComments() {
        return comments;
    }

    public void setComments(List<String> comments) {
        this.comments = comments;
    }
}
