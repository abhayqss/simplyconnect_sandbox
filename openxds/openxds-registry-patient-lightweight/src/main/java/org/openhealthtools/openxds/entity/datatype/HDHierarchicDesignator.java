package org.openhealthtools.openxds.entity.datatype;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "HD_HierarchicDesignator")
public class HDHierarchicDesignator implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "namespace_id")
    private String namespaceID;

    @Column(name = "universal_id")
    private String universalID;

    @Column(name = "universal_id_type")
    private String universalIDType;

    public HDHierarchicDesignator(String namespaceID, String universalID, String universalIDType) {
        this.namespaceID = namespaceID;
        this.universalID = universalID;
        this.universalIDType = universalIDType;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNamespaceID() {
        return namespaceID;
    }

    public void setNamespaceID(String namespaceID) {
        this.namespaceID = namespaceID;
    }

    public String getUniversalID() {
        return universalID;
    }

    public void setUniversalID(String universalID) {
        this.universalID = universalID;
    }

    public String getUniversalIDType() {
        return universalIDType;
    }

    public void setUniversalIDType(String universalIDType) {
        this.universalIDType = universalIDType;
    }
}
