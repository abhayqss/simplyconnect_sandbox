package com.scnsoft.eldermark.entity.hl7;

import ca.uhn.hl7v2.model.DataTypeException;
import ca.uhn.hl7v2.model.v25.segment.MSH;
import ca.uhn.hl7v2.model.v25.segment.OBR;
import ca.uhn.hl7v2.model.v25.segment.OBX;
import ca.uhn.hl7v2.model.v25.segment.PID;
import com.scnsoft.eldermark.services.hl7.util.Hl7Utils;
import com.scnsoft.eldermark.services.hl7.util.MessageProperty;
import org.apache.commons.lang3.StringUtils;

import java.util.Date;

public class Hl7Message implements PatientOrganizationAware {

    private MSH msh;
    private PID pid;
    private OBR obr;
    private OBX obx;

    public Hl7Message(MSH msh, PID pid, OBR obr, OBX obx) {
        this.msh = msh;
        this.obx = obx;
        this.obr = obr;
        this.pid = pid;
    }

    @Override
    public String getPatientOid() {
        return getMshTargetOid();
    }

    @Override
    public String getPatientFirstName() {
        return getPidPatientGivenName();
    }

    @Override
    public String getPatientLastName() {
        return getPidPatientFamilyName();
    }

    public String getMshSendingOid() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return msh.getSendingFacility().getUniversalID().toString();
            }
        });
    }

    public String getMshTargetOid() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return msh.getReceivingFacility().getUniversalID().toString();
            }
        });
    }

    public Date getMshDateTimeOfMessage() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<Date>() {
            @Override
            public Date getProperty() {
                try {
                    return msh.getDateTimeOfMessage().getTs1_Time().getValueAsDate();
                } catch (DataTypeException e) {
                    return null;
                }
            }
        });
    }

    public String getMshMessageType() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return msh.getMessageType().toString();
            }
        });
    }

    public String getMshMessageControlId() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return msh.getMessageControlID().toString();
            }
        });
    }

    public String getMshProcessingId(){
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return msh.getProcessingID().toString();
            }
        });
    }

    public String getMshVersionId() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return msh.getVersionID().getVersionID().toString();
            }
        });
    }

    public String getMshAcceptAcknowledgmentType(){
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return msh.getAcceptAcknowledgmentType().toString();
            }
        });
    }

    public String getMshApplicationAcknowledgmentType(){
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return msh.getApplicationAcknowledgmentType().toString();
            }
        });
    }

    public String getMshMessageProfileIdentifier(){
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return msh.getMessageProfileIdentifier(0).toString();
            }
        });
    }

    public String getPidSetId() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return pid.getSetIDPID().toString();
            }
        });
    }

    public String getPidPatientIdentifierIdNumber() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return pid.getPatientIdentifierList(0).getCx1_IDNumber().toString();
            }
        });
    }

    public String getPidPatientIdentifierAssigningAuthority(){
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return pid.getPatientIdentifierList(0).getCx4_AssigningAuthority().toString();
            }
        });
    }

    public String getPidPatientIdentifierTypeCode(){
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return pid.getPatientIdentifierList(0).getCx5_IdentifierTypeCode().toString();
            }
        });
    }

    public String getPidPatientGivenName(){
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return pid.getPatientName(0).getGivenName().toString();
            }
        });
    }

    public String getPidPatientFamilyName(){
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return pid.getPatientName(0).getFamilyName().getFn1_Surname().toString();
            }
        });
    }

    public Date getPidDateTimeOfBirth() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<Date>() {
            @Override
            public Date getProperty() {
                try {
                    return pid.getDateTimeOfBirth().getTs1_Time().getValueAsDate();
                } catch (DataTypeException e) {
                    return null;
                }
            }
        });
    }

    public String getPidAdministrativeSex(){
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return pid.getAdministrativeSex().toString();
            }
        });
    }

    public String getPidRace(){
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return pid.getRace(0).getCe2_Text().toString();
            }
        });
    }

    public String getPidPatientCity(){
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return pid.getPatientAddress(0).getCity().toString();
            }
        });
    }

    public String getPidPatientStreetAddress(){
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return pid.getPatientAddress(0).getStreetAddress().getSad1_StreetOrMailingAddress().toString();
            }
        });
    }

    public String getPidPatientStateOrProvince(){
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return pid.getPatientAddress(0).getStateOrProvince().toString();
            }
        });
    }

    public String getPidPhoneNumber(){
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                String localNumber = pid.getPhoneNumberHome(0).getLocalNumber().toString();
                String areaCityCode = pid.getPhoneNumberHome(0).getAreaCityCode().toString();
                String countryCode = pid.getPhoneNumberHome(0).getCountryCode().toString();
                StringBuilder fullNumber = new StringBuilder();
                if (!StringUtils.isEmpty(countryCode)) {
                    fullNumber.append(countryCode);
                }
                if (!StringUtils.isEmpty(areaCityCode)) {
                    fullNumber.append(areaCityCode);
                }
                if (!StringUtils.isEmpty(localNumber)) {
                    fullNumber.append(localNumber);
                }
                return fullNumber.toString();
            }
        });
    }

    public String getObrSetId() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return obr.getSetIDOBR().toString();
            }
        });
    }

    public String getObrPlacerOrderNumberUniversalId() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return obr.getPlacerOrderNumber().getUniversalID().toString();
            }
        });
    }

    public String getObrPlacerOrderNumberEntityIdentifier() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return obr.getPlacerOrderNumber().getEntityIdentifier().toString();
            }
        });
    }

    public String getObrPlacerOrderNumberUniversalIdType() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return obr.getPlacerOrderNumber().getUniversalIDType().toString();
            }
        });
    }

    public String getObrPlacerOrderNumberNamespaceId() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return obr.getPlacerOrderNumber().getNamespaceID().toString();
            }
        });
    }

    public String getObrFillerOrderNumberUniversalId() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return obr.getFillerOrderNumber().getUniversalID().toString();
            }
        });
    }

    public String getObrFillerOrderNumberEntityIdentifier() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return obr.getFillerOrderNumber().getEntityIdentifier().toString();
            }
        });
    }

    public String getObrFillerOrderNumberUniversalIdType() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return obr.getFillerOrderNumber().getUniversalIDType().toString();
            }
        });
    }

    public String getObrFillerOrderNumberNamespaceId() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return obr.getFillerOrderNumber().getNamespaceID().toString();
            }
        });
    }

    public String getObrUniversalServiceIdentifier() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return obr.getUniversalServiceIdentifier().getCe1_Identifier().toString();
            }
        });
    }

    public String getObrUniversalServiceIdentifierText() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return obr.getUniversalServiceIdentifier().getCe2_Text().toString();
            }
        });
    }

    public String getObrUniversalServiceIdentifierNameOfCodingSystem() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return obr.getUniversalServiceIdentifier().getCe3_NameOfCodingSystem().toString();
            }
        });
    }

    public Date getObrObservationDateTime() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<Date>() {
            @Override
            public Date getProperty() {
                try {
                    return obr.getObservationDateTime().getTs1_Time().getValueAsDate();
                } catch (DataTypeException e) {
                    return null;
                }
            }
        });
    }

    public Date getObrSpecimenReceivedDateTime() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<Date>() {
            @Override
            public Date getProperty() {
                try {
                    return obr.getSpecimenReceivedDateTime().getTs1_Time().getValueAsDate();
                } catch (DataTypeException e) {
                    return null;
                }
            }
        });
    }

    public String getObrOrderingProviderFirstName() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return obr.getOrderingProvider(0).getGivenName().toString();
            }
        });
    }

    public String getObrOrderingProviderLastName() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return obr.getOrderingProvider(0).getFamilyName().getFn1_Surname().toString();
            }
        });
    }

    public String getObrOrderingProviderNamespaceId() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return obr.getOrderingProvider(0).getAssigningAuthority().getNamespaceID().toString();
            }
        });
    }

    public String getObrOrderingProviderUniversalIdType() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return obr.getOrderingProvider(0).getAssigningAuthority().getUniversalIDType().toString();
            }
        });
    }

    public String getObrOrderingProviderUniversalId() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return obr.getOrderingProvider(0).getAssigningAuthority().getUniversalID().toString();
            }
        });
    }

    public Date getObrResultsRptStatusChngDateTime() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<Date>() {
            @Override
            public Date getProperty() {
                try {
                    return obr.getResultsRptStatusChngDateTime().getTs1_Time().getValueAsDate();
                } catch (DataTypeException e) {
                    return null;
                }
            }
        });
    }

    public String getObrDiagnosticServSectId() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return obr.getDiagnosticServSectID().toString();
            }
        });
    }

    public String getObrResultStatus() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return obr.getResultStatus().toString();
            }
        });
    }

    public String getObxSetId() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return obx.getSetIDOBX().toString();
            }
        });
    }

    public String getObxValueType() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return obx.getValueType().toString();
            }
        });
    }

    public String getObxObservationIdentifier() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return obx.getObservationIdentifier().toString();
            }
        });
    }

    public String getObxObservationValue() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return obx.getObservationValue(0).getData().toString();
            }
        });
    }

    public String getObxUnits() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return obx.getUnits().getCe1_Identifier().getValue();
            }
        });
    }

    public String getObxUnitsNameOfCodingSystem() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return obx.getUnits().getCe3_NameOfCodingSystem().getValue();
            }
        });
    }

    public String getObxReferencesRange() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return obx.getReferencesRange().getValue();
            }
        });
    }

    public String getObxObservationResultStatus() {
        return Hl7Utils.getNullCheckedResult(new MessageProperty<String>() {
            @Override
            public String getProperty() {
                return obx.getObservationResultStatus().getValue();
            }
        });
    }
}
