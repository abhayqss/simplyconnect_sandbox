package org.openhealthtools.openxds.registry;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v231.datatype.CE;
import ca.uhn.hl7v2.model.v231.datatype.XTN;
import ca.uhn.hl7v2.model.v231.group.ADT_A39_PIDPD1MRGPV1;
import ca.uhn.hl7v2.model.v231.message.ADT_A39;
import ca.uhn.hl7v2.model.v231.segment.MSH;
import ca.uhn.hl7v2.model.v231.segment.PID;
import com.misyshealthcare.connect.base.SharedEnums;
import com.misyshealthcare.connect.base.demographicdata.PhoneNumber;
import com.misyshealthcare.connect.net.IConnectionDescription;
import org.openhealthexchange.openpixpdq.ihe.impl_v2.hl7.HL7v231ToBaseConvertor;
import org.openhealthtools.openxds.XdsFactory;
import org.openhealthtools.openxds.registry.patient.parser.datatype.DataTypeService;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by averazub on 11/28/2016.
 */
public class HL7V231ConverterCustom extends HL7v231ToBaseConvertor {

    private PID pid;

    private static final DataTypeService dataTypeService = XdsFactory.getInstance().getBean(DataTypeService.class);

    public HL7V231ConverterCustom(Message in, IConnectionDescription connection) {
        super(in, connection);
        try {
            MSH msh = (MSH)in.get("MSH");
            if(in instanceof ADT_A39) {
                ADT_A39_PIDPD1MRGPV1 pidpd1mrgpv1 = (ADT_A39_PIDPD1MRGPV1)in.get("PIDPD1MRGPV1");
                this.pid = (PID)pidpd1mrgpv1.get("PID");
            } else {
                this.pid = (PID)in.get("PID");
            }
        } catch (HL7Exception var12) {
            throw new ExceptionInInitializerError(var12);
        }


    }

    public String getRaceIdentifier() throws HL7Exception {
        return getCEIdentifier(this.pid.getRace(0));
    }

    public String getPrimaryLanguageIdentifier() {
        return getCEIdentifier(this.pid.getPrimaryLanguage());
    }

    public String getMartialStatusIdentifier() {
        return getCEIdentifier(this.pid.getMaritalStatus());
    }

    public String getReligionIdentifier() {
        return getCEIdentifier(this.pid.getReligion());
    }

    public String getEthnicGroupIdentifier() throws HL7Exception {
        return getCEIdentifier(this.pid.getEthnicGroup(0));
    }

    public String getCitizenShipIdentifier() throws HL7Exception {
        return getCEIdentifier(this.pid.getCitizenship(0));
    }

    public String getCitizenShip() throws HL7Exception {
        return getCEName(this.pid.getCitizenship(0));
    }

    public String getVeteranMilitaryStatusIdentifier() throws HL7Exception  {
        return getCEIdentifier(this.pid.getVeteransMilitaryStatus());
    }


    public String getVeteranMilitaryStatus() throws HL7Exception  {
        return getCEName(this.pid.getVeteransMilitaryStatus());
    }

    public String getNationalityIdentifier()  throws HL7Exception {
        return getCEIdentifier(this.pid.getNationality());
    }


    public String getNationality()  throws HL7Exception {
        return getCEName(this.pid.getNationality());
    }

    protected String getCEIdentifier(CE element) {
        return element.getIdentifier().getValue();
    }
    public String getCEName(CE element) throws HL7Exception {
        return element.getText().getValue();
    }



    public List<PhoneNumber> getPhoneList() {
        ArrayList phoneList = new ArrayList();

        PhoneNumber e;

        if ((this.pid.getPhoneNumberHome()!=null) && (this.pid.getPhoneNumberHome().length!=0)) {
            for (XTN phoneNumberHome: this.pid.getPhoneNumberHome()) {
                e = this._getPhoneNumber(phoneNumberHome);
                if (e != null) {
                    e.setType(SharedEnums.PhoneType.HOME);
                    phoneList.add(e);
                }
            }
        }


        if ((this.pid.getPhoneNumberBusiness()!=null) && (this.pid.getPhoneNumberBusiness().length!=0)) {
            for (XTN phoneNumberBusiness: this.pid.getPhoneNumberBusiness()) {
                e = this._getPhoneNumber(phoneNumberBusiness);
                if (e != null) {
                    e.setType(SharedEnums.PhoneType.WORK);
                    phoneList.add(e);
                }
            }
        }

        return phoneList;
    }

    public List<PhoneNumber> getEmails() {
        ArrayList<PhoneNumber> phoneList = new ArrayList<PhoneNumber>();

        if (this.pid.getPhoneNumberHome()!=null) {
            for (XTN phoneNumber: this.pid.getPhoneNumberHome()) {
                PhoneNumber num = new PhoneNumber();
                num.setType(SharedEnums.PhoneType.HOME);
                num.setEmail(phoneNumber.getEmailAddress().getValue());
                phoneList.add(num);
            }
        }

        if (this.pid.getPhoneNumberBusiness()!=null) {
            for (XTN phoneNumber: this.pid.getPhoneNumberBusiness()) {
                PhoneNumber num = new PhoneNumber();
                num.setType(SharedEnums.PhoneType.WORK);
                num.setEmail(phoneNumber.getEmailAddress().getValue());
                phoneList.add(num);
            }
        }


        return phoneList;
    }

    private PhoneNumber _getPhoneNumber(XTN xtn) {
        PhoneNumber number = new PhoneNumber();
        String sNum = xtn.get9999999X99999CAnyText().getValue();
        if(sNum == null) {
            return null;
        } else {
            number.setAreaCode(this._parseAreaCode(sNum));
            number.setExtension(this._parseExtension(sNum));
            number.setNote(this._parseNote(sNum));
            number.setNumber(this._parseNumber(sNum));
            return number;
        }
    }

    private String _parseNumber(String sNum) {
        int sIndex = sNum.indexOf(")");
        return sIndex < 0?sNum.substring(0, 8):sNum.substring(sIndex + 1, sIndex + 9);
    }

    private String _parseNote(String sNum) {
        int sIndex = sNum.indexOf("C");
        return sIndex < 0?null:sNum.substring(sIndex + 1);
    }

    private String _parseExtension(String sNum) {
        int sIndex = sNum.indexOf("X");
        if(sIndex < 0) {
            return null;
        } else {
            int eIndex = sNum.indexOf("C");
            return eIndex < 0?sNum.substring(sIndex + 1):sNum.substring(sIndex + 1, eIndex);
        }
    }

    private String _parseAreaCode(String sNum) {
        int sIndex = sNum.indexOf("(");
        return sIndex < 0?null:sNum.substring(sIndex + 1, sIndex + 4);
    }

    @Override
    public Calendar getDeathDate() {
        Date dt = dataTypeService.convertTsToDate(this.pid.getPatientDeathDateAndTime());
        if (dt==null) {
            return null;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(dt);
        return cal;
    }

    @Override
    public Boolean getDeathIndicator() {
        String deathIndicator = this.pid.getPatientDeathIndicator().getValue();
        if (deathIndicator != null)
        {
            if (deathIndicator.equals("1")) {
                return true;
            }
            else return Boolean.valueOf(deathIndicator);
        }
        return false;
    }


}
