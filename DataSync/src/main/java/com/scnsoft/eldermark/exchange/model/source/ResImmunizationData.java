package com.scnsoft.eldermark.exchange.model.source;

import com.scnsoft.eldermark.framework.model.source.IdentifiableSourceEntity;
import com.scnsoft.eldermark.framework.orm.Column;
import com.scnsoft.eldermark.framework.orm.Id;
import com.scnsoft.eldermark.framework.orm.Table;

import java.util.Date;

@Table(ResImmunizationData.TABLE_NAME)
public class ResImmunizationData extends IdentifiableSourceEntity<String> {
    public static final String TABLE_NAME = "Res_Immunization";
    public static final String ID = "UUID";
    public static final String ROUTE_CCDID = "Route_CCDID";
    public static final String VACCINE_CCDID = "Vaccine_CCDID";
    public static final String INJECTION_SITE_CCDID = "Injection_Site_CCDID";

    @Id
    @Column(ID)
    private String id;

    @Column("Res_Number")
    private Long resNumber;

    @Column("Date_Received")
    private Date dateReceived;

    @Column("Vaccine_Name")
    private String vaccineName;

    @Column(VACCINE_CCDID)
    private Long vaccineId;

    @Column("Route")
    private String route;

    @Column(ROUTE_CCDID)
    private Long routeId;

    @Column("Injection_Site")
    private String injectionSite;

    @Column(INJECTION_SITE_CCDID)
    private Long injectionSiteId;

    @Column("Vaccine_Lot_Number")
    private String vaccineLotNumber;

    @Column("Adverse_Reaction")
    private String adverseReaction;

    @Column("Manufacturer_Name")
    private String manufacturerName;

    @Column("Administered_By")
    private String administeredBy;

    @Override
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Long getResNumber() {
        return resNumber;
    }

    public void setResNumber(Long resNumber) {
        this.resNumber = resNumber;
    }

    public Date getDateReceived() {
        return dateReceived;
    }

    public void setDateReceived(Date dateReceived) {
        this.dateReceived = dateReceived;
    }

    public String getVaccineName() {
        return vaccineName;
    }

    public void setVaccineName(String vaccineName) {
        this.vaccineName = vaccineName;
    }

    public Long getVaccineId() {
        return vaccineId;
    }

    public void setVaccineId(Long vaccineId) {
        this.vaccineId = vaccineId;
    }

    public String getRoute() {
        return route;
    }

    public void setRoute(String route) {
        this.route = route;
    }

    public Long getRouteId() {
        return routeId;
    }

    public void setRouteId(Long routeId) {
        this.routeId = routeId;
    }

    public String getInjectionSite() {
        return injectionSite;
    }

    public void setInjectionSite(String injectionSite) {
        this.injectionSite = injectionSite;
    }

    public Long getInjectionSiteId() {
        return injectionSiteId;
    }

    public void setInjectionSiteId(Long injectionSiteId) {
        this.injectionSiteId = injectionSiteId;
    }

    public String getVaccineLotNumber() {
        return vaccineLotNumber;
    }

    public void setVaccineLotNumber(String vaccineLotNumber) {
        this.vaccineLotNumber = vaccineLotNumber;
    }

    public String getAdverseReaction() {
        return adverseReaction;
    }

    public void setAdverseReaction(String adverseReaction) {
        this.adverseReaction = adverseReaction;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public void setManufacturerName(String manufacturerName) {
        this.manufacturerName = manufacturerName;
    }

    public String getAdministeredBy() {
        return administeredBy;
    }

    public void setAdministeredBy(String administeredBy) {
        this.administeredBy = administeredBy;
    }
}
